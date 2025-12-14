package it.unina.bugboard.repository;

import it.unina.bugboard.model.Issue;

public interface DatabaseIssueInterface {
	Issue saveIssue(Issue i);
}
