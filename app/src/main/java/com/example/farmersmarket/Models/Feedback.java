package com.example.farmersmarket.Models;

public class Feedback {
    String name;        // name of reviewer
    int rating;         // rating of the product
    String msg;         // review message

    public Feedback(String name, int rating) {
        this.name = name;
        this.rating = rating;
    }
            // setter
    public void setMsg(String msg) {
        this.msg = msg;
    }
                    // getters
    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getMsg() {
        return msg;
    }
}
