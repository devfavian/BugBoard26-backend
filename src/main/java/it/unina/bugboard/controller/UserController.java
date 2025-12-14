package it.unina.bugboard.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unina.bugboard.dto.LoginRequest;
import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseUserInterface;
import it.unina.bugboard.services.UserServicesInterface;

@RestController
@RequestMapping("/bugboard/users")
public class UserController {
	DatabaseUserInterface database;
	UserServicesInterface services;
	
	public UserController(DatabaseUserInterface database, UserServicesInterface services) {
		this.database = database;
		this.services = services;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		Optional<User> userOpt = services.login(request.getEmail(), request.getPsw());
		
		if(userOpt.isEmpty()) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Credenziali non valide");
		}
		
		User u = userOpt.get();
		
		return ResponseEntity
				.ok(Map.of(
					"userID", u.getId(),
					"role", u.getRole()
						));
	}
}
