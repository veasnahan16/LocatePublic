package com.example.veasnahan.locatepublic;

public class Item {

    private String type;
    private String brand;
    private String eng;
    private String khm;
    private String imageUrl;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
    }

    public String getKhm() {
        return khm;
    }

    public void setKhm(String khm) {
        this.khm = khm;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Item(String type, String brand, String eng, String khm, String imageUrl) {
        this.type = type;
        this.brand = brand;
        this.eng = eng;
        this.khm = khm;
        this.imageUrl = imageUrl;
    }
}
