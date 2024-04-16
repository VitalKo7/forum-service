package ait.cohort34.security.filter;

import ait.cohort34.accounting.model.Role;
import ait.cohort34.post.dao.PostRepository;
import ait.cohort34.post.model.Post;
import ait.cohort34.security.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(60)
@RequiredArgsConstructor

public class DeletePostFilter implements Filter {

    final PostRepository postRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (checkEndpoint(request.getMethod(), request.getServletPath())) {

            User principal = (User) request.getUserPrincipal();

            String[] parts = request.getServletPath().split("/");
            String postId = parts[parts.length - 1];

            Post post = postRepository.findById(postId).orElse(null);
            if (post == null) {
                response.sendError(404, "Post not found");
                return;
            }

//            if (!principal.getName().equals(post.getAuthor()) && !principal.getRoles().contains(Role.MODERATOR.name())) {
            if (!(principal.getName().equals(post.getAuthor()) || principal.getRoles().contains(Role.MODERATOR.name()))) {
                response.sendError(403, "You are do not have permission to access this resource");
                return;
            }
        }

        chain.doFilter(req, resp);

    }

    private boolean checkEndpoint(String method, String path) {
        return HttpMethod.DELETE.matches(method) && path.matches("/forum/post/\\w+");
    }
}
