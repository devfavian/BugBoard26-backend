package it.unina.bugboard.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unina.bugboard.dto.NewIssueRequest;
import it.unina.bugboard.model.Issue;
import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseIssueInterface;
import it.unina.bugboard.security.JwtService;
import it.unina.bugboard.services.IssueServicesInterface;
import it.unina.bugboard.services.UserServicesInterface;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bugboard/issue")
public class IssueController {

	DatabaseIssueInterface database;
	IssueServicesInterface issueServices;
	UserServicesInterface userServices;
		
	public IssueController(DatabaseIssueInterface database, IssueServicesInterface issueServices, UserServicesInterface userServices) {
		this.database = database;
		this.issueServices = issueServices;
		this.userServices = userServices;
	}
	
	@PostMapping("/new")
	public ResponseEntity<?> newIssue(@Valid @RequestBody NewIssueRequest request) {

	    Long userId = (Long) SecurityContextHolder.getContext()
	            .getAuthentication()
	            .getPrincipal();

	    Optional<User> userOpt = userServices.findUserById(userId);
	    if (userOpt.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    User currentUser = userOpt.get();
	    Issue issue = issueServices.createIssue(request, currentUser);

	    return ResponseEntity.ok(issue);
	}
}
