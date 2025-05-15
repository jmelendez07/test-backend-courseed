package com.test.demo.services.implementations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.demo.services.interfaces.InterfaceValidationService;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class ValidationService implements InterfaceValidationService {

    @Autowired
    private Validator validator;

    public void validate(Object object) {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        
        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            
            for (ConstraintViolation<Object> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }

            throw new CustomWebExchangeBindException(violations, errors).getWebExchangeBindException();
        }
    } 
}
