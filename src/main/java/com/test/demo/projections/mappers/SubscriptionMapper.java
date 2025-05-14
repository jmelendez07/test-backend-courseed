package com.test.demo.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.test.demo.persistence.documents.Subscription;
import com.test.demo.projections.dtos.SaveSubscriptionDto;
import com.test.demo.projections.dtos.SubscriptionDto;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    
    @Mapping(target = "user", ignore = true)
    SubscriptionDto toSubscriptionDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Subscription toSubscription(SaveSubscriptionDto saveSubscriptionDto);
}
