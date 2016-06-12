package com.jifenke.lepluslive.lejiauser.service;


import com.jifenke.lepluslive.global.util.MD5Util;
import com.jifenke.lepluslive.lejiauser.domain.entities.LeJiaUser;
import com.jifenke.lepluslive.lejiauser.repository.LeJiaUserRepository;

import com.jifenke.lepluslive.score.repository.ScoreARepository;
import com.jifenke.lepluslive.score.repository.ScoreBRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by wcg on 16/4/21.
 */
@Service
@Transactional(readOnly = true)
public class LeJiaUserService {

    @Value("${bucket.ossBarCodeReadRoot}")
    private String barCodeRootUrl;

    @Inject
    private LeJiaUserRepository leJiaUserRepository;

    @Inject
    private BarcodeService barcodeService;


    @Inject
    private ScoreARepository scoreARepository;

    @Inject
    private ScoreBRepository scoreBRepository;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public LeJiaUser findUserByUserSid(String userSid) {
        return leJiaUserRepository.findByUserSid(userSid);
    }

    /**
     * 判断该手机号是否已经注册
     *
     * @param phoneNumber
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public LeJiaUser findUserByPhoneNumber(String phoneNumber) {
        return leJiaUserRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * 设置密码
     *
     * @param pwd 加密前密码
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void setPwd(LeJiaUser leJiaUser, String pwd) {
        String md5Pwd = MD5Util.MD5Encode(pwd, null);
        leJiaUser.setPwd(md5Pwd);
        leJiaUserRepository.save(leJiaUser);
    }


    /**
     * 登录
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public LeJiaUser login(String phoneNumber, String pwd, String token) {

        LeJiaUser leJiaUser = leJiaUserRepository.findByPhoneNumber(phoneNumber);
        if (!leJiaUser.getPwd().equals(MD5Util.MD5Encode(pwd, null))) {
            return null;
        }
        if (token != null && (!token.equals(leJiaUser.getToken()))) { //更新推送token
            leJiaUser.setToken(token);
            leJiaUserRepository.save(leJiaUser);
        }
        return leJiaUser;
    }

}
