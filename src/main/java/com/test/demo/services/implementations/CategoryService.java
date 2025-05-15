package com.test.demo.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.repositories.CategoryRepository;
import com.test.demo.projections.dtos.CategoryDto;
import com.test.demo.projections.mappers.CategoryMapper;
import com.test.demo.services.interfaces.InterfaceCategoryService;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Mono;

@Service
public class CategoryService implements InterfaceCategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Mono<Page<CategoryDto>> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        return categoryRepository.findAllBy(pageable)
            .map(categoryMapper::toCategoryDto)
            .collectList()
            .zipWith(categoryRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<CategoryDto> getCategoryById(String id) {
        return categoryRepository.findById(id)
            .map(categoryMapper::toCategoryDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "categoryId", 
                    "No hemos podido encontrar la categoría indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }

    @Override
    public Mono<CategoryDto> getCategoryByName(String name) {
        return categoryRepository.findByName(name)
            .map(categoryMapper::toCategoryDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    name, 
                    "name", 
                    "No hemos podido encontrar la categoría indicada por su nombre. Te sugerimos que verifiques y lo intentes nuevamente."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<Boolean> deleteCategory(String id) {
        return categoryRepository.findById(id)
            .flatMap(category -> 
                categoryRepository.deleteById(id)
                    .then(Mono.just(true))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "categoryId", 
                    "No hemos podido encontrar la categoría indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }

    public String standarizeCategory(String categoryName) {
        String category = categoryName.trim().toLowerCase();
        
        if (category.matches(".*(arquitectura y dis[e|e][n|ñ]o).*")) {
            return "arquitectura";
        }
        
        if (category.matches(".*(artes|artes y humanidades|ciencias humanas|humanidades).*")) {
            return "artes y humanidades";
        }
        
        if (category.matches(".*(centro interdisciplinario de estudios sobre desarrollo|ciencias sociales|ciencias sociales y humanidades).*")) {
            return "ciencias sociales";
        }
        
        if (category.matches(".*(ciencias de la educacion|educacion).*")) {
            return "ciencias de la educacion";
        }
        
        if (category.matches(".*(ciencias de la salud|enfermeria|medicina|odontologia|psicologia|quimica y farmacia).*")) {
            return "ciencias de la salud";
        }
        
        if (category.matches(".*(ciencias|ciencias basicas).*")) {
            return "ciencias";
        }
        
        if (category.matches(".*(ciencias juridicas|derecho|derecho canonico|escuela de negocios leyes y sociedad).*")) {
            return "derecho";
        }
        
        if (category.matches(".*(ciencias politicas y relaciones internacionales|escuela de gobierno alberto lleras camargo|escuela de gobierno y etica publica).*")) {
            return "ciencias politicas";
        }
        
        if (category.matches(".*(comunicacion y lenguaje).*")) {
            return "comunicacion y lenguaje";
        }
        
        if (category.matches(".*(diseño e ingenieria|ingenieria).*")) {
            return "ingenieria";
        }
        
        if (category.matches(".*(estudios ambientales y rurales|instituto ideeas|instituto pensar|vicerrectoria de investigacion y creacion).*")) {
            return "ambiental";
        }
        
        if (category.matches(".*(filosof[i|í]a|teolog[i|í]a).*")) {
            return "filosofia";
        }
        
        if (category.matches(".*(nutricion y dietetica).*")) {
            return "nutricion y dietetica";
        }
        
        if (category.matches(".*(econom[i|í]a|empresariales|administrativas|negocios|finanzas).*")) {
            return "ciencias economicas";
        }

        if (category.matches(".*(direccion de internacionalizacion).*")) {
            return "direccion de internacionalizacion";
        }
        
        return "none";
    }
}
