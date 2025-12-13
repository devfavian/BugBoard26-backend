package it.unina.bugboard.repository;

import java.util.Optional;

import it.unina.bugboard.model.User;

public interface DatabaseUserInterface {
	Optional<User> login(User u);
}
