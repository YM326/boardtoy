package com.example.demo.config.security;

import com.example.demo.entity.Login2Entity;
import com.example.demo.repository.Login2Repository;
import com.example.demo.util.ARIA256Util;
import com.example.demo.util.SHA256Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    Login2Repository loginRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        Object credentials = authentication.getCredentials();
        if(!(credentials instanceof String))
            return null;

        String pw = credentials.toString();
        Login2Entity loginEntity = loginRepository.findById(username);
        if(loginEntity == null)
            return null;

        String decSalt = ARIA256Util.decrypted(loginEntity.getSalt(), ARIA256Util.LOGIN_KEY);
        String encryptPw = SHA256Util.getEncrypt(pw, decSalt);

        //에러 띄우기
        if(!encryptPw.equals(loginEntity.getPw()))
            return null;

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(
                loginEntity,
                null,
                loginEntity.getAuth());

        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
