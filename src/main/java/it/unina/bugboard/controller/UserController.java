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
import it.unina.bugboard.security.JwtService;
import it.unina.bugboard.services.UserServicesInterface;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bugboard")
public class UserController {
	UserServicesInterface services;
	JwtService jwtServices;
	
	public UserController(UserServicesInterface services, JwtService jwtServices) {
		this.services = services;
		this.jwtServices = jwtServices;
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

	    Optional<User> userOpt = services.login(
	            request.getEmail(),
	            request.getPsw()
	    );

	    if (userOpt.isEmpty()) {
	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .build();
	    }

	    User u = userOpt.get();
        String token = jwtServices.createToken(u.getId(), u.getRole().name());
        LoginResponse response = new LoginResponse(u.getId(), u.getRole(), token);

        return ResponseEntity.ok(response);
	}
	
	@PostMapping("/admin/register")
	public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
		services.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}

