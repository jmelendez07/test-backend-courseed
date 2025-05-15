package com.test.demo.services.implementations;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.test.demo.persistence.documents.Institution;
import com.test.demo.persistence.repositories.CourseRepository;
import com.test.demo.persistence.repositories.InstitutionRepository;
import com.test.demo.persistence.repositories.UserRepository;
import com.test.demo.projections.dtos.InstitutionDto;
import com.test.demo.projections.dtos.InstitutionWithCoursesCountDto;
import com.test.demo.projections.dtos.SaveInstitutionDto;
import com.test.demo.projections.mappers.InstitutionMapper;
import com.test.demo.services.interfaces.InterfaceInstitutionService;
import com.test.demo.web.exceptions.CustomWebExchangeBindException;

import java.util.UUID;
import java.nio.file.*;

import reactor.core.publisher.Mono;

@Service
public class InstitutionService implements InterfaceInstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private UserRepository userRepository;
    
    private String uploadPath = "uploads/institutions";

    @Override
    public Mono<Page<InstitutionDto>> getAllInstitutions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return institutionRepository.findAllBy(pageable)
            .map(institutionMapper::toInstitutionDto)
            .collectList()
            .zipWith(institutionRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<InstitutionDto> getInstitutionById(String id) {
        return institutionRepository.findById(id)
            .map(institutionMapper::toInstitutionDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id,
                    "institutionId", 
                    "No hemos podido encontrar la institución indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }

    @Override
    public Mono<InstitutionDto> getInstitutionByName(String name) {
        return institutionRepository.findByName(name)
            .map(institutionMapper::toInstitutionDto)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    name, 
                    "name", 
                    "No hemos podido encontrar la institución indicada por su nombre. Te sugerimos que verifiques y lo intentes nuevamente."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<InstitutionDto> getInstitutionByAuth(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> institutionRepository.findByUserId(user.getId())
                .map(institutionMapper::toInstitutionDto)
                .switchIfEmpty(Mono.error(
                    new CustomWebExchangeBindException(
                        principal.getName(), 
                        "userId", 
                        "No hemos podido encontrar la institución asociada al usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                    ).getWebExchangeBindException()
                ))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(), 
                    "userId", 
                    "No hemos podido encontrar el usuario indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ));
    }

    @Override
    public Mono<List<InstitutionWithCoursesCountDto>> getInstitutionsWithCoursesCount(int page, int size) {
        return institutionRepository.findAll()
            .flatMap(institution -> courseRepository.countByInstitutionId(institution.getId())
                .map(totalCourses -> new InstitutionWithCoursesCountDto(institution.getId(), institution.getName(), totalCourses))
            )
            .sort((institution1, institution2) -> institution2.getTotalCourses().compareTo(institution1.getTotalCourses()))
            .skip(page * size)
            .take(size)
            .collectList();
    }

    @Override
    public Mono<Object> createInstitution(Principal principal, SaveInstitutionDto saveInstitutionDto, String baseurl) {
        return institutionRepository.findByName(saveInstitutionDto.getName())
            .flatMap(existingInstitution -> Mono.error(
                new CustomWebExchangeBindException(
                    saveInstitutionDto.getName(),
                    "name",
                    "La institución que has mencionado ya está registrada. Asegúrate de elegir un nombre diferente e intenta nuevamente."
                ).getWebExchangeBindException()
            ))
            .switchIfEmpty(
                userRepository.findByEmail(principal.getName())
                .switchIfEmpty(Mono.error(
                    new CustomWebExchangeBindException(
                        principal.getName(),
                        "userId",
                        "No se pudo encontrar el usuario asociado al creador de la institución."
                    ).getWebExchangeBindException()
                ))
                .flatMap(user -> {
                    Institution institution = institutionMapper.toInstitution(saveInstitutionDto);
                    institution.setUserId(user.getId());
    
                    if (saveInstitutionDto.getImage() != null) {
                        String filename = UUID.randomUUID() + "-" + saveInstitutionDto.getImage().filename();
                        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
                        Path filePath = uploadDir.resolve(filename);
    
                        return Mono.fromCallable(() -> {
                            Files.createDirectories(uploadDir);
                            return filePath;
                        })
                        .flatMap(path -> saveInstitutionDto.getImage().transferTo(path))
                        .then(Mono.defer(() -> {
                            institution.setImage(baseurl + "/" + uploadPath + "/" + filename);
                            return institutionRepository.save(institution)
                                .map(institutionMapper::toInstitutionDto);
                        }));
                    } else {
                        return institutionRepository.save(institution)
                            .map(institutionMapper::toInstitutionDto);
                    }
                })
            );
    }

    @Override
    public Mono<Object> updateInstitution(String id, Principal principal, SaveInstitutionDto saveInstitutionDto, String baseUrl) {
        return institutionRepository.findById(id)
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id,
                    "institutionId",
                    "No hemos podido encontrar la institución indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException()
            ))
            .flatMap(institution -> {
                institution.setName(saveInstitutionDto.getName());

                if (saveInstitutionDto.getImage() != null) {
                    String filename = UUID.randomUUID() + "-" + saveInstitutionDto.getImage().filename();
                    Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
                    Path filePath = uploadDir.resolve(filename);

                    if (institution.getImage() != null) {
                        Path oldFilePath = Paths.get(uploadDir.toString(), institution.getImage().substring(institution.getImage().lastIndexOf("/") + 1));
                        try {
                            Files.deleteIfExists(oldFilePath);
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("Error al eliminar la imagen antigua: " + e.getMessage()));
                        }
                    }

                    return Mono.fromCallable(() -> {
                        Files.createDirectories(uploadDir);
                        return filePath;
                    })
                    .flatMap(path -> saveInstitutionDto.getImage().transferTo(path))
                    .then(Mono.defer(() -> {
                        institution.setImage(baseUrl + "/" + uploadPath + "/" + filename);
                        return institutionRepository.save(institution)
                            .map(institutionMapper::toInstitutionDto);
                    }));
                } else {
                    if (institution.getImage() != null) {
                        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
                        Path oldFilePath = Paths.get(uploadDir.toString(), institution.getImage().substring(institution.getImage().lastIndexOf("/") + 1));
                        try {
                            Files.deleteIfExists(oldFilePath);
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException("Error al eliminar la imagen antigua: " + e.getMessage()));
                        }
                    }

                    institution.setImage(null);
                    return institutionRepository.save(institution)
                        .map(institutionMapper::toInstitutionDto);
                }
            });
    }

    @Override
    public Mono<Boolean> deleteInstitution(String id) {
        return institutionRepository.findById(id)
            .flatMap(institution -> 
                institutionRepository.deleteById(id)
                    .then(Mono.just(true))
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    id, 
                    "institutionId", 
                    "No hemos podido encontrar la institución indicada. Te sugerimos que verifiques la información y lo intentes de nuevo."
                ).getWebExchangeBindException())
            );
    }
    
}
