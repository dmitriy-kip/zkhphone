package com.progmatik.zkhphone.classes;

public class myPushMessage {
    private String message;
    private String title;
    private Boolean alarm;
    private Boolean vibrate;
    private Boolean isSystem;

    public myPushMessage(String title, String message, Boolean alarm, Boolean vibrate, Boolean isSystem ) {
       this.message = message;
       this.title = title;
       this.alarm = alarm;
       this.vibrate = vibrate;
       this.isSystem = isSystem;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTitle() {
        return this.title;
    }

    public Boolean getAlarm() {
        return this.alarm;
    }

    public Boolean getVibrate() {
        return this.vibrate;
    }

    public Boolean getIsSystem() {
        return this.isSystem;
    }
}
    