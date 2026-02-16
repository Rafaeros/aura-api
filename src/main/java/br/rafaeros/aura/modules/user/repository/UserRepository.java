package br.rafaeros.aura.modules.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import br.rafaeros.aura.modules.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.company c LEFT JOIN FETCH c.settings WHERE u.email = :email")
    Optional<User> findProfileByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.company c LEFT JOIN FETCH c.settings WHERE u.id = :id")
    Optional<User> findProfileById(@Param("id") Long id);
}