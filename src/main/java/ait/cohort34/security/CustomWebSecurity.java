package ait.cohort34.security;

import ait.cohort34.post.dao.PostRepository;
import ait.cohort34.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomWebSecurity {
    final PostRepository postRepository;

    public boolean checkPostAuthor(String postId, String userName) {
        Post post = postRepository.findById(postId).orElse(null);

        return post != null && post.getAuthor().equals(userName);
    }
}
