package com.progmatik.zkhphone.classes;

import java.util.ListIterator;
import java.util.ArrayList;

public class myKeyValuesList {

    private ArrayList<myKeyValueItem> items = new ArrayList<myKeyValueItem>();

    public Boolean addItem(myKeyValueItem item) {
        Boolean result = false;
        Boolean keyExists = false;
        ListIterator<myKeyValueItem> it = this.items.listIterator();
        while (it.hasNext()) {
            myKeyValueItem i = it.next();
            if (i.getKey().equals(item.getKey())) {
                it.set(item);
                result = true;
                keyExists = true;
                break;
            }
        }
        if (!keyExists) {
            this.items.add(item);
            result = true;
        }
        return result;
    }

    public Boolean updateItem(String key, myKeyValueItem item) {
        Boolean result = false;
        ListIterator<myKeyValueItem> it = this.items.listIterator();
        while (it.hasNext()) {
            myKeyValueItem i = it.next();
            if (i.getKey().equals(key)) {
                it.set(item);
                result = true;
                break;
            }
        }
        return result;
    }

    public Boolean deleteItem(String key) {
        Boolean result = false;
        ListIterator<myKeyValueItem> it = this.items.listIterator();
        while (it.hasNext()) {
            myKeyValueItem i = it.next();
            if (i.getKey().equals(key)) {
                it.remove();
                result = true;
                break;
            }
        }
        return result;
    }


    public myKeyValueItem getItem(String key) {
        myKeyValueItem result = null;
        ListIterator<myKeyValueItem> it = this.items.listIterator();
        while (it.hasNext()) {
            myKeyValueItem i = it.next();
            if (i.getKey().equals(key)) {
                result = i;
                break;
            }
        }
        return result;
    }

    public ArrayList<myKeyValueItem> getItems() {
        return this.items;
    }

    public ArrayList<String> getKeys() {
        ArrayList<String> result = new ArrayList<String>();
        for (myKeyValueItem i : this.items) {
            result.add(i.getKey());
        }
        return result;
    }

    public ArrayList<String> getValues() {
        ArrayList<String> result = new ArrayList<String>();
        for (myKeyValueItem i : this.items) {
            result.add(i.getValue());
        }
        return result;
    }

    public Integer getCount() {
        return this.items.size();
    }
}


