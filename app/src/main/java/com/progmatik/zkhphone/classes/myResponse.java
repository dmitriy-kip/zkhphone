package com.progmatik.zkhphone.classes;

import java.util.ArrayList;

public class myResponse {

    private myResponseResult result = null;

    public myResponseResult getResult() {
        return this.result;
    }
    public void createResult() {
        this.result = new myResponseResult();
    }

    public class myResponseResult {

        private Integer code = 0;
        private String desc = "";
        private myResponseResultList list = new myResponseResultList();

        public void setCode(Integer code) {
            this.code = code;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Integer getCode() {
            return this.code;
        }

        public String getDesc() {
            return this.desc;
        }

        public myResponseResultList getList() {
            return this.list;
        }
        public void createList() { this.list = new myResponseResultList(); }

        public class myResponseResultList {

            private String type = "";
            private myKeyValuesList attributes = new myKeyValuesList();
            private ArrayList<myKeyValuesList> items = new ArrayList <myKeyValuesList>();

            public void setType(String type ) {
                this.type = type;
            }

            public String getType() {
                return this.type;
            }

            public void addItem(myKeyValuesList item) {
                this.items.add( item );
            }

            public myKeyValuesList getItemByIndex(Integer index) {
                if ( index >= 0 && index < this.items.size() ) {
                    return this.items.get(index);
                } else { return null; }
            }

            public ArrayList <myKeyValuesList> getItems() {
                return this.items;
            }

            public myKeyValuesList getAttributes() {
                return this.attributes;
            }
        }
    }
}
    