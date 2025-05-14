package com.test.demo.projections.dtos;

public class MostCommonReactionDto {
    private String type;
    private Integer count;
    
    public MostCommonReactionDto() {}
    
    public MostCommonReactionDto(String type, Integer count) {
        this.type = type;
        this.count = count;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getCount() {
        return count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
}
