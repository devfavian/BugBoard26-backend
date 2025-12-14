package it.unina.bugboard.services;

import it.unina.bugboard.dto.NewIssueRequest;
import it.unina.bugboard.model.Issue;
import it.unina.bugboard.model.User;

public interface IssueServicesInterface {
	Issue createIssue(NewIssueRequest request, User currentUser);
}