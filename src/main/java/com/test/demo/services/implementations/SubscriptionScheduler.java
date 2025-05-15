package com.test.demo.services.implementations;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.test.demo.persistence.documents.Subscription;
import com.test.demo.persistence.repositories.SubscriptionRepository;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class SubscriptionScheduler {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PostConstruct
    public void scheduleSubscriptionCheck() {
        Flux.interval(Duration.ofMinutes(1))
            .flatMap(tick -> updateSubscriptions())
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe();
    }

    private Flux<Subscription> updateSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        
        return subscriptionRepository.findAll()
            .flatMap(subscription -> {
                String currentStatus = subscription.getState();

                if ("pending".equals(currentStatus) && subscription.getStartDate().isBefore(now)) {
                    subscription.setState("active");
                    return subscriptionRepository.save(subscription);
                }

                if ("active".equals(currentStatus) && subscription.getEndDate().isBefore(now)) {
                    subscription.setState("expired");
                    return subscriptionRepository.save(subscription);
                }

                return Mono.empty();
            });
    }
}
