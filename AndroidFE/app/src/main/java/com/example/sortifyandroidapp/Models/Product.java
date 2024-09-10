package com.example.sortifyandroidapp.Models;

import java.io.Serializable;

public class Product extends Throwable implements Serializable {

    public Integer productId;
    public String productName;
    public String barcode;
    public String image;
    public String details;
    public Integer typeId;

    public Product(String productName, String barcode, String image, Integer typeId, String details) {
        this.productName = productName;
        this.barcode = barcode;
        this.image = image;
        this.details = details;
        this.typeId = typeId;
    }

    public Product(Integer productId, String productName, String barcode, String image, Integer typeId, String details) {
        this.productName = productName;
        this.barcode = barcode;
        this.image = image;
        this.details = details;
        this.typeId = typeId;
        this.productId = productId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /*
        public Product(String productName, String barcode, String image, String details) {
            this.productName = productName;
            this.barcode = barcode;
            this.image = image;
            this.details = details;
        }
    */
    public Product(String productNameTxt, String barcodeTxt, byte[] decodedImg, Integer tId, String detailsTxt) {
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}
