package it.unina.bugboard.services;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import it.unina.bugboard.dto.NewIssueRequest;
import it.unina.bugboard.model.Issue;
import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseIssueInterface;
import it.unina.bugboard.utils.AllowedField;
import it.unina.bugboard.utils.Priority;
import it.unina.bugboard.utils.State;
import it.unina.bugboard.utils.StringManager;

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
		
	    if (sort == null || sort.isBlank()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing sort param");
	    }

	    String rawField = StringManager.getElement(sort, 0);
	    String direction;
	    
	    if(StringManager.getFields(sort).length <= 1) direction = "asc";		//controllo che mi sia passato il campo dell'ordinamento
	    else direction = StringManager.getElement(sort, 1);
	    
	    if (direction == null || direction.isBlank()) direction = "asc";		//nel caso in cui fosse "", non sarebbe null, ma creerebbe problemi

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
