package com.fujisoft.campaign.bean;

/**
 * Created by 860116021 on 2017/2/6.
 * 地址bean
 */

public class AddressBean {

    private String id;          //地址id
    private String name;        //收件人姓名
    private String phone;       //收件人手机号码
    private String province;    //省
    private String city;        //市
    private String region;      //区
    private String street;      //街道
    private String isDefault;   //是否默认地址（1：是；0：否）

    public AddressBean(String id, String name, String phone, String province, String city, String region, String
            street, String isDefault) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.region = region;
        this.street = street;
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}
