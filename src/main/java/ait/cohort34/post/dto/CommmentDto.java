package ait.cohort34.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommmentDto {
    String user;
    String message;
    LocalDateTime dateCreated;
    Integer likes;
}
