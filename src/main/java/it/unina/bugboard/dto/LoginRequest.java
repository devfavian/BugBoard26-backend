package it.unina.bugboard.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String psw;

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
}
