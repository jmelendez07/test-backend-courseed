package com.test.demo.web.exceptions;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

public class CustomWebExchangeBindException {

    private BindingResult bindingResult;
    private MethodParameter methodParameter;
    
    public CustomWebExchangeBindException(Object target, String field, String message) {
        DataBinder dataBinder = new DataBinder(target);
        BindingResult bindingResult = dataBinder.getBindingResult();
        bindingResult.addError(new FieldError(target.getClass().getName(), field, message));
        MethodParameter methodParameter = new MethodParameter(getClass().getDeclaredMethods()[0], -1);

        this.bindingResult = bindingResult;
        this.methodParameter = methodParameter;
    }

    public CustomWebExchangeBindException(Object target, Map<String, String> errors) {
        DataBinder dataBinder = new DataBinder(target);
        BindingResult bindingResult = dataBinder.getBindingResult();
        
        for(Map.Entry<String, String> entry : errors.entrySet()) {
            bindingResult.addError(new FieldError(target.getClass().getName(), entry.getKey(), entry.getValue()));
        }
        
        MethodParameter methodParameter = new MethodParameter(getClass().getDeclaredMethods()[0], -1);

        this.bindingResult = bindingResult;
        this.methodParameter = methodParameter;
    }

    public WebExchangeBindException getWebExchangeBindException() {
        return new WebExchangeBindException(this.methodParameter, this.bindingResult);
    }

    public static Map<String, String> getErrors(WebExchangeBindException webExchangeBindException) {
        return webExchangeBindException
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage()));
    }

}
