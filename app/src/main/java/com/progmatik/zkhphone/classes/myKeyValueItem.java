package com.progmatik.zkhphone.classes;

public class myKeyValueItem {
    String key = "";
    String value = "";

    public myKeyValueItem(String key, String value ) {
        this.key = key;
         this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
