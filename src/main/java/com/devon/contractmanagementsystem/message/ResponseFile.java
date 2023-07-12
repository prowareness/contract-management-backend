package com.devon.contractmanagementsystem.message;

import java.util.Date;

public class ResponseFile {
    private String name;
    private String url;
    private String type;
    private long size;

    private Date effectiveDate;
    private Date expirationDate;

    public ResponseFile(String name, String url, String type, long size, Date effectiveDate, Date expirationDate) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.size = size;
        this.effectiveDate=effectiveDate;
        this.expirationDate=expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getEffectiveDate(){
        return effectiveDate;
    }
    public void setEffectiveDate(Date effectiveDate){
        this.effectiveDate=effectiveDate;
    }

    public Date getExpirationDate(){
        return expirationDate;
    }
    public void setExpirationDate(Date expirationDate){
        this.expirationDate=expirationDate;
    }
}

