package com.tata.cliente_persona.service;

import com.tata.cliente_persona.dto.PersonaDTO;
import com.tata.cliente_persona.entity.Persona;
import com.tata.cliente_persona.exception.DuplicateResourceException;
import com.tata.cliente_persona.exception.ResourceNotFoundException;
import com.tata.cliente_persona.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaService {
    
    private final PersonaRepository personaRepository;
    
    public List<PersonaDTO> getAllPersonas() {
        List<Persona> personas = personaRepository.findAll();
        return personas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PersonaDTO getPersonaById(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));
        return convertToDTO(persona);
    }
    
    public PersonaDTO createPersona(PersonaDTO personaDTO) {
        if (personaRepository.existsByIdentificacion(personaDTO.getIdentificacion())) {
            throw new DuplicateResourceException("Persona", "identificación", personaDTO.getIdentificacion());
        }
        
        Persona persona = convertToEntity(personaDTO);
        Persona savedPersona = personaRepository.save(persona);
        return convertToDTO(savedPersona);
    }
    
    public PersonaDTO updatePersona(Long id, PersonaDTO personaDTO) {
        Persona existingPersona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));
        
        if (personaRepository.existsByIdentificacionAndIdNot(personaDTO.getIdentificacion(), id)) {
            throw new DuplicateResourceException("Persona", "identificación", personaDTO.getIdentificacion());
        }
        
        // Actualizar campos
        existingPersona.setNombre(personaDTO.getNombre());
        existingPersona.setGenero(personaDTO.getGenero());
        existingPersona.setEdad(personaDTO.getEdad());
        existingPersona.setIdentificacion(personaDTO.getIdentificacion());
        existingPersona.setDireccion(personaDTO.getDireccion());
        existingPersona.setTelefono(personaDTO.getTelefono());
        
        Persona updatedPersona = personaRepository.save(existingPersona);
        return convertToDTO(updatedPersona);
    }
    
    public void deletePersona(Long id) {
        if (!personaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Persona", "id", id);
        }
        personaRepository.deleteById(id);
    }
    
    private PersonaDTO convertToDTO(Persona persona) {
        PersonaDTO dto = new PersonaDTO();
        BeanUtils.copyProperties(persona, dto);
        return dto;
    }
    
    private Persona convertToEntity(PersonaDTO dto) {
        Persona persona = new Persona();
        BeanUtils.copyProperties(dto, persona);
        return persona;
    }
} 