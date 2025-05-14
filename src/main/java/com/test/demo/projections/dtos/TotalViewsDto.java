package com.test.demo.projections.dtos;

public class TotalViewsDto {
    private Long total;
    private Long lastMonth;
    
    public TotalViewsDto(Long total, Long lastMonth) {
        this.total = total;
        this.lastMonth = lastMonth;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(Long lastMonth) {
        this.lastMonth = lastMonth;
    }
}
