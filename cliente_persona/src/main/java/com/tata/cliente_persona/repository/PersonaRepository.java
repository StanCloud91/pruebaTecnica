package com.tata.cliente_persona.repository;

import com.tata.cliente_persona.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
        
    boolean existsByIdentificacion(String identificacion);
    
    boolean existsByIdentificacionAndIdNot(String identificacion, Long id);
} 