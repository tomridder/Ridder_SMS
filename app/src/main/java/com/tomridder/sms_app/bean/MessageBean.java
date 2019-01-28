package com.tomridder.sms_app.bean;

public class MessageBean
{
    private int id;
    private  String address;
    private String person;
    private long date;
    private int read;
    private int type;
    private long threadId;
    private String message;

    public MessageBean(int id, String address, String person, long date, int read, int type, long threadId,String message)
    {
        this.id = id;
        this.address = address;
        this.person = person;
        this.date = date;
        this.read = read;
        this.type = type;
        this.threadId = threadId;
        this.message=message;
    }

    public int getId() {
        return id;
    }

    public MessageBean setId(int id) {
        this.id = id;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public MessageBean setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPerson() {
        return person;
    }

    public MessageBean setPerson(String person) {
        this.person = person;
        return this;
    }

    public long getDate() {
        return date;
    }

    public MessageBean setDate(long date) {
        this.date = date;
        return this;
    }

    public int getRead() {
        return read;
    }

    public MessageBean setRead(int read) {
        this.read = read;
        return this;
    }

    public int getType() {
        return type;
    }

    public MessageBean setType(int type) {
        this.type = type;
        return this;
    }

    public long getThreadId() {
        return threadId;
    }

    public MessageBean setThreadId(long threadId) {
        this.threadId = threadId;
        return this;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", person='" + person + '\'' +
                ", date=" + date +
                ", read=" + read +
                ", type=" + type +
                ", threadId=" + threadId +
                ", message='" + message + '\'' +
                '}';
    }
}
