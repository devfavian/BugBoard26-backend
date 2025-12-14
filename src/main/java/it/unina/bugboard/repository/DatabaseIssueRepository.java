package it.unina.bugboard.repository;

import org.springframework.stereotype.Repository;

import it.unina.bugboard.model.Issue;

@Repository
public class DatabaseIssueRepository implements DatabaseIssueInterface {
	
	private final JpaIssueRepositoryInterface jpa;
	
	public DatabaseIssueRepository(JpaIssueRepositoryInterface jpa) {
		this.jpa = jpa;
	}
	
	public Issue saveIssue(Issue i) {
		return jpa.save(i);
	}
}
