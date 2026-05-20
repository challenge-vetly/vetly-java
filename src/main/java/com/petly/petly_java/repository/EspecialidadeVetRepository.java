package com.petly.petly_java.repository;

import com.petly.petly_java.model.EspecialidadeVet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EspecialidadeVetRepository extends JpaRepository<EspecialidadeVet, String> {
}
