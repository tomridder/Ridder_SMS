package com.tomridder.sms_app.bean;

public class ContactBean
{
    private String name;
    private String callNum;

    public ContactBean(String name, String callNum) {
        this.name = name;
        this.callNum = callNum;
    }

    public String getName() {
        return name;
    }

    public ContactBean setName(String name) {
        this.name = name;
        return this;
    }

    public String getCallNum() {
        return callNum;
    }

    public ContactBean setCallNum(String callNum) {
        this.callNum = callNum;
        return this;
    }
}
