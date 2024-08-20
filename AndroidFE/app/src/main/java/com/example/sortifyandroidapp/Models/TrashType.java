package com.example.sortifyandroidapp.Models;

import java.io.Serializable;

/**
 * Trash Type Class is used to store different types of products
 * Serializable helps to transfer TrashType Model objects between activities
 * **/
public class TrashType extends Throwable implements Serializable {
    public Integer typeId;
    public String typeName;
    public String info;

    private TrashType(){

    }
    public TrashType(Integer typeId, String typeName, String info) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.info = info;
    }

    public TrashType(String typeName, String info) {
        this.typeName = typeName;
        this.info = info;
    }
}
