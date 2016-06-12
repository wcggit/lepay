package com.jifenke.lepluslive.merchant.service;

import com.jifenke.lepluslive.merchant.controller.dto.MerchantDto;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantDetail;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantWallet;
import com.jifenke.lepluslive.merchant.repository.MerchantDetailRepository;
import com.jifenke.lepluslive.merchant.repository.MerchantRepository;
import com.jifenke.lepluslive.merchant.repository.MerchantUserRepository;
import com.jifenke.lepluslive.merchant.repository.MerchantWalletRepository;
import com.jifenke.lepluslive.order.domain.entities.OffLineOrder;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 * Created by wcg on 16/3/17.
 */
@Service
@Transactional(readOnly = true)
public class MerchantService {

  @Inject
  private MerchantRepository merchantRepository;

  @Inject
  private MerchantDetailRepository merchantDetailRepository;

  @Inject
  private MerchantUserRepository merchantUserRepository;

  @Inject
  private EntityManagerFactory entityManagerFactory;

  @Inject
  private MerchantWalletRepository merchantWalletRepository;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<Merchant> findMerchantsByPage(Integer offset) {
    if (offset == null) {
      offset = 1;
    }
    return merchantRepository.findAll(
        new PageRequest(offset - 1, 10, new Sort(Sort.Direction.ASC, "sid"))).getContent();
  }

  /**
   * 获取商家详情
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Optional<Merchant> findMerchantBySId(String sid) {
    return merchantRepository.findByMerchantSid(sid);
  }

  /**
   * 获取商家详情
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Merchant findMerchantById(Long id) {
    return merchantRepository.findOne(id);
  }

  /**
   * 获取商家轮播图
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<MerchantDetail> findAllMerchantDetailByMerchant(Merchant merchant) {
    return merchantDetailRepository.findAllByMerchant(merchant);
  }

  /**
   * 按照距离远近对商家排序  以后可以被findMerchantListByCustomCondition取代 open app 暂时使用
   *
   * @param latitude  经度
   * @param longitude 纬度
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<MerchantDto> findOrderByDistance(Double latitude, Double longitude) {

    List<MerchantDto> dtoList = new ArrayList<>();
    List<Object[]>
        list =
        merchantRepository.findOrderByDistance(latitude, longitude, 0, 10);
    for (Object[] o : list) {
      MerchantDto merchantDto = new MerchantDto();
      merchantDto.setId(Long.parseLong(o[0].toString()));
      merchantDto.setSid(Integer.parseInt(o[1].toString()));
      merchantDto.setLocation(o[2].toString());
      merchantDto.setPhoneNumber(o[3].toString());
      merchantDto.setName(o[4].toString());
      merchantDto.setPicture(o[5].toString());
      merchantDto.setDiscount(Integer.parseInt(o[6].toString()));
      merchantDto.setRebate(Integer.parseInt(o[7].toString()));
      merchantDto.setLng(Double.parseDouble(o[8].toString()));
      merchantDto.setLat(Double.parseDouble(o[9].toString()));
      merchantDto.setDistance(o[10].toString());
      dtoList.add(merchantDto);
    }
    return dtoList;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<MerchantDto> findMerchantListByCustomCondition(Double latitude, Double longitude,
                                                             Integer page, Long type, Long areaId) {
    if (page == null) {
      page = 1;
    }

    EntityManager em = entityManagerFactory.createEntityManager();
    //定义SQL
    String sql = null;

    sql =
        "SELECT m.id,m.sid,m.location,m.phone_number,m.`name`,m.picture,m.discount,m.rebate,m.lng,m.lat, ROUND( 6378.138 * 2 * ASIN(SQRT(POW(SIN(("
        + latitude + " * PI() / 180 - m.lat * PI() / 180) / 2),2) + COS(" + latitude
        + " * PI() / 180) * COS(m.lat * PI() / 180) * POW(SIN((" + longitude
        + " * PI() / 180 - m.lng * PI() / 180) / 2),2))) * 1000) AS distance FROM merchant m WHERE 1=1";

    if (type != null) {
      sql += " AND m.merchant_type_id = " + type;
    }

    if (areaId != null) {
      sql += " AND m.area_id = " + areaId;
      sql += " ORDER BY sid LIMIT " + (page - 1) + "," + 10 + "";
    } else if (latitude != null) {
      sql += " ORDER BY distance LIMIT " + (page - 1) + "," + 10 + "";
    } else {
      sql += " ORDER BY sid LIMIT " + (page - 1) * 10 + "," + 10 + "";
    }

    //创建原生SQL查询QUERY实例
    Query query = em.createNativeQuery(sql);

    List<Object[]> list = query.getResultList();

    List<MerchantDto> dtoList = new ArrayList<>();
    for (Object[] o : list) {
      MerchantDto merchantDto = new MerchantDto();
      merchantDto.setId(Long.parseLong(o[0].toString()));
      merchantDto.setSid(Integer.parseInt(o[1].toString()));
      merchantDto.setLocation(o[2].toString());
      merchantDto.setPhoneNumber(o[3].toString());
      merchantDto.setName(o[4].toString());
      merchantDto.setPicture(o[5].toString());
      merchantDto.setDiscount(Integer.parseInt(o[6].toString()));
      merchantDto.setRebate(Integer.parseInt(o[7].toString()));
      merchantDto.setLng(Double.parseDouble(o[8].toString()));
      merchantDto.setLat(Double.parseDouble(o[9].toString()));
      merchantDto.setDistance(o[10] != null ? o[10].toString() : null);
      dtoList.add(merchantDto);
    }
    return dtoList;
  }

  public MerchantWallet findMerchantWalletByMerchant(Merchant merchant) {
    return merchantWalletRepository.findByMerchant(merchant);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void paySuccess(OffLineOrder offLineOrder) {
    MerchantWallet
        merchantWallet =
        findMerchantWalletByMerchant(offLineOrder.getMerchant());
    merchantWallet
        .setTransfersMoney(merchantWallet.getTransfersMoney() + offLineOrder.getTransferMoney());
    merchantWallet.setTotalTransferMoney(
        merchantWallet.getTotalTransferMoney() + offLineOrder.getTransferMoney());
    merchantWalletRepository.save(merchantWallet);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public List<MerchantUser> findMerchantUserByMerchant(Merchant merchant) {
    return merchantUserRepository.findAllByMerchant(merchant);
  }
}
