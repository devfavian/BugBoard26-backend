package it.unina.bugboard.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.unina.bugboard.model.User;
import it.unina.bugboard.repository.DatabaseUserInterface;

class UserServicesLoginTest {

    @Test
    void login_returnsUser_whenCredentialsMatch() {
        DatabaseUserInterface database = Mockito.mock(DatabaseUserInterface.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserServices services = new UserServices(database, passwordEncoder);

        User user = new User();
        user.setEmail("user@example.com");
        user.setPsw("hashed");
        Mockito.when(database.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);

        Optional<User> result = services.login("user@example.com", "secret");

        assertTrue(result.isPresent());
        assertSame(user, result.get());
    }

    @Test
    void login_returnsEmpty_whenEmailNull() {
        DatabaseUserInterface database = Mockito.mock(DatabaseUserInterface.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserServices services = new UserServices(database, passwordEncoder);

        Optional<User> result = services.login(null, "secret");

        assertTrue(result.isEmpty());
        Mockito.verifyNoInteractions(database, passwordEncoder);
    }

    @Test
    void login_returnsEmpty_whenPasswordDoesNotMatch() {
        DatabaseUserInterface database = Mockito.mock(DatabaseUserInterface.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserServices services = new UserServices(database, passwordEncoder);

        User user = new User();
        user.setEmail("user@example.com");
        user.setPsw("hashed");
        Mockito.when(database.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        Optional<User> result = services.login("user@example.com", "wrong");

        assertTrue(result.isEmpty());
    }
}
