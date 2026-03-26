package com.vti.bevtilib.config;

import com.vti.bevtilib.model.UserDetail;
import com.vti.bevtilib.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final String frontendUrl;

    public CustomLoginSuccessHandler(UserService userService,
                                     @Value("${app.frontend-url}") String frontendUrl) {
        this.userService = userService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String username = authentication.getName();
        HttpSession session = request.getSession();

        userService.findByUsername(username).ifPresent(user -> {
            if ("CUSTOMER".equals(user.getRole())) {
                UserDetail detail = user.getUserDetail();
                if (detail == null || !StringUtils.hasText(detail.getEmail())) {
                    session.setAttribute("showFirstLoginPopup", true);
                } else {
                    session.removeAttribute("showFirstLoginPopup");
                }
            }
        });

        String redirectURL = frontendUrl + "/";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            switch (role) {
                case "ROLE_ADMIN":
                    redirectURL = frontendUrl + "/admin/dashboard";
                    break;
                case "ROLE_STAFF":
                    redirectURL = frontendUrl + "/staff/home";
                    break;
            }
            break;
        }
        response.sendRedirect(redirectURL);
    }
}
