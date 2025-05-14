package com.test.demo.projections.dtos;

public class RoleWithUsersCount {
    private String role;
    private Long totalUsers;
    
    public RoleWithUsersCount(String role, Long totalUsers) {
        this.role = role;
        this.totalUsers = totalUsers;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }
}
