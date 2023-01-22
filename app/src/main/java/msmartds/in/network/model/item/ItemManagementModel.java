package msmartds.in.network.model.item;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yuganshu on 22/02/2017.
 */

public class ItemManagementModel {
    String itemId;
    String name;
    String shortDesc;
    String longDesc;
    String price;
    String discount;
    @SerializedName("dt")
    String deliveryTIme;
    String status;
    String itemImage;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDeliveryTIme() {
        return deliveryTIme;
    }

    public void setDeliveryTIme(String deliveryTIme) {
        this.deliveryTIme = deliveryTIme;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public ItemManagementModel(String itemID, String name, String shortDescription, String longDescription, String price, String discount, String deliveryTIme, String status, String itemImage) {
        this.itemId = itemID;
        this.name = name;
        this.shortDesc = shortDescription;
        this.longDesc = longDescription;
        this.price = price;
        this.discount = discount;
        this.deliveryTIme = deliveryTIme;
        this.status = status;
        this.itemImage = itemImage;
    }
}
