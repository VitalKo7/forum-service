package ait.cohort34.post.service;

import ait.cohort34.post.dao.PostRepository;
import ait.cohort34.post.dto.DatePeriodDto;
import ait.cohort34.post.dto.NewCommentDto;
import ait.cohort34.post.dto.NewPostDto;
import ait.cohort34.post.dto.PostDto;
import ait.cohort34.post.dto.exceptions.PostNotFoundException;
import ait.cohort34.post.model.Comment;
import ait.cohort34.post.model.Post;
import ait.cohort34.post.service.logging.PostLogger;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//@Slf4j(topic = "Post Service ")
public class PostServiceImpl implements PostService {

    final PostRepository postRepository;
    final ModelMapper modelMapper;

    @Override
    public PostDto addNewPost(String author, NewPostDto newPostDto) {
        Post post = modelMapper.map(newPostDto, Post.class);
        post.setAuthor(author);
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto findPostById(String id) {
//        log.info("post with id: {}, requested ", id);
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto removePost(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        postRepository.delete(post);
        return modelMapper.map(post, PostDto.class);
    }

    @PostLogger
    @Override
    public PostDto updatePost(String id, NewPostDto newPostDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);

        String content = newPostDto.getContent();
        if (content != null) {
            post.setContent(content);
        }

        String title = newPostDto.getTitle();
        if (title != null) {
            post.setTitle(title);
        }

        Set<String> tags = newPostDto.getTags();
        if (tags != null) {
//            post.getTags().addAll(tags);

//            tags.forEach(tag -> post.addTag(tag));
            tags.forEach(post::addTag);
        }

        post = postRepository.save(post);

        return modelMapper.map(post, PostDto.class);
    }

//    @PostLogger(enabled = false)
    @PostLogger
    @Override
    public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {

        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);

        Comment comment = new Comment(author, newCommentDto.getMessage());

        post.addComment(comment);
        post = postRepository.save(post);

        return modelMapper.map(post, PostDto.class);
    }

    @PostLogger
    @Override
    public void addLike(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        post.addLike();
        postRepository.save(post);
    }

    @Override
    public Iterable<PostDto> findPostsByAuthor(String author) {
        return postRepository
                .findByAuthorIgnoreCase(author)
                .map(p -> modelMapper.map(p, PostDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<PostDto> findPostsByTags(Set<String> tags) {
        return postRepository
                .findByTagsInIgnoreCase(tags)
                .map(p -> modelMapper.map(p, PostDto.class))
                .toList();
    }

    @Override
    public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto) {
        return postRepository.findByDateCreatedBetween(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
                .map(p -> modelMapper.map(p, PostDto.class))
                .collect(Collectors.toList());
    }
}
