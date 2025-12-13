package it.unina.bugboard.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

import it.unina.bugboard.model.User;

@Repository
public class DatabaseUserRepository implements DatabaseUserInterface {
	
	private final JpaUserRepositoryInterface jpa;
	
	public DatabaseUserRepository(JpaUserRepositoryInterface jpa) {
		this.jpa = jpa;
	}
	
	@Override
	public Optional<User> login(User u) {
		return jpa.findByEmailAndPsw(u.getEmail(), u.getPsw());
	}
}