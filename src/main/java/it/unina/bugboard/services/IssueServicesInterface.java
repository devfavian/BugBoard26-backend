package it.unina.bugboard.services;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import it.unina.bugboard.dto.IssueResponse;
import it.unina.bugboard.dto.ModifyRequest;
import it.unina.bugboard.dto.NewIssueRequest;
import it.unina.bugboard.model.Issue;
import it.unina.bugboard.model.User;

public interface IssueServicesInterface {
	Issue createIssue(NewIssueRequest request, User currentUser);
	List<IssueResponse> getAllIssues(String sort);
	Issue modifyIssue(Long id, ModifyRequest request);
	Issue uploadIssueImage(Long id, MultipartFile file);
	ResponseEntity<Resource> getIssueImage(Long id);
}