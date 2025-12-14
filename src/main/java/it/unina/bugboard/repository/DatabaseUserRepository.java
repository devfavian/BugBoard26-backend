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
	public Optional<User> findByEmail(String email) {
		return jpa.findByEmail(email);
	}
	
	@Override
	public User saveUser(User u) {
		return jpa.save(u);
	}
	
	@Override
	public boolean emailExist(String email) {
		return jpa.existsByEmail(email);
	}
}