package it.unina.bugboard.dto;

import it.unina.bugboard.model.Role;

public class LoginResponse {

    private Long userID;
    private Role role;

    public LoginResponse(Long userID, Role role) {
        this.userID = userID;
        this.role = role;
    }

    public Long getUserID() {
        return userID;
    }

    public Role getRole() {
        return role;
    }
}

