package it.unina.bugboard.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
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
	
	public List<Issue> findAll(Sort sort){
		return jpa.findAll(sort);
	}
}
