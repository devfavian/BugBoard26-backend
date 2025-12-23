package it.unina.bugboard.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if (email == null || rawPsw == null) return Optional.empty();

        return database.findByEmail(email).filter(u -> passwordEncoder.matches(rawPsw, u.getPsw()));	//filter tiene il valore solo se la condizione è vera
        																								//in questo caso se è empty ritorna Optional.empty()
    }
	
	public User register(RegisterRequest request) {
		if(database.emailExist(request.getEmail())) throw new IllegalArgumentException("Email is already registered");
		
		User u = new User();
		u.setEmail(request.getEmail());
		u.setPsw(passwordEncoder.encode(request.getPsw()));
		u.setRole(request.getRole());
		
		return database.saveUser(u);
	}
	
	public Optional<User> findUserById(Long id) {
		return database.findUserById(id);
	}
}
