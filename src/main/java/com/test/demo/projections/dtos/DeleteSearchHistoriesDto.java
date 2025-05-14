package com.test.demo.projections.dtos;

import java.io.Serializable;
import java.util.List;

import com.test.demo.projections.validators.groups.FirstValidationGroup;
import com.test.demo.projections.validators.groups.SecondValidationGroup;
import com.test.demo.projections.validators.groups.ThirdValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@GroupSequence({ DeleteSearchHistoriesDto.class, FirstValidationGroup.class, SecondValidationGroup.class, ThirdValidationGroup.class })
public class DeleteSearchHistoriesDto implements Serializable {
    
    @NotEmpty(message = "Es importante que selecciones los historiales de busqueda antes de continuar.", groups = FirstValidationGroup.class)
    @Size(min = 1, message = "Es importante que selecciones los historiales de busqueda antes de continuar.", groups = SecondValidationGroup.class)
    private List<@NotBlank(message = "Es importante que selecciones los historiales de busqueda antes de continuar.", groups = ThirdValidationGroup.class) String> searchHistories;

    public List<String> getSearchHistories() {
        return searchHistories;
    }

    public void setSearchHistories(List<String> searchHistories) {
        this.searchHistories = searchHistories;
    }
    
}
