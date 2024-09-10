package com.example.sortifyandroidapp.Models;

import java.io.Serializable;

public class Form  implements Serializable {

    public Integer formId;
    public Integer userId;
    public Integer typeId;
    public String productName;
    public String barcode;
    public String productImage;
    public String productdetails;
    public Integer seen;
    public String date;

    public Form(Integer typeId, String productName, String barcode, String productImage, String productdetails) {
        this.typeId = typeId;
        this.productName = productName;
        this.barcode = barcode;
        this.productImage = productImage;
        this.productdetails = productdetails;
    }

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductdetails() {
        return productdetails;
    }

    public void setProductdetails(String productdetails) {
        this.productdetails = productdetails;
    }

    public Integer getSeen() {
        return seen;
    }

    public void setSeen(Integer seen) {
        this.seen = seen;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
