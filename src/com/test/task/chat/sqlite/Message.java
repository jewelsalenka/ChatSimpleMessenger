package com.test.task.chat.sqlite;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Olenka on 18.11.2014.
 */
public class Message implements Comparable{
    @Override
    public int compareTo(Object object) {
        Message m = (Message) object;
        return getDate().compareTo(m.getDate());
    }

    public enum Type{
        INPUT, OUTPUT
    }
    private String mMessage;
    private Type mType;
    private Date mDate;

    public Message(String mMessage, Type mType, Date mDate) {
        this.mMessage = mMessage;
        this.mType = mType;
        this.mDate = mDate;
    }

    public String getMessage() {
        return mMessage;
    }

    public Type getType() {
        return mType;
    }

    public Date getDate() {
        return mDate;
    }

    public String getStringDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
