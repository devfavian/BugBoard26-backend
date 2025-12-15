package it.unina.bugboard.dto;

import java.time.LocalDateTime;

import it.unina.bugboard.utils.Priority;
import it.unina.bugboard.utils.State;
import it.unina.bugboard.utils.Type;

public class IssueResponse {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private State state;
    private Type type;
    private String path;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long creatorId;

    public IssueResponse(Long id, String title, String description, Priority priority, State state, Type type, String path, LocalDateTime createdAt, LocalDateTime updatedAt, Long creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.state = state;
        this.type = type;
        this.path = path;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.creatorId = creatorId;
    }

    // --- getters only (l'api non può fare set) ---
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public State getState() { return state; }
    public Type getType() { return type; }
    public String getPath() { return path; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getCreatorId() { return creatorId; }
}
