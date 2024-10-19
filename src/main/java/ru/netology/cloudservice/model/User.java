package ru.netology.cloudservice.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import ru.netology.cloudservice.repository.UserRepository;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Embedded
    private Credentials credentials;

    @Embedded
    private TokenDto token;

    @Column(name = "create_dt", nullable = false, updatable = false, insertable = false)
    //@CreationTimestamp
    private LocalDateTime createDt;

    @Column(name = "last_activity_dt", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastActivityDt;

    @Embeddable
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Credentials implements Serializable {
        @Column(name = "username", nullable = false, unique = true)
        private String login;
        @Column(name = "password", nullable = false)
        private String password;
    }

    @Embeddable
    //@Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
    public static class TokenDto implements Serializable {
        @Column(name = "auth_token", nullable = false)
        private String authToken = "";

        public void invalidateToken(UserRepository userRepository, User user) {
            authToken = "";
            userRepository.save(user);
        }
    }
}

