package it.unina.bugboard.repository;

import java.util.Optional;

import it.unina.bugboard.model.User;

public interface DatabaseUserInterface {
	Optional<User> findByEmail(String email);
	User saveUser(User u);
	boolean emailExist(String email);
	Optional<User> findUserById(Long id);
}
