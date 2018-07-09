package com.mycoin.data;

public class AddBudget {

    private BudgetBean budget;

    public BudgetBean getBudget() {
        return budget;
    }

    public void setBudget(BudgetBean budget) {
        this.budget = budget;
    }

    public static class BudgetBean {

        private int budget;
        private int month;
        private int user_id;

        public int getBudget() {
            return budget;
        }

        public void setBudget(int budget) {
            this.budget = budget;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

    }

}

