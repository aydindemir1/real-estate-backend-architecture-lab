package com.aydindemir.repository;

import com.aydindemir.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAuthRepository extends JpaRepository<Auth, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Auth> findByUsername(String username);

    Optional<Auth> findByUsernameAndPassword(String username, String password);
}
