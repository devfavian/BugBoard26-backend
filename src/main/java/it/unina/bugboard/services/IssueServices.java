package it.unina.bugboard.services;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import it.unina.bugboard.dto.NewIssueRequest;
import it.unina.bugboard.model.Issue;
import it.unina.bugboard.model.Priority;
import it.unina.bugboard.model.State;
import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseIssueInterface;
import it.unina.bugboard.utils.AllowedField;

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
	
	public List<Issue> getAllIssues(String sort) {

	    String[] p = sort.split(",");
	    String rawField = p[0];
	    String direction = p.length > 1 ? p[1] : "asc";

	    AllowedField field;
	    try {
	        field = AllowedField.from(rawField);
	    } catch (IllegalArgumentException e) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
	    }

	    Sort s = direction.equalsIgnoreCase("desc")
	            ? Sort.by(field.getProperty()).descending()
	            : Sort.by(field.getProperty()).ascending();

	    return database.findAll(s);
	}

}
