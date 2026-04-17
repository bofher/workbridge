package com.ccp.WorkBridge.user.repo;

import com.ccp.WorkBridge.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User getByEmail(String email);
}
