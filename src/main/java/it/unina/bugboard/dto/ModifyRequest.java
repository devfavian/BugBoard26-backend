package it.unina.bugboard.dto;

import it.unina.bugboard.utils.Priority;
import it.unina.bugboard.utils.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ModifyRequest {
	@NotBlank
	private String title;

	@NotBlank
	private String description;
	
	private Priority priority;
	
	private String path;
	
	@NotNull
	private Type type;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
