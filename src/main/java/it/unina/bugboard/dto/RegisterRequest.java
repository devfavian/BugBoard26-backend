package it.unina.bugboard.dto;

import it.unina.bugboard.model.Role;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String psw;
    
    @NotBlank
    private Role role;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPsw() {
		return psw;
	}

	public void setPsw(String psw) {
		this.psw = psw;
	}
	
		public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
