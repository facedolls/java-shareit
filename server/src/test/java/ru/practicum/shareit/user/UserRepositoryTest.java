package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    protected void setUp() {
        userRepository.save(new User(null, "Paul", "paul@ya.ru"));
    }

    @DisplayName("Should show exists or not by email")
    @Test
    public void existsByEmail() {
        boolean resultOne = userRepository.existsByEmail("paul@ya.ru");

        assertTrue(resultOne);

        boolean resultTwo = userRepository.existsByEmail("qwerty@ya.ru");

        assertFalse(resultTwo);
    }

    @AfterEach
    protected void deleteUsers() {
        userRepository.deleteAll();
    }
}
