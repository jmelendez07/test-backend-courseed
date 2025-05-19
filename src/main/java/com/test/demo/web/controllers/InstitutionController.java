package com.test.demo.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.test.demo.projections.dtos.SaveInstitutionDto;
import com.test.demo.services.implementations.InstitutionService;

import reactor.core.publisher.Mono;

public class InstitutionController {

    @Autowired
    private InstitutionService institutionService;

    public Mono<ServerResponse> getAllInstitutions(ServerRequest serverRequest) {
        return institutionService
            .getAllInstitutions(
                Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
                Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
            )
            .flatMap(institutions -> ServerResponse.ok().bodyValue(institutions))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getInstitutionById(ServerRequest serverRequest) {
        return institutionService.getInstitutionById(serverRequest.pathVariable("id"))
            .flatMap(institutionDto -> ServerResponse.ok().bodyValue(institutionDto))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
    
    public Mono<ServerResponse> getInstitutionByName(ServerRequest serverRequest) {
        return institutionService.getInstitutionByName(serverRequest.pathVariable("name"))
            .flatMap(institutionDto -> ServerResponse.ok().bodyValue(institutionDto))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getInstitutionByAuth(ServerRequest serverRequest) {
        return serverRequest.principal()
            .flatMap(principal -> institutionService.getInstitutionByAuth(principal)
                .flatMap(institutionDto -> ServerResponse.ok().bodyValue(institutionDto))
                .switchIfEmpty(ServerResponse.notFound().build())
            );
    }

    public Mono<ServerResponse> getInstitutionsWithCoursesCount(ServerRequest serverRequest) {
        return institutionService.getInstitutionsWithCoursesCount(
            Integer.parseInt(serverRequest.queryParam("page").orElse("0")), 
            Integer.parseInt(serverRequest.queryParam("size").orElse("10"))
        )
        .flatMap(institutions -> ServerResponse.ok().bodyValue(institutions))
        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createInstitution(ServerRequest serverRequest) {
        return serverRequest.multipartData()
            .flatMap(parts -> {
    
                FormFieldPart namePart = (FormFieldPart) parts.toSingleValueMap().get("name");
                FilePart image = (FilePart) parts.getFirst("image");

                if (namePart == null || namePart.value().isBlank()) {
                    return ServerResponse.badRequest().bodyValue("Para proceder, debes completar el campo correspondiente al nombre de la institución.");
                }

                SaveInstitutionDto saveInstitutionDto = new SaveInstitutionDto();
                saveInstitutionDto.setName(namePart.value());

                if (image != null && image.filename() != null && !image.filename().isBlank()) {
                    saveInstitutionDto.setImage(image);
    
                    if (!saveInstitutionDto.isValidImage()) {
                        return ServerResponse.badRequest().bodyValue("La imagen debe ser de tipo válido (jpg, png, jpeg) y menor a 2 MB.");
                    }
                }

                String baseUrl = serverRequest.uri().getScheme() + "://" + serverRequest.uri().getHost() + ":" + serverRequest.uri().getPort();

                return serverRequest.principal()
                    .flatMap(principal -> institutionService.createInstitution(principal, saveInstitutionDto, baseUrl)
                        .flatMap(institutionDto -> ServerResponse.ok().bodyValue(institutionDto))
                        .switchIfEmpty(ServerResponse.notFound().build())
                    );
            });
    }

    public Mono<ServerResponse> updateInstitution(ServerRequest serverRequest) {
        return serverRequest.multipartData()
            .flatMap(parts -> {
                FormFieldPart namePart = (FormFieldPart) parts.toSingleValueMap().get("name");
                FilePart image = (FilePart) parts.getFirst("image");
    
                if (namePart == null || namePart.value().isBlank()) {
                    return ServerResponse.badRequest().bodyValue("Para proceder, debes completar el campo correspondiente al nombre de la institución.");
                }
    
                SaveInstitutionDto saveInstitutionDto = new SaveInstitutionDto();
                saveInstitutionDto.setName(namePart.value());
    
                if (image != null && image.filename() != null && !image.filename().isBlank()) {
                    saveInstitutionDto.setImage(image);
    
                    if (!saveInstitutionDto.isValidImage()) {
                        return ServerResponse.badRequest().bodyValue("La imagen debe ser de tipo válido (jpg, png, jpeg) y menor a 2 MB.");
                    }
                }
    
                String baseUrl = serverRequest.uri().getScheme() + "://" + serverRequest.uri().getHost() + ":" + serverRequest.uri().getPort();
    
                return serverRequest.principal()
                    .flatMap(principal -> institutionService.updateInstitution(serverRequest.pathVariable("id"), principal, saveInstitutionDto, baseUrl)
                        .flatMap(institutionDto -> ServerResponse.ok().bodyValue(institutionDto))
                        .switchIfEmpty(ServerResponse.notFound().build())
                    );
            });
    }

    public Mono<ServerResponse> deleteInstitution(ServerRequest serverRequest) {
        return institutionService.deleteInstitution(serverRequest.pathVariable("id"))
            .flatMap(i -> ServerResponse.ok().bodyValue(i))
            .switchIfEmpty(ServerResponse.notFound().build());
    }
}
