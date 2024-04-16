package ait.cohort34.security.filter;

import ait.cohort34.accounting.dao.UserAccountRepository;
import ait.cohort34.accounting.model.Role;
import ait.cohort34.accounting.model.UserAccount;
import ait.cohort34.security.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(10)
public class AuthenticationFilter implements Filter {

    final UserAccountRepository userAccountRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

//        System.out.println(request.getMethod());
//        System.out.println(request.getServletPath());
//        System.out.println(request.getHeader("Authorization"));

        if (checkEndpoint(request.getMethod(), request.getServletPath())) {
            try {
                String[] credentials = getCredentials(request.getHeader("Authorization"));

                UserAccount userAccount = userAccountRepository.findById(credentials[0]).orElseThrow(RuntimeException::new);

                if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
                    throw new RuntimeException();
                }

                Set<String> roles = userAccount.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet());

                request = new WrappedRequest(request, userAccount.getLogin(), roles);

            } catch (Exception e) {
//                response.setStatus(401);
                response.sendError(401);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String path) {

//        Version #1
//        if (HttpMethod.POST.matches(method) && path.matches("/account/register")
//        || path.matches("/forum/posts/\\w+(/\\w+)?")) {
//            return false;
//        }

//        Version #2
//        if ("POST".equalsIgnoreCase(method) && "/account/register".equalsIgnoreCase(path)) {
//            return false;
//        }

//        if (("/forum/posts/tags").equalsIgnoreCase(path) || ("/forum/posts/period").equalsIgnoreCase(path)) {
//            return false;
//        }
//        if (path.startsWith("/forum/posts/tags")) {
//            return false;
//        }

//        if (HttpMethod.GET.matches(method)) {
//        }
//        if ("GET".equalsIgnoreCase(method) && path.contains("/forum/posts/author/")) {
//            return false;
//        }

//        String[] pathParts = path.split("/"); // check by pathParts[0]=xxx, pathParts[1]=xxx,  pathParts[2]=xxx,

//        Version #1
        return !(
                (HttpMethod.POST.matches(method) && path.matches("/account/register")
                        || path.matches("/forum/posts/\\w+(/\\w+)?"))
        );
//        return true;
    }

    private String[] getCredentials(String authorization) {
        String token = authorization.split(" ")[1];
        String decode = new String(Base64.getDecoder().decode(token));
        return decode.split(":");
    }

    private class WrappedRequest extends HttpServletRequestWrapper {
        private String login;
        private Set<String> roles;

        public WrappedRequest(HttpServletRequest request, String login, Set<String> roles) {
            super(request);
            this.login = login;
            this.roles = roles;
        }

        @Override
        public Principal getUserPrincipal() {
//            return () -> login;
            return new User(login, roles);
        }
    }


}
