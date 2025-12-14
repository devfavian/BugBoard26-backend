package it.unina.bugboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unina.bugboard.model.User;

public interface JpaUserRepositoryInterface extends JpaRepository<User,Long> {
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	Optional<User> findById(Long id);
	//save
}

