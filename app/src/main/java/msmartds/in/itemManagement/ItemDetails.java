package msmartds.in.itemManagement;

/**
 * Created by Yuganshu on 22/02/2017.
 */

public class ItemDetails {
    String itemID;
    String name;
    String shortDescription;
    String longDescription;
    String price;
    String discount;
    String deliveryTIme;
    String status;
    String itemImage;

    public ItemDetails(String itemID, String name, String shortDescription, String longDescription, String price, String discount, String deliveryTIme, String status, String itemImage) {
        this.itemID = itemID;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.price = price;
        this.discount = discount;
        this.deliveryTIme = deliveryTIme;
        this.status = status;
        this.itemImage = itemImage;
    }
}
