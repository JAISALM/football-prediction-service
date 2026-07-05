package com.fps.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_predictions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "window_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "window_id", nullable = false)
    private PredictionWindow window;

    @Column(nullable = false)
    private Boolean predictedValue;

    private Integer pointsEarned;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
