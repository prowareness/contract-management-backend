package com.devon.contractmanagementsystem.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Table(name = "files")
public class FileDB {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id")
    private String id;
    
    private String name;

    private String type;

    @Lob
    private byte[] data;

    @Column(name = "effective_date")
    private Date effectiveDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    public FileDB() {
    }

    public FileDB(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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