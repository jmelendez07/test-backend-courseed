package com.test.demo.projections.dtos;

import jakarta.validation.constraints.NotBlank;

public class SaveSearchHistoryDto {
    @NotBlank(message = "Es importante que rellenes el campo de buscar antes de continuar.")
    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
