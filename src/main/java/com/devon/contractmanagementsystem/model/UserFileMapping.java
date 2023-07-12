package com.devon.contractmanagementsystem.model;

import javax.persistence.*;

@Entity
@Table(name = "user_files_mapping")
public class UserFileMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "content_id")
    private String contentId;

    public UserFileMapping(){

    }
    public UserFileMapping(int userId, String contentId) {
        this.userId = userId;
        this.contentId = contentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}