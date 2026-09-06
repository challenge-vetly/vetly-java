package com.vetly.vetly_java.controller;

import com.vetly.vetly_java.dto.TutorConsentimentoRequest;
import com.vetly.vetly_java.dto.TutorConsentimentoResponse;
import com.vetly.vetly_java.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutores/me")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping("/consentimento")
    public ResponseEntity<TutorConsentimentoResponse> readConsentimento() {
        return new ResponseEntity<>(tutorService.lerConsentimento(), HttpStatus.OK);
    }

    @PutMapping("/consentimento")
    public ResponseEntity<TutorConsentimentoResponse> updateConsentimento(@Valid @RequestBody TutorConsentimentoRequest request) {
        return new ResponseEntity<>(tutorService.atualizarConsentimento(request), HttpStatus.OK);
    }
}
