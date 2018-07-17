package com.example.veasnahan.locatepublic;

public class Product     {
    private String name;
    private String header;
    private String title;
    private String imageUrl;
    private Boolean bybrand;

    public Product(String name, String header, String title, String imageUrl, Boolean bybrand) {
        this.name = name;
        this.header = header;
        this.title = title;
        this.imageUrl = imageUrl;
        this.bybrand = bybrand;
    }

    public String getName() {
        return name;
    }
    public String getHeader() {
        return header;
    }
    public String getTitle() {
        return title;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public Boolean getByBrand() {
        return bybrand;
    }

}
