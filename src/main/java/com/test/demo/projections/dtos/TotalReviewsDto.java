package com.test.demo.projections.dtos;

public class TotalReviewsDto {
    private Long total;
    private Long lastMonth;
    
    public TotalReviewsDto(Long total, Long lastMonth) {
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
