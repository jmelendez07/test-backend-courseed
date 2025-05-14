package com.test.demo.projections.dtos;

import java.io.Serializable;


public class TokenDto implements Serializable {
    private String token;

    public TokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}
