package it.unina.bugboard.services;

import java.util.Optional;

import it.unina.bugboard.dto.RegisterRequest;
import it.unina.bugboard.model.User;

public interface UserServicesInterface {
	Optional<User> login(String email, String rawPsw);
	User register(RegisterRequest request);
	Optional<User> findUserById(Long id);
}
