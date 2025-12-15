package it.unina.bugboard.repository;

import java.util.List;

import org.springframework.data.domain.Sort;

import it.unina.bugboard.model.Issue;

public interface DatabaseIssueInterface {
	Issue saveIssue(Issue i);
	List<Issue> findAll(Sort sort);
}
