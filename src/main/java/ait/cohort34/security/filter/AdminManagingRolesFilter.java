package ait.cohort34.security.filter;


import ait.cohort34.accounting.model.Role;
import ait.cohort34.security.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
//@RequiredArgsConstructor
@Order(20)
public class AdminManagingRolesFilter implements Filter {

//    final UserAccountRepository userAccountRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (checkEndpoint(request.getMethod(), request.getServletPath())) {

//            Principal principal = request.getUserPrincipal();
//            UserAccount userAccount = userAccountRepository.findById(principal.getName()).get();

            User principal = (User) request.getUserPrincipal();
//            String login = request.getUserPrincipal().getName();

//            if (!userAccount.getRoles().contains(Role.ADMINISTRATOR)) {
            if (!principal.getRoles().contains(Role.ADMINISTRATOR.name())) {
                response.sendError(403, String.format("You are not allowed to access this resource."));
            }

        }
        chain.doFilter(request, response);
    }

    private boolean checkEndpoint(String method, String path) {
        String[] parts = path.split("/");

//        System.out.println(parts.length);
//        Arrays.stream(parts).forEach(System.out::println);
//
//        return false;

//        return path.contains("role");

//        return parts.length == 6 && "role".equalsIgnoreCase(parts[4]);

        return path.matches("/account/user/\\w+/role/\\w+");
    }
}





