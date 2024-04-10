package ait.cohort34.post.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = {"user", "dateCreated"})
@NoArgsConstructor
public class Comment {
    @Setter
    String user;
    @Setter
    String message;
    LocalDateTime dateCreated = LocalDateTime.now();
    int likes;

    public Comment(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public void addLike() {
        likes++;
    }
}
