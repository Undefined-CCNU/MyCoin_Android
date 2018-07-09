package com.mycoin.data;

public class Accounting {

    private ExpendBean expendBean;

    public ExpendBean getExpendBean() {
        return expendBean;
    }

    public void setExpendBean(ExpendBean expendBean) {
        this.expendBean = expendBean;
    }

    public static class ExpendBean {

        private String date;
        private int id;
        private int sum;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }


    }

}
