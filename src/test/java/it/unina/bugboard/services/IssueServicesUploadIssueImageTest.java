package it.unina.bugboard.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import it.unina.bugboard.model.Issue;
import it.unina.bugboard.repository.DatabaseIssueInterface;

class IssueServicesUploadIssueImageTest {

    @TempDir
    Path tempDir;

    @Test
    void uploadIssueImage_savesFileAndUpdatesPath_whenValidImage() {
        DatabaseIssueInterface database = Mockito.mock(DatabaseIssueInterface.class);
        IssueServices services = new IssueServices(database, tempDir.toString(), "/images/issues");

        Issue issue = new Issue();
        issue.setId(42L);
        Mockito.when(database.findById(42L)).thenReturn(Optional.of(issue));
        Mockito.when(database.saveIssue(Mockito.any(Issue.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "content".getBytes(StandardCharsets.UTF_8)
        );

        Issue saved = services.uploadIssueImage(42L, file);

        assertTrue(saved.getPath().startsWith("/images/issues/42/"));
        String filename = saved.getPath().substring(saved.getPath().lastIndexOf('/') + 1);
        Path stored = tempDir.resolve("42").resolve(filename);
        assertTrue(Files.exists(stored));
    }

    @Test
    void uploadIssueImage_throwsWhenFileIsEmpty() {
        DatabaseIssueInterface database = Mockito.mock(DatabaseIssueInterface.class);
        IssueServices services = new IssueServices(database, tempDir.toString(), "/images/issues");

        Issue issue = new Issue();
        issue.setId(1L);
        Mockito.when(database.findById(1L)).thenReturn(Optional.of(issue));

        MultipartFile file = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> services.uploadIssueImage(1L, file));
        assertEquals("Empty file", ex.getMessage());
    }

    @Test
    void uploadIssueImage_throwsWhenImageTypeNotSupported() {
        DatabaseIssueInterface database = Mockito.mock(DatabaseIssueInterface.class);
        IssueServices services = new IssueServices(database, tempDir.toString(), "/images/issues");

        Issue issue = new Issue();
        issue.setId(2L);
        Mockito.when(database.findById(2L)).thenReturn(Optional.of(issue));

        MultipartFile file = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                "gif".getBytes(StandardCharsets.UTF_8)
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> services.uploadIssueImage(2L, file));
        assertEquals("Not supported", ex.getMessage());
    }
}
