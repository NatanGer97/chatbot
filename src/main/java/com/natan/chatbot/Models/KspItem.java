package com.natan.chatbot.Models;

public class KspItem {
        String name;
        String img;
        double price;
        String brandName;
        String brandImg;

    public KspItem(String name, String img, double price, String brandName, String brandImg) {
        this.name = name;
        this.img = img;
        this.price = price;
        this.brandName = brandName;
        this.brandImg = brandImg;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setBrandImg(String brandImg) {
        this.brandImg = brandImg;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public double getPrice() {
        return price;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandImg() {
        return brandImg;
    }
}
