package com.example.scynd2.Utils;

public class GoalsUtil {
        //target management
        private static GoalsUtil instance;
        private String mytime;
        private String myloc;


        private GoalsUtil() {
        }

        public static GoalsUtil getInstance() {
            if (instance == null) {
                instance = new GoalsUtil();
            }
            return instance;
        }

        public String getMytime() {
            return mytime;
        }

        public void setMytime(String value) {
            mytime = value;
        }

    public String getMyloc() {
        return myloc;
    }

    public void setMyloc(String myloc) {
        this.myloc = myloc;
    }


}
