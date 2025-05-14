package com.test.demo.projections.dtos;

public class UserCountByMonth {
    private int year;
    private String month;
    private Long totalUsers;
    
    public UserCountByMonth(int year, String month, Long totalUsers) {
        this.year = year;
        this.month = month;
        this.totalUsers = totalUsers;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

}
