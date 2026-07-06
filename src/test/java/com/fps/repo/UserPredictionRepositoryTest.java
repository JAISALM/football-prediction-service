package com.fps.repo;

import com.fps.enums.MatchStatus;
import com.fps.enums.QuestionType;
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

import java.util.List;
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
    void findByWindowIdReturnsAllPredictionsForWindow() {
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

        List<UserPrediction> predictions = userPredictionRepository.findByWindowId(window.getId());

        assertEquals(2, predictions.size());
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
    void userPredictionUniqueConstraint() {
        User user = userRepository.save(User.builder().username("constraint_test").build());
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Milan")
                .awayTeam("Inter")
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

        assertThrows(Exception.class, () -> {
            userPredictionRepository.save(UserPrediction.builder()
                    .user(user)
                    .window(window)
                    .predictedValue(false)
                    .build());
            userPredictionRepository.flush();
        });
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
}
