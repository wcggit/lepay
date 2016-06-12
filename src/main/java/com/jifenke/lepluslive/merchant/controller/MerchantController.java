package com.jifenke.lepluslive.merchant.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.global.util.PaginationUtil;
import com.jifenke.lepluslive.merchant.controller.dto.MerchantDto;
import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantDetail;
import com.jifenke.lepluslive.merchant.service.MerchantService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Created by wcg on 16/3/17.
 */
@RestController
@RequestMapping("/lepay")
public class MerchantController {

  @Inject
  private MerchantService merchantService;

  //分页
  @RequestMapping(value = "/merchant/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public
  @ResponseBody
  List<Merchant> findPageProduct(
      @RequestParam(value = "page", required = false) Integer offset) {
    return merchantService.findMerchantsByPage(offset);
  }

  @ApiOperation(value = "首页加载商家列表及周边")
  @RequestMapping(value = "/reload", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult reload(
      @ApiParam(value = "经度(保留六位小数)") @RequestParam(required = false) Double longitude,
      @ApiParam(value = "纬度(保留六位小数)") @RequestParam(required = false) Double latitude,
      @ApiParam(value = "第几页") @RequestParam(required = false) Integer page,
      @ApiParam(value = "商家类别") @RequestParam(required = false) Long type,
      @ApiParam(value = "某个城市的区域id") @RequestParam(required = false) Long areaId) {

    List<MerchantDto>
        merchantDtoList =
        merchantService.findMerchantListByCustomCondition(latitude, longitude, page, type, areaId);

    return LejiaResult.build(200, "ok", merchantDtoList);
  }


}
