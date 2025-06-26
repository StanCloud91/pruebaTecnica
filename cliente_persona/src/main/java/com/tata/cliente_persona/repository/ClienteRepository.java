package com.tata.cliente_persona.repository;

import com.tata.cliente_persona.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
      
    boolean existsByIdentificacion(String identificacion);
    
    boolean existsByClienteId(String clienteId);
    
    boolean existsByIdentificacionAndIdNot(String identificacion, Long id);
    
    boolean existsByClienteIdAndIdNot(String clienteId, Long id);
} 