package com.test.demo.services.interfaces;

import java.util.List;

import com.test.demo.persistence.documents.Role;
import com.test.demo.projections.dtos.RoleWithUsersCount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InterfaceRoleService {
    Flux<Role> findAll();
    Mono<Role> findById(String id);
    Mono<Role> findByName(String name);
    Mono<List<RoleWithUsersCount>> getRolesWithUserCount(); 
    Mono<Role> create(Role role);
    Mono<Void> deleteById(String id);
}
