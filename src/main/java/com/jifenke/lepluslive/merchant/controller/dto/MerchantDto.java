package com.jifenke.lepluslive.merchant.controller.dto;


import com.jifenke.lepluslive.merchant.domain.entities.MerchantDetail;

import javax.validation.constraints.AssertFalse;
import java.util.List;

/**
 * Created by zhangwen on 2016/4/26.
 */
public class MerchantDto {

    private Long id;

    private int sid;

    private String location;

    private String phoneNumber;

    private String name;

    private String picture;

    private Integer discount; //折扣

    private Integer rebate;  //返利

    private String distance;

    private Double lng;

    private Double lat;  //纬度

    private List<MerchantDetail> detailList;

    public List<MerchantDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<MerchantDetail> detailList) {
        this.detailList = detailList;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getRebate() {
        return rebate;
    }

    public void setRebate(Integer rebate) {
        this.rebate = rebate;
    }

}
