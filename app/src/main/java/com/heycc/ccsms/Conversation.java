package com.heycc.ccsms;

/**
 * Created by cc on 11/26/15.
 */
public class Conversation {
    public String title;
    public String time;
    public String msg;

    public Conversation(String title, String msg, String time) {
        this.title = title;
        this.time = time;
        this.msg = msg;
    }
}
