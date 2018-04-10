package com.schauhan.multinotes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class Note {

    private String title;
    private String content;
    private Date createddate;
    private Date modifieddate;
    private String id;

    public String getId() {
        return id;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getModifieddate() {
        return modifieddate;
    }

    public void setModifieddate(Date modifieddate) {
        this.modifieddate = modifieddate;
    }

    public Note(String title, String content)
    {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.createddate = this.modifieddate = new Date();
    }

    public Note(String id, String title, String content, Date createddate, Date modifieddate)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createddate = createddate;
        this.modifieddate = modifieddate;
    }


}
