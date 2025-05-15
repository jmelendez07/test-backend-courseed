package com.test.demo.web.controllers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.SaveSubscriptionDto;
import com.test.demo.services.implementations.SubscriptionService;
import reactor.core.publisher.Mono;

public class SubscriptionController {
    
    @Autowired
    private SubscriptionService subscriptionService;

    // @Value("${PAYU_API_KEY}")
    private String API_KEY = "4Vj8eK4rloUd272L48hsrarnUA";

    // @Value("${PAYU_MERCHANT_ID}")
    private String MERCHANT_ID = "508029";

    public Mono<ServerResponse> findByAuthUser(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> subscriptionService.findByAuthUser(
                principal, 
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            ))
                .flatMap(searchHistories -> ServerResponse.ok().bodyValue(searchHistories))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> confirm(ServerRequest serverRequest) {
        String receivedToken = serverRequest.queryParam("secret").orElse("");
        if (!receivedToken.equals(API_KEY)) {
            return ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue("Token inválido");
        }

        return serverRequest.formData().flatMap(formData -> {
            String referenceCode = formData.getFirst("reference_sale");
            String referencePol = formData.getFirst("reference_sale");
            String amount = formData.getFirst("value");
            String currency = formData.getFirst("currency");
            String signatureSentByPayU = formData.getFirst("sign");
            String statePol = formData.getFirst("state_pol"); 

            if (referenceCode == null || amount == null || currency == null || signatureSentByPayU == null || statePol == null) {
                return ServerResponse.status(HttpStatus.BAD_REQUEST).bodyValue("Datos incompletos");
            }

            String generatedSignature = generateMd5Signature(referencePol, amount, currency, statePol);

            if (!generatedSignature.equals(signatureSentByPayU)) {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Firma inválida");
            }

            if (!statePol.equals("4")) {
                return ServerResponse.status(HttpStatus.PAYMENT_REQUIRED).bodyValue("El pago no fue aprobado, estado: " + statePol);
            }

            String transactionId = formData.getFirst("transaction_id");
            String paymentMethod = formData.getFirst("payment_method_name");
            String authorizationCode = formData.getFirst("authorization_code");
            String responseMessage = formData.getFirst("response_message_pol");
            String userId = extractUserId(referenceCode);
            String plan = extractPlan(referenceCode);
            Long price = parseLong(formData.getFirst("value"));

            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusMonths(plan.equals("basic") ? 1 : 12);

            SaveSubscriptionDto dto = new SaveSubscriptionDto();
            dto.setUserId(userId);
            dto.setPlan(plan);
            dto.setState(statePol.equals("4") ? "active" : "failed");
            dto.setReferenceCode(referenceCode);
            dto.setTransaction(transactionId);
            dto.setPrice(price);
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            dto.setPaymentMethod(paymentMethod);
            dto.setCurrency(currency);
            dto.setAuthorizationCode(authorizationCode);
            dto.setResponseMessage(responseMessage);

            return subscriptionService.createSubscription(dto)
                .flatMap(savedSubscription -> ServerResponse.ok().bodyValue(savedSubscription))
                .onErrorResume(e -> ServerResponse.notFound().build());
        });
    }

    private String extractUserId(String referenceCode) {
        String[] parts = referenceCode.split("_");
        return parts.length > 1 ? parts[1] : "unknown";
    }

    private String extractPlan(String referenceCode) {
        String[] parts = referenceCode.split("_");
        return parts.length > 2 ? parts[2] : "basic";
    }

    private Long parseLong(String value) {
        try {
            return value != null ? Long.parseLong(value.split("\\.")[0]) : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String generateMd5Signature(String referenceCode, String amount, String currency, String statePol) {
    try {
        String formattedAmount = String.format(Locale.US, "%.1f", Double.parseDouble(amount));
        String data = API_KEY + "~" + MERCHANT_ID + "~" + referenceCode + "~" + formattedAmount + "~" + currency + "~" + statePol;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("Error al generar firma MD5", e);
    }
}

}
