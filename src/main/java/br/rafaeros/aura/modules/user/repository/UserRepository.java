package br.rafaeros.aura.modules.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.rafaeros.aura.modules.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("""
            SELECT u FROM User u
            JOIN FETCH u.company c
            LEFT JOIN FETCH c.settings s
            WHERE u.email = :email
            """)
    Optional<User> findyProfileByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
