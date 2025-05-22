package com.test.demo.services.implementations;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.documents.Subscription;
import com.test.demo.persistence.repositories.SubscriptionRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.projections.dtos.SaveSubscriptionDto;
import com.test.demo.projections.dtos.SubscriptionDto;
import com.test.demo.projections.mappers.SubscriptionMapper;
import com.test.demo.projections.mappers.UserMapper;
import com.test.demo.services.interfaces.InterfaceSubscriptionService;
import com.test.demo.services.interfaces.Roles;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Mono;

@Service
public class SubscriptionService implements InterfaceSubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Mono<Page<SubscriptionDto>> findByAuthUser(Principal principal, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findByEmail(principal.getName())
        .flatMap(user -> 
            subscriptionRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(subscription -> {
                    SubscriptionDto subscriptionDto = subscriptionMapper.toSubscriptionDto(subscription);
                    subscriptionDto.setUser(userMapper.toUserDto(user));
                    return subscriptionDto;
                })
                .collectList()
                .zipWith(subscriptionRepository.countByUserId(user.getId()))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
        );
    }

    @Override
    public Mono<SubscriptionDto> createSubscription(SaveSubscriptionDto saveSubscriptionDto) {
        return userRepository.findById(saveSubscriptionDto.getUserId())
            .flatMap(user -> {
                boolean isSubscriber = user.getRoles().stream()
                    .anyMatch(role -> role.equals(Roles.PREFIX + Roles.SUBSCRIBER));

                return subscriptionRepository.findFirstByUserIdAndStateOrderByEndDateDesc(saveSubscriptionDto.getUserId(), "active")
                    .switchIfEmpty(Mono.just(new Subscription()))
                    .flatMap(activeSubscription -> {
                        Subscription newSubscription = subscriptionMapper.toSubscription(saveSubscriptionDto);
                        
                        if (activeSubscription.getId() != null) {
                            newSubscription.setState("pending");
                            newSubscription.setStartDate(activeSubscription.getEndDate());
                        } else {
                            newSubscription.setState("active");
                            newSubscription.setStartDate(LocalDateTime.now());
                        }

                        newSubscription.setEndDate(newSubscription.getStartDate().plusMonths(
                            saveSubscriptionDto.getPlan().equals("basic") ? 1 : 12
                        ));

                        Mono<SubscriptionDto> subscriptionMono = subscriptionRepository.save(newSubscription)
                            .map(savedSubscription -> {
                                SubscriptionDto subscriptionDto = subscriptionMapper.toSubscriptionDto(savedSubscription);
                                subscriptionDto.setUser(userMapper.toUserDto(user));
                                return subscriptionDto;
                            });

                        if (!isSubscriber) {
                            user.setRoles(Arrays.asList(Roles.PREFIX + Roles.SUBSCRIBER));
                            return userRepository.save(user)
                                .then(subscriptionMono);
                        }

                        return subscriptionMono;
                    });
            })
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    saveSubscriptionDto.getUserId(), 
                    "userId", 
                    "Parece que el usuario no se encuentra en el sistema."
                ).getWebExchangeBindException()
            ));
    }

    public Mono<Boolean> verify(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> subscriptionRepository.countByUserId(user.getId())
                .map(count -> count > 0)
            );
    }
    
}
