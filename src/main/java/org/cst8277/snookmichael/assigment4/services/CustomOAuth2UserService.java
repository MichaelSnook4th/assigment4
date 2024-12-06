package org.cst8277.snookmichael.assigment4.services;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract user details
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");

        // Create JWT
        String jwt = JwtUtil.createJWT(name, email);

        // Optionally add JWT to user attributes
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("jwt", jwt);

        // Create OAuth2AuthenticationToken
        OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(
                oAuth2User, oAuth2User.getAuthorities(), userRequest.getClientRegistration().getRegistrationId()
        );

        // Call the method with the correct number of arguments
        authService.processOAuth2Token(authenticationToken, jwt);

        return oAuth2User;
    }
}