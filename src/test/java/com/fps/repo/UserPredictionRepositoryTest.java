package com.fps.repo;

import com.fps.enums.QuestionType;
import com.fps.enums.WindowStatus;
import com.fps.entities.Match;
import com.fps.entities.PredictionWindow;
import com.fps.entities.User;
import com.fps.entities.UserPrediction;
import com.fps.FootballPredictionServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.BeforeEach;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FootballPredictionServiceApplication.class)
@ActiveProfiles("local")
@Transactional
class UserPredictionRepositoryTest {

    @Autowired
    private UserPredictionRepository userPredictionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PredictionWindowRepository predictionWindowRepository;

    @Autowired
    private PredictionWindowRepository predictionWindowRepo;

    @BeforeEach
    void setUp() {
        userPredictionRepository.deleteAll();
        predictionWindowRepository.deleteAll();
        userRepository.deleteAll();
        matchRepository.deleteAll();
    }

    @Test
    void findByUserIdAndWindowIdReturnsPrediction() {
        User user = userRepository.save(User.builder().username("predictor").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Arsenal")
                .awayTeam("Chelsea")
                .build());
        PredictionWindow window = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.GOAL)
                .build());

        userPredictionRepository.save(UserPrediction.builder()
                .user(user)
                .window(window)
                .predictedValue(true)
                .build());

        Optional<UserPrediction> found = userPredictionRepository.findByUserIdAndWindowId(
                user.getId(), window.getId());

        assertTrue(found.isPresent());
        assertTrue(found.get().getPredictedValue());
    }

    @Test
    void findByUserIdAndWindowIdReturnsEmptyForNoPrediction() {
        Optional<UserPrediction> found = userPredictionRepository.findByUserIdAndWindowId(
                "non-existent-user", "non-existent-window");

        assertTrue(found.isEmpty());
    }

    @Test
    void findByWindowIdWithPaginationReturnsPredictions() {
        User user1 = userRepository.save(User.builder().username("player1").build());
        User user2 = userRepository.save(User.builder().username("player2").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Liverpool")
                .awayTeam("ManCity")
                .build());
        PredictionWindow window = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.CORNER)
                .build());

        userPredictionRepository.save(UserPrediction.builder()
                .user(user1)
                .window(window)
                .predictedValue(true)
                .build());
        userPredictionRepository.save(UserPrediction.builder()
                .user(user2)
                .window(window)
                .predictedValue(false)
                .build());

        Page<UserPrediction> page = userPredictionRepository.findByWindowId(
                window.getId(), PageRequest.of(0, 10));

        assertEquals(2, page.getContent().size());
    }

    @Test
    void findByWindowIdWithPaginationAndEntityGraph() {
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Barcelona")
                .awayTeam("RealMadrid")
                .build());
        PredictionWindow window = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.CARD)
                .build());

        for (int i = 0; i < 5; i++) {
            User user = userRepository.save(User.builder().username("p" + i).build());
            userPredictionRepository.save(UserPrediction.builder()
                    .user(user)
                    .window(window)
                    .predictedValue(i % 2 == 0)
                    .build());
        }

        Page<UserPrediction> page = userPredictionRepository.findByWindowId(
                window.getId(), PageRequest.of(0, 2));

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    void findByUserIdReturnsPageWithEntityGraph() {
        User user = userRepository.save(User.builder().username("finder").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("PSG")
                .awayTeam("Marseille")
                .build());
        PredictionWindow window = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.GOAL)
                .build());

        userPredictionRepository.save(UserPrediction.builder()
                .user(user)
                .window(window)
                .predictedValue(true)
                .build());

        Page<UserPrediction> page = userPredictionRepository.findByUserId(
                user.getId(), PageRequest.of(0, 10));

        assertEquals(1, page.getContent().size());
        assertNotNull(page.getContent().get(0).getWindow());
    }

    @Test
    void countByUserIdReturnsTotal() {
        User user = userRepository.save(User.builder().username("counter").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("AC Milan")
                .awayTeam("Inter")
                .build());

        for (int i = 0; i < 3; i++) {
            PredictionWindow window = predictionWindowRepository.save(PredictionWindow.builder()
                    .match(match)
                    .windowIndex(i + 1)
                    .startMinute(i * 15)
                    .endMinute((i + 1) * 15)
                    .questionType(QuestionType.GOAL)
                    .build());

            userPredictionRepository.save(UserPrediction.builder()
                    .user(user)
                    .window(window)
                    .predictedValue(true)
                    .build());
        }

        long count = userPredictionRepository.countByUserId(user.getId());
        assertEquals(3, count);
    }

    @Test
    void countByUserIdAndPointsEarnedGreaterThan() {
        User user = userRepository.save(User.builder().username("scorer").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Dortmund")
                .awayTeam("Leverkusen")
                .build());

        PredictionWindow window1 = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.GOAL)
                .build());

        PredictionWindow window2 = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(2)
                .startMinute(15)
                .endMinute(30)
                .questionType(QuestionType.GOAL)
                .build());

        userPredictionRepository.save(UserPrediction.builder()
                .user(user)
                .window(window1)
                .predictedValue(true)
                .pointsEarned(10)
                .build());
        userPredictionRepository.save(UserPrediction.builder()
                .user(user)
                .window(window2)
                .predictedValue(false)
                .pointsEarned(0)
                .build());

        long correct = userPredictionRepository.countByUserIdAndPointsEarnedGreaterThan(user.getId(), 0);
        assertEquals(1, correct);
    }

    @Test
    void predictionWithPointsEarned() {
        User user = userRepository.save(User.builder().username("points_test").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("PSG")
                .awayTeam("Marseille")
                .build());
        PredictionWindow window = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.GOAL)
                .build());

        UserPrediction saved = userPredictionRepository.save(UserPrediction.builder()
                .user(user)
                .window(window)
                .predictedValue(true)
                .pointsEarned(10)
                .build());

        UserPrediction found = userPredictionRepository.findById(saved.getId()).orElseThrow();
        assertEquals(10, found.getPointsEarned());
    }

    @Test
    void createdAtIsSetOnPersist() {
        User user = userRepository.save(User.builder().username("time_test").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Dortmund")
                .awayTeam("Leverkusen")
                .build());
        PredictionWindow window = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.GOAL)
                .build());

        UserPrediction saved = userPredictionRepository.save(UserPrediction.builder()
                .user(user)
                .window(window)
                .predictedValue(true)
                .build());

        UserPrediction found = userPredictionRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(found.getCreatedAt());
    }

    @Test
    void versionFieldExists() {
        User user = userRepository.save(User.builder().username("version_test").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("version_match")
                .awayTeam("version_opponent")
                .build());
        PredictionWindow window = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.GOAL)
                .build());

        UserPrediction saved = userPredictionRepository.save(UserPrediction.builder()
                .user(user)
                .window(window)
                .predictedValue(true)
                .build());

        UserPrediction found = userPredictionRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(found.getVersion());
    }
}
