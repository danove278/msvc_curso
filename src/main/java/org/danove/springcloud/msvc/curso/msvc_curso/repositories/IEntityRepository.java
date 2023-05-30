package org.danove.springcloud.msvc.curso.msvc_curso.repositories;

import org.danove.springcloud.msvc.curso.msvc_curso.models.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEntityRepository<E extends Curso> extends JpaRepository<E, Long> {
}