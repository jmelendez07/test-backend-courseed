package com.test.demo.services.interfaces;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.test.demo.projections.dtos.InstitutionDto;
import com.test.demo.projections.dtos.InstitutionWithCoursesCountDto;
import com.test.demo.projections.dtos.SaveInstitutionDto;
import reactor.core.publisher.Mono;

public interface InterfaceInstitutionService {
    Mono<Page<InstitutionDto>> getAllInstitutions(int page, int size);
    Mono<List<InstitutionWithCoursesCountDto>> getInstitutionsWithCoursesCount(int page, int size);
    Mono<InstitutionDto> getInstitutionById(String id);
    Mono<InstitutionDto> getInstitutionByName(String name);
    Mono<InstitutionDto> getInstitutionByAuth(Principal principal);
    Mono<Object> createInstitution(Principal principal, SaveInstitutionDto saveInstitutionDto, String baseurl);
    Mono<Object> updateInstitution(String id, Principal principal, SaveInstitutionDto saveInstitutionDto, String baseurl);
    Mono<Boolean> deleteInstitution(String id);
}
