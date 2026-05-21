package com.petly.petly_java.repository;

import com.petly.petly_java.model.Especie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecieRepository extends JpaRepository<Especie, String> {
}
