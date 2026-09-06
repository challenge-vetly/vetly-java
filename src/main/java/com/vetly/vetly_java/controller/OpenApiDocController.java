package com.vetly.vetly_java.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serve o {@code swagger.yaml} escrito a mao — o que documenta as regras de negocio
 * e traz os mocks do roteiro de teste — para o Swagger UI, ao lado da especificacao
 * gerada automaticamente pelo springdoc em {@code /v3/api-docs}.
 *
 * <p>O arquivo continua com uma unica copia, na raiz do repositorio; o
 * {@code processResources} do build.gradle o copia para {@code openapi/swagger.yaml}
 * nos resources. Um endpoint proprio e necessario porque
 * {@code spring.web.resources.add-mappings=false} desliga o handler de recursos estaticos.
 */
@RestController
public class OpenApiDocController {

    private static final MediaType APPLICATION_YAML = MediaType.parseMediaType("application/yaml");

    @GetMapping(value = "/swagger.yaml", produces = "application/yaml")
    public ResponseEntity<Resource> swaggerYaml() {
        Resource yaml = new ClassPathResource("openapi/swagger.yaml");
        if (!yaml.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(APPLICATION_YAML).body(yaml);
    }
}
