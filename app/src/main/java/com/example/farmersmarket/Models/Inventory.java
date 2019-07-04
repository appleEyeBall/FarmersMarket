package com.example.farmersmarket.Models;

public class Inventory {
    String label;
    String strain;
    String flavor;
    String imguri;
    int rating;
    String pricing;
    String weightType;
    String cbd;
    String thc;
    boolean inStock;
                    // Setters

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStrain(String strain) {
        this.strain = strain;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public void setImguri(String imguri) {
        this.imguri = imguri;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public void setWeightType(String weightType) {
        this.weightType = weightType;
    }

    public void setCbd(String cbd) {
        this.cbd = cbd;
    }

    public void setThc(String thc) {
        this.thc = thc;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    // Getters
    public String getLabel() {
        return label;
    }

    public String getStrain() {
        return strain;
    }

    public String getFlavor() {
        return flavor;
    }

    public String getImguri() {
        return imguri;
    }

    public int getRating() {
        return rating;
    }

    public String getPricing() {
        return pricing;
    }

    public String getWeightType() {
        return weightType;
    }

    public String getCbd() {
        return cbd;
    }

    public String getThc() {
        return thc;
    }

    public boolean getInStock(){
        return inStock;
    }
}
