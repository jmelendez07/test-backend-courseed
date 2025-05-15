package com.test.demo.services.interfaces;

import java.security.Principal;

import org.springframework.data.domain.Page;

import com.test.demo.projections.dtos.SaveSubscriptionDto;
import com.test.demo.projections.dtos.SubscriptionDto;

import reactor.core.publisher.Mono;

public interface InterfaceSubscriptionService {
    Mono<Page<SubscriptionDto>> findByAuthUser(Principal principal, int page, int size);
    Mono<SubscriptionDto> createSubscription(SaveSubscriptionDto saveSubscriptionDto);
}
