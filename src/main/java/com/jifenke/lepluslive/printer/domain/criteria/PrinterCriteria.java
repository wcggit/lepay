package com.jifenke.lepluslive.printer.domain.criteria;

/**
 * Created by lss on 2016/10/9.
 */
public class PrinterCriteria {

    private Integer offset;

    private Long id;

    private String machineCode;

    private String name;

    private String merchant;

    private String merchantUser;

    private Integer state;

    private String createDate;

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

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getMerchantUser() {
        return merchantUser;
    }

    public void setMerchantUser(String merchantUser) {
        this.merchantUser = merchantUser;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }


}
