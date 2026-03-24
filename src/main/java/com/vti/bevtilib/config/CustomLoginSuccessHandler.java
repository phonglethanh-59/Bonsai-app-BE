package com.vti.bevtilib.config;

import com.vti.bevtilib.model.UserDetail;
import com.vti.bevtilib.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

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

        String redirectURL = "http://localhost:3000/";

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            switch (role) {
                case "ROLE_ADMIN":
                    redirectURL = "http://localhost:3000/admin/dashboard";
                    break;
                case "ROLE_STAFF":
                    redirectURL = "http://localhost:3000/staff/home";
                    break;
            }
            break;
        }
        response.sendRedirect(redirectURL);
    }
}
