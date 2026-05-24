package com.petly.petly_java.repository;

import com.petly.petly_java.model.VeterinarioEspecie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VeterinarioEspecieRepository extends JpaRepository<VeterinarioEspecie, String> {
}
