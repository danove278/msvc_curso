package org.danove.springcloud.msvc.curso.msvc_curso.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IEntityService<T> {
    List<T> findAll();

    Optional<T> findByID(Long id);

    T create(T entity);

    void removeById(Long id);

    void removeByObject(T entity);


    // import org.springframework.data.domain.Page;
    // import org.springframework.data.domain.Pageable;
    Page<T> findAll(Pageable pageable);
}