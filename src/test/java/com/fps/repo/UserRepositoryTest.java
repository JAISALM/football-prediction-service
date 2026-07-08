package com.fps.repo;

import com.fps.entities.User;
import com.fps.FootballPredictionServiceApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FootballPredictionServiceApplication.class)
@ActiveProfiles("local")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsernameReturnsUser() {
        User user = userRepository.save(User.builder()
                .username("player1")
                .totalPoints(150)
                .build());

        Optional<User> found = userRepository.findByUsername("player1");

        assertTrue(found.isPresent());
        assertEquals("player1", found.get().getUsername());
        assertEquals(150, found.get().getTotalPoints());
    }

    @Test
    void findByUsernameReturnsEmptyForUnknownUser() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertTrue(found.isEmpty());
    }

    @Test
    void saveAndFindById() {
        User saved = userRepository.save(User.builder()
                .username("newplayer")
                .build());

        User found = userRepository.findById(saved.getId()).orElseThrow();

        assertEquals("newplayer", found.getUsername());
        assertNotNull(found.getCreatedAt());
    }

    @Test
    void defaultTotalPointsIs100() {
        User saved = userRepository.save(User.builder()
                .username("defaults")
                .build());

        User found = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals(100, found.getTotalPoints());
    }

    @Test
    void usernameIsUnique() {
        userRepository.save(User.builder().username("unique_user").build());

        assertThrows(Exception.class, () -> {
            userRepository.save(User.builder().username("unique_user").build());
            userRepository.flush();
        });
    }

    @Test
    void versionFieldExists() {
        User saved = userRepository.save(User.builder()
                .username("version_test")
                .build());

        User found = userRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(found.getVersion());
    }
}
