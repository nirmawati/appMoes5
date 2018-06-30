package com.example.nirma.moes5.model;

public class Messages
{
    private String message, type;
    private Long time;
    private  Boolean seen;
    private String from;

    public Messages()
    {

    }

    public Messages(String message, String type, Long time, Boolean seen, String from)
    {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
