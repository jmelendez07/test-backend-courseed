package com.test.demo.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.documents.Role;
import com.test.demo.persistence.repositories.RoleRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.projections.dtos.RoleWithUsersCount;
import com.test.demo.services.interfaces.InterfaceRoleService;
import com.test.demo.services.interfaces.Roles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoleService implements InterfaceRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Flux<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Mono<Role> findById(String id) {
        return roleRepository.findById(id);
    }

    @Override
    public Mono<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Mono<List<RoleWithUsersCount>> getRolesWithUserCount() {
        return roleRepository.findAll()
            .flatMap(role -> userRepository.findByRolesContaining(Roles.PREFIX + role.getName())
                .count()
                .map(usersCount -> new RoleWithUsersCount(Roles.PREFIX + role.getName(), usersCount))
            )
            .collectList();
    }

    @Override
    public Mono<Role> create(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return roleRepository.deleteById(id);
    }
}
