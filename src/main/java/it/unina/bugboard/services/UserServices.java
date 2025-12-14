package it.unina.bugboard.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseUserInterface;

@Service
public class UserServices implements UserServicesInterface {
	
	private final DatabaseUserInterface database;
	private final PasswordEncoder passwordEncoder;
	
    public UserServices(DatabaseUserInterface database, PasswordEncoder passwordEncoder) {
        this.database = database;
        this.passwordEncoder = passwordEncoder;
    }
	
	public Optional<User> login(String email, String rawPsw) {
		Optional<User> userOpt = database.login(email);
		
		if(userOpt.isEmpty()) return Optional.empty();
		
		User user = userOpt.get();
		if(!passwordEncoder.matches(rawPsw, user.getPsw()))	return Optional.empty();
		
		return Optional.of(user);
	}
}
