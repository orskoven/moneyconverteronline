package com.example.moneyconverteronline.models;

public class ValutaModel {

    String code;
    String desc;
    double rate;

    public ValutaModel(String code, String desc, double rate) {
        this.code = code;
        this.desc = desc;
        this.rate = rate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return
                code +
                " " + desc +
                " " + rate;
    }
}
