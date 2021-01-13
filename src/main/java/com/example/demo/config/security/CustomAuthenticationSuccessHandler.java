package com.example.demo.config.security;

import com.example.demo.entity.Login2Entity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        final Login2Entity loginEntity = (Login2Entity) authentication.getPrincipal();
        HttpSession httpSession = request.getSession();

        if (httpSession != null) {
            httpSession.setAttribute("id", loginEntity.getId());
            httpSession.setAttribute("sseKey", loginEntity.getSsekey());

            SavedRequest savedRequest = requestCache.getRequest(request, response);

            String redirectUrl = "/board";
            if (savedRequest != null) {
                String url = savedRequest.getRedirectUrl();
                if (url != null) {
                    String regex = "^(https?)://([^:/\\s]+)(:([^/]*))?((/[^\\s//]+)*)?/?([^#\\s?]*)(\\?([^#\\s]*))?(#(\\w*))?$";
                    Matcher matcher = Pattern.compile(regex).matcher(url);
                    if (matcher.find()) {
                        String group5 = matcher.group(5);
                        if (!group5.equals("")) {
                            redirectUrl = group5;
                        }
                    }
                }
            }

            if(loginEntity.getRole().equals("ROLE_ADMIN"))
                redirectUrl = "/admin";

            if (redirectUrl != null) {
                httpSession.removeAttribute("prevPage");
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            } else {
                super.onAuthenticationSuccess(request, response, authentication);
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
