package com.fps.model;

import com.fps.enums.QuestionType;
import com.fps.enums.WindowStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_windows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PredictionWindow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    private Integer windowIndex;

    @Column(nullable = false)
    private Integer startMinute;

    @Column(nullable = false)
    private Integer endMinute;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WindowStatus status = WindowStatus.OPEN;

    private Boolean resultValue;

    private LocalDateTime resolvedAt;
}
