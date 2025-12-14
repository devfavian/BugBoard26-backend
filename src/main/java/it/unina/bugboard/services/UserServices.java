package it.unina.bugboard.services;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import it.unina.bugboard.dto.RegisterRequest;
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
		Optional<User> userOpt = database.findByEmail(email);
		
		if(userOpt.isEmpty()) return Optional.empty();
		
		User user = userOpt.get();
		if(!passwordEncoder.matches(rawPsw, user.getPsw()))	return Optional.empty();
		
		return Optional.of(user);
	}
	
	public User register(RegisterRequest request) {
		if(database.emailExist(request.getEmail())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered!");
		User u = new User();
		u.setEmail(request.getEmail());
		u.setPsw(passwordEncoder.encode(request.getPsw()));
		u.setRole(request.getRole());
		
		return database.saveUser(u);
	}
}
