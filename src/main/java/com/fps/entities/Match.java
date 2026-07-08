package com.fps.entities;

import com.fps.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String homeTeam;

    @Column(nullable = false)
    private String awayTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MatchStatus status = MatchStatus.SCHEDULED;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentMinute = 0;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Integer version;

    @PrePersist
    void onPrePersist() {
        LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
        if (startTime == null) {
            startTime = utcNow;
        }
        if (createdAt == null) {
            createdAt = utcNow;
        }
    }
}
