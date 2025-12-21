package it.unina.bugboard.dto;

import it.unina.bugboard.utils.Priority;
import it.unina.bugboard.utils.State;
import it.unina.bugboard.utils.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ModifyRequest {
	
	private String title;
	private String description;
	private Priority priority;		
	private Type type;	
	private State state;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
