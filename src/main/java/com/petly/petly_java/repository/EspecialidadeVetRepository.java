package com.petly.petly_java.repository;

import com.petly.petly_java.model.EspecialidadeVet;
import com.petly.petly_java.model.NomeEspecialidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EspecialidadeVetRepository extends JpaRepository<EspecialidadeVet, String> {

    List<EspecialidadeVet> findByNomeIn(List<NomeEspecialidade> nomes);

}