package org.danove.springcloud.msvc.curso.msvc_curso.restcontroller;

import jakarta.validation.Valid;
import org.danove.springcloud.msvc.curso.msvc_curso.models.entity.Curso;
import org.danove.springcloud.msvc.curso.msvc_curso.services.IEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class CursoRestController<E extends Curso, S extends IEntityService<E>> {
    @Autowired
    private S service;

    @GetMapping
    public List<E> findAll() {
        return this.service.findAll();
    }

    @GetMapping("/page/{page}")
    public Page<E> index(@PathVariable Integer page){
        return this.service.findAll(PageRequest.of(page, 4));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<E> entityOpt = this.service.findByID(id);
        if (entityOpt.isPresent())
            return ResponseEntity.ok(entityOpt.get());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("No existe el id: %d.", id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody E entity, BindingResult result) {
//		El RequestBody se necesita porque recibimos los datos en formato JSON y esta anotacion se encarga
//		de transformarla en un objeto cliente.

//		El BindingResult es el objeto que contiene todos los mensajes de error.

        if (result.hasErrors()) {
            return this.validar(result);
        }
        try {
            this.service.create(entity);
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    String.format(String.format("%s: %s.", e.getMessage(), e.getMostSpecificCause().getMessage())));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(entity);

    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@Valid @RequestBody E entity, BindingResult result, @PathVariable Long id) {

        if (result.hasErrors()) {
            return this.validar(result);
        }

        Optional<E> entityOld = this.service.findByID(entity.getId());
        if (entityOld.isPresent()) {
            entityOld.ifPresent(entityO -> entityO.setNombre(entity.getNombre()));
//            entityOld.ifPresent(entityO -> entityO.setApellido(entity.getApellido()));

//    		Tengo que hacer esta comprobacion porque si no editamos el email y los objetos
//    		cliente y clienteActual tienen el mismo voy a tener una excepcion de campo duplicado
//    		en la base de datos porque en el Entity le puse "Unique"
//            if (entityOld.get().getEmail().equalsIgnoreCase(entity.getEmail()))
//                entityOld.ifPresent(entityO -> entityO.setEmail(entity.getEmail()));

            try {
                this.service.create(entity);
            } catch (DataAccessException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(String.format("%s: %s.", e.getMessage(), e.getMostSpecificCause().getMessage()));
            }
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("no se encontro el \"id\": %d", entity.getId()));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        this.service.removeById(id);
    }

    @DeleteMapping
    public void remove(@RequestBody E entity) {
        this.service.removeByObject(entity);
    }

//    @SuppressWarnings("null")
    private ResponseEntity<?> validar(BindingResult result) {

        List<String> errores = result.getFieldErrors().stream()
                .map(error -> String.format("El campo %s: %s.", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        // -> solo si tenemos errores globales
        if (result.hasGlobalErrors()) {
            errores.add(Objects.requireNonNull(result.getGlobalError()).getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errores);
    }

}
