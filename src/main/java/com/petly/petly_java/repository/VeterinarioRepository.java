package com.petly.petly_java.repository;

import com.petly.petly_java.model.NomeEspecialidade;
import com.petly.petly_java.model.Veterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VeterinarioRepository extends JpaRepository<Veterinario, String> {

    // Busca veterinários que possuem a especialidade informada (por nome do enum)
    @Query("""
            SELECT v FROM Veterinario v
            JOIN v.especialidades ve
            JOIN ve.especialidade e
            WHERE e.nome = :especialidade
            """)
    Page<Veterinario> findByEspecialidade(
            @Param("especialidade") NomeEspecialidade especialidade,
            Pageable pageable
    );
}