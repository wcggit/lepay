package com.jifenke.lepluslive.printer.domain.entities;

import com.jifenke.lepluslive.merchant.domain.entities.Merchant;
import com.jifenke.lepluslive.merchant.domain.entities.MerchantUser;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lss on 16-12-22.
 */
@Entity
@Table(name = "Printer")
public class Printer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String machineCode;

    private String mKey;

    private String name;

    private Integer printerCount;

    @ManyToOne
    private Merchant merchant;

    @ManyToOne
    private MerchantUser merchantUser;

    private Integer state;

    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public MerchantUser getMerchantUser() {
        return merchantUser;
    }

    public void setMerchantUser(MerchantUser merchantUser) {
        this.merchantUser = merchantUser;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public Integer getPrinterCount() {
        return printerCount;
    }

    public void setPrinterCount(Integer printerCount) {
        this.printerCount = printerCount;
    }
}
