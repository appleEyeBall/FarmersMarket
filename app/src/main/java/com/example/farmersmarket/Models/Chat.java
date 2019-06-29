package com.example.farmersmarket.Models;

// multiple objects of this class will be contained in each documents,
// because a document contains the whole chat, this class creates each msg
public class Chat {
    String userId;      //user Id for each msg sent
    long time;
    String msg;

    public Chat(String userId, long time, String msg) {
        this.userId = userId;
        this.time = time;
        this.msg = msg;
    }
            //Getters
    public String getUserId() {
        return userId;
    }

    public long getTime() {
        return time;
    }

    public String getMsg() {
        return msg;
    }
}
