package it.unina.bugboard.dto;

import it.unina.bugboard.utils.Role;

public class LoginResponse {

    private Long userID;
    private Role role;
    private String token;

    public LoginResponse(Long userID, Role role, String token) {
        this.userID = userID;
        this.role = role;
        this.setToken(token);
    }

    public Long getUserID() {
        return userID;
    }

    public Role getRole() {
        return role;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

