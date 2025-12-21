package it.unina.bugboard.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import it.unina.bugboard.dto.IssueResponse;
import it.unina.bugboard.dto.ModifyRequest;
import it.unina.bugboard.dto.NewIssueRequest;
import it.unina.bugboard.model.Issue;
import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseIssueInterface;
import it.unina.bugboard.utils.AllowedField;
import it.unina.bugboard.utils.Priority;
import it.unina.bugboard.utils.State;
import it.unina.bugboard.utils.StringManager;
import jakarta.persistence.EntityNotFoundException;

@Service
public class IssueServices implements IssueServicesInterface {
	
	private final DatabaseIssueInterface database;
    private final Path uploadRoot;
    private final String publicBase;
	
	public IssueServices(DatabaseIssueInterface database, @Value("${app.upload.root}") String uploadRoot, @Value("${app.upload.public-base:/images/issues}") String publicBase) {
		this.database = database;
		this.uploadRoot = Paths.get(uploadRoot);
		this.publicBase = publicBase;
	}
	
	public Issue createIssue(NewIssueRequest request, User currentUser) {
		
		User u = new User();
		u.setId(currentUser.getId());
		
		Issue i = new Issue();
		i.setTitle(request.getTitle());
		i.setDescription(request.getDescription());
		if(request.getPriority() == null)	i.setPriority(Priority.MEDIUM);
		else i.setPriority(request.getPriority());
		i.setType(request.getType());
		i.setState(State.TODO);
		i.setCreator(u);
		
		return database.saveIssue(i);
	}
	
	public List<IssueResponse> getAllIssues(String sort) {
		
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

	    return database.findAll(s)			//trasformo issue in issue response, così che non debba passare campi sensibili come la password
	            .stream()
	            .map(i -> new IssueResponse(
	                    i.getId(),
	                    i.getTitle(),
	                    i.getDescription(),
	                    i.getPriority(),
	                    i.getState(),
	                    i.getType(),
	                    i.getPath(),
	                    i.getCreatedAt(),
	                    i.getUpdatedAt(),
	                    i.getCreator().getId()
	            )).toList();
	}
	
	public Issue modifyIssue(Long id, ModifyRequest request) {
		Issue issue = database.findById(id).orElseThrow(
				()-> new EntityNotFoundException("Issue not found"));
		
		issue.setTitle(request.getTitle());
		issue.setDescription(request.getDescription());
		issue.setPriority(request.getPriority());
		issue.setType(request.getType());
		issue.setState(request.getState());
		
		return database.saveIssue(issue);
	}
	
	@Transactional
	public Issue uploadIssueImage(Long id, MultipartFile file) {
        Issue issue = database.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue con id " + id + " non trovata"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File vuoto");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Carica un file immagine");
        }

        try {
        	// 1) crea directory uploads/issues/{id}
        	Path issueDir = uploadRoot.resolve(String.valueOf(id));
        	Files.createDirectories(issueDir);

        	// 2) cancella vecchio file se esiste (1 immagine per issue)
        	deleteOldImageIfPresent(issue);

        	// 3) salva nuovo file
        	String ext = guessExtension(contentType);
        	String filename = UUID.randomUUID() + ext;
        	Path destination = issueDir.resolve(filename).normalize();

        	try (InputStream in = file.getInputStream()) {
        	    Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        	}

        	// 4) salva path "pubblico" nel DB (URL relativo)
        	String publicPath = UriComponentsBuilder
        	        .fromPath(publicBase)
        	        .pathSegment(String.valueOf(id))
        	        .pathSegment(filename)
        	        .toUriString();

        	issue.setPath(publicPath);

            return database.saveIssue(issue);

        } catch (IOException e) {
            throw new RuntimeException("Errore salvataggio immagine", e);
        }
    }
	
	private void deleteOldImageIfPresent(Issue issue) throws IOException {
	    String oldPath = issue.getPath();
	    if (oldPath == null || oldPath.isBlank()) return;

	    // se nel DB salvi "/images/issues/{id}/{file}"
	    String prefix = UriComponentsBuilder
	            .fromPath(publicBase)
	            .path("/")
	            .toUriString();

	    if (!oldPath.startsWith(prefix)) return;

	    // trasforma URL relativo in path su disco:
	    // /images/issues/3/x.jpg -> uploads/issues/3/x.jpg
	    String relative = oldPath.substring(prefix.length());
	    Path oldFile = uploadRoot.resolve(relative).normalize();

	    Files.deleteIfExists(oldFile);
	}
    
    private static String guessExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Not supported");
        };
    }

}
