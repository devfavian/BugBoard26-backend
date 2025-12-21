package it.unina.bugboard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.unina.bugboard.dto.IssueResponse;
import it.unina.bugboard.dto.ModifyRequest;
import it.unina.bugboard.dto.NewIssueRequest;
import it.unina.bugboard.model.Issue;
import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseIssueInterface;
import it.unina.bugboard.services.IssueServicesInterface;
import it.unina.bugboard.services.UserServicesInterface;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/bugboard/issue")
public class IssueController {
	IssueServicesInterface issueServices;
	UserServicesInterface userServices;
		
	public IssueController(IssueServicesInterface issueServices, UserServicesInterface userServices) {
		this.issueServices = issueServices;
		this.userServices = userServices;
	}
	
	@PostMapping("/new")
	public ResponseEntity<IssueResponse> newIssue(@Valid @RequestBody NewIssueRequest request) {
		
	    Long userId = (Long) SecurityContextHolder.getContext()
	            .getAuthentication()
	            .getPrincipal();

	    Optional<User> userOpt = userServices.findUserById(userId);
	    if (userOpt.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    User currentUser = userOpt.get();
	    Issue created = issueServices.createIssue(request, currentUser);
	    IssueResponse response = new IssueResponse(created.getId());

	    return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/view")
	public List<IssueResponse> getIssuesByParam(@RequestParam String sort){
		return issueServices.getAllIssues(sort);
	}
	
	@PutMapping("/modify/{id}")
	public ResponseEntity<Void> modifyIssue(@PathVariable Long id, @RequestBody ModifyRequest request) {
		issueServices.modifyIssue(id, request);
	    return ResponseEntity.ok().build();
	}
	
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IssueResponse> uploadImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        Issue uploaded = issueServices.uploadIssueImage(id, file);
        IssueResponse response = new IssueResponse(uploaded.getId(), uploaded.getPath());
        return ResponseEntity.ok().body(response);
    }

}
