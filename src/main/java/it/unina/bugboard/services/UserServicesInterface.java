package it.unina.bugboard.services;

import java.util.Optional;

import it.unina.bugboard.model.User;

public interface UserServicesInterface {
	Optional<User> login(String email, String rawPsw);
}
