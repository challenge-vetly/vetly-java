package com.petly.petly_java.repository;

import com.petly.petly_java.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarioRepository extends JpaRepository<Veterinario, String> {
}
