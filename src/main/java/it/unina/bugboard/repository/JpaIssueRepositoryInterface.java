package it.unina.bugboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unina.bugboard.model.Issue;

public interface JpaIssueRepositoryInterface extends JpaRepository<Issue,Long> {
//save
}
