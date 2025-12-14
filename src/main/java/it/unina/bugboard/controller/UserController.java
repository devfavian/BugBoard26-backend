package it.unina.bugboard.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unina.bugboard.dto.LoginRequest;
import it.unina.bugboard.dto.LoginResponse;
import it.unina.bugboard.dto.RegisterRequest;
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
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {

	    Optional<User> userOpt = services.login(
	            req.getEmail(),
	            req.getPsw()
	    );

	    if (userOpt.isEmpty()) {
	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .build();
	    }

	    User u = userOpt.get();

	    LoginResponse response = new LoginResponse(
	            u.getId(),
	            u.getRole()
	    );

	    return ResponseEntity.ok(response);
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
		services.register(req);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}

