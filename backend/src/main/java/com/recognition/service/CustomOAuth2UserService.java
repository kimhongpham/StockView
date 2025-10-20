package com.recognition.service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;

import com.recognition.entity.Users;
import com.recognition.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String providerId = oauth2User.getAttribute("sub"); // Google user ID
        String email = oauth2User.getAttribute("email");
        String fullName = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<Users> userOpt = userRepository.findByEmail(email);
        Users user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
            user.setFirstName(fullName);
            user.setAvatarUrl(picture);
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setLastLogin(OffsetDateTime.now());
        } else {
            user = new Users();
            user.setEmail(email);
            user.setUsername(generateUsernameFromEmail(email));
            user.setFirstName(fullName);
            user.setAvatarUrl(picture);
            user.setRole("USER");
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setIsActive(true);
            user.setIsVerified(true);
            user.setCreatedAt(OffsetDateTime.now());
        }

        userRepository.save(user);

        var authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
        );

        return new DefaultOAuth2User(
                authorities,
                oauth2User.getAttributes(),
                "email"
        );
    }

    private String generateUsernameFromEmail(String email) {
        String base = email.substring(0, email.indexOf("@"))
                .replaceAll("[^a-zA-Z0-9_]", "")
                .toLowerCase();
        if (base.length() < 3) {
            base = "googleuser_" + base;
        }

        int suffix = 1;
        String newUsername = base;
        while (userRepository.existsByUsername(newUsername)) {
            newUsername = base + suffix++;
        }
        return newUsername;
    }
}
