package com.moutamid.dantlicorp.Model;

public class NotificationModel {
   public String title, type, message;

    public NotificationModel() {
    }

    public NotificationModel(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
