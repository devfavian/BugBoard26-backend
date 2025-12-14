package it.unina.bugboard.services;

import org.springframework.stereotype.Service;

import it.unina.bugboard.dto.NewIssueRequest;
import it.unina.bugboard.model.Issue;
import it.unina.bugboard.model.Priority;
import it.unina.bugboard.model.State;
import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseIssueInterface;

@Service
public class IssueServices implements IssueServicesInterface {
	
	private final DatabaseIssueInterface database;
	
	public IssueServices(DatabaseIssueInterface database) {
		this.database = database;
	}
	
	public Issue createIssue(NewIssueRequest request, User currentUser) {
		
		Issue i = new Issue();
		i.setTitle(request.getTitle());
		i.setDescription(request.getDescription());
		if(request.getPriority() == null)	i.setPriority(Priority.MEDIUM);
		else i.setPriority(request.getPriority());
		i.setPath(request.getPath());
		i.setType(request.getType());
		i.setState(State.TODO);
		i.setCreator(currentUser);
		
		return database.saveIssue(i);
	}
}
