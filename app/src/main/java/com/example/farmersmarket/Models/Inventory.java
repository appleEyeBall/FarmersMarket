package com.example.farmersmarket.Models;

public class Inventory {
    String label;
    String strain;
    String flavor;
    String imguri;
    int rating;
    String pricing;
    String[] feedbacks;

    public Inventory(String label, String pricing){
        this.label = label;
        this.pricing = pricing;
    }

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

    public void setFeedbacks(String[] feedbacks) {
        this.feedbacks = feedbacks;
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

    public String[] getFeedbacks() {
        return feedbacks;
    }
}
