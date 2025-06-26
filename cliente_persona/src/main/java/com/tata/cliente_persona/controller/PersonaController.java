package com.tata.cliente_persona.controller;

import com.tata.cliente_persona.dto.ApiResponse;
import com.tata.cliente_persona.dto.PersonaDTO;
import com.tata.cliente_persona.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PersonaController {
    
    private final PersonaService personaService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<PersonaDTO>>> getAllPersonas() {
        List<PersonaDTO> personas = personaService.getAllPersonas();
        return ResponseEntity.ok(ApiResponse.success(personas, "Personas obtenidas exitosamente"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PersonaDTO>> getPersonaById(@PathVariable Long id) {
        PersonaDTO persona = personaService.getPersonaById(id);
        return ResponseEntity.ok(ApiResponse.success(persona, "Persona obtenida exitosamente"));
    }
    
    
    @PostMapping
    public ResponseEntity<ApiResponse<PersonaDTO>> createPersona(@Valid @RequestBody PersonaDTO personaDTO) {
        PersonaDTO createdPersona = personaService.createPersona(personaDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdPersona, "Persona creada exitosamente"));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PersonaDTO>> updatePersona(@PathVariable Long id, 
                                                                @Valid @RequestBody PersonaDTO personaDTO) {
        PersonaDTO updatedPersona = personaService.updatePersona(id, personaDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedPersona, "Persona actualizada exitosamente"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePersona(@PathVariable Long id) {
        personaService.deletePersona(id);
        return ResponseEntity.ok(ApiResponse.success("Persona eliminada exitosamente"));
    }
} 