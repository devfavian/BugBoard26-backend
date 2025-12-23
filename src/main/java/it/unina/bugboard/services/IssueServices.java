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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
		return database.saveIssue(toIssue(request, currentUser));
	}
	
	public List<IssueResponse> getAllIssues(String sort) {
	    String[] parts = StringManager.getFields(sort);   // es: ["createdAt","desc"]
	    String rawField = parts[0];

	    String direction = (parts.length >= 2 && !parts[1].isBlank())
	            ? parts[1]
	            : "asc";

	    AllowedField field = AllowedField.from(rawField);

	    Sort s = direction.equalsIgnoreCase("desc")
	            ? Sort.by(field.getProperty()).descending()
	            : Sort.by(field.getProperty()).ascending();

	    return database.findAll(s)
	            .stream()
	            .map(IssueServices::toResponse)
	            .toList();
	}
	
	public Issue modifyIssue(Long id, ModifyRequest request) {
		Issue issue = database.findById(id).orElseThrow(
				()-> new EntityNotFoundException("Issue not found"));
		
		applyChanges(issue, request);
		return database.saveIssue(issue);
	}
	
	public Issue uploadIssueImage(Long id, MultipartFile file) {
        Issue issue = database.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue " + id + " not found"));

        if (file == null || file.isEmpty())	throw new IllegalArgumentException("Empty file");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) throw new IllegalArgumentException("Upload an image file");

        String ext = guessExtension(contentType);
        String filename = UUID.randomUUID() + ext;    
        
        Path issueDir = uploadRoot.resolve(String.valueOf(id));
        Path destination = issueDir.resolve(filename).normalize();
        
        // sicurezza: il path deve restare dentro issueDir
        if (!destination.startsWith(issueDir.normalize())) {
            throw new IllegalArgumentException("Invalid file path");
        }
            
        String oldPath = issue.getPath(); 	//se il file
        String publicPath = UriComponentsBuilder
            .fromPath(publicBase)
            .pathSegment(String.valueOf(id))
            .pathSegment(filename)
            .toUriString();
        
        try {
            Files.createDirectories(issueDir);

            // 1) salva nuovo file (prima)
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            // 2) aggiorna DB
            issue.setPath(publicPath);
            Issue saved = database.saveIssue(issue);

            // 3) elimina vecchio file (dopo)
            deleteOldImageIfPresent(oldPath);

            return saved;

        } catch (IOException e) {
            // se qualcosa va male dopo la scrittura, prova a pulire il file appena scritto
            try { Files.deleteIfExists(destination); } catch (IOException ignored) {}
            throw new IllegalStateException("Unable to save issue image", e);
        }
    }
	  
	public Resource getIssueImage(Long id) {
	    Issue issue = database.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Issue not found"));

	    String publicPath = issue.getPath();
	    if (publicPath == null || publicPath.isBlank()) throw new EntityNotFoundException("No image for this issue");

	    String prefix = UriComponentsBuilder.fromPath(publicBase).path("/").toUriString();
	    if (!publicPath.startsWith(prefix)) throw new IllegalArgumentException("Invalid image path");

	    String relative = publicPath.substring(prefix.length()); // es: "{id}/{file}"
	    Path file = uploadRoot.resolve(relative).normalize();

	    if (!file.startsWith(uploadRoot.normalize())) throw new IllegalArgumentException("Invalid image path");

	    if (!Files.exists(file)) throw new EntityNotFoundException("Image file not found");

	    try {
	        return new UrlResource(file.toUri());
	    } catch (Exception e) {
	        throw new IllegalStateException("Cannot load image", e);
	    }
	}
    

    /*		-----		*/
    /*		UTILS		*/
    /*		-----		*/
    
    
	public Issue toIssue(NewIssueRequest request, User creator) {
	    Issue issue = new Issue();
	    issue.setTitle(request.getTitle());
	    issue.setDescription(request.getDescription());
	    issue.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
	    issue.setType(request.getType());
	    issue.setState(State.TODO);
	    issue.setCreator(creator);
	    
	    return issue;
	}
	
	private static IssueResponse toResponse(Issue i) {
	    return new IssueResponse(
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
	    );
	}
	
	private void applyChanges(Issue issue, ModifyRequest request) {		//if camp are not passed, i don't want to set it null! (so != null)
	    if (request.getTitle() != null) issue.setTitle(request.getTitle());
	    if (request.getDescription() != null) issue.setDescription(request.getDescription());
	    if (request.getPriority() != null) issue.setPriority(request.getPriority());
	    if (request.getType() != null) issue.setType(request.getType());
	    if (request.getState() != null) issue.setState(request.getState());
	}
	
	private void deleteOldImageIfPresent(String oldPath) throws IOException {
	    if (oldPath == null || oldPath.isBlank()) return;

	    String prefix = UriComponentsBuilder.fromPath(publicBase).path("/").toUriString();
	    if (!oldPath.startsWith(prefix)) return;

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
