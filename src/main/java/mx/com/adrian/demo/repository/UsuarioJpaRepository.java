package mx.com.adrian.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.com.adrian.demo.entity.UsuarioEntity;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Integer> {
    Optional<UsuarioEntity> findByUsername(String username);
}