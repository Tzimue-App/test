package com.template.springboot.security;

import com.template.springboot.model.User;
import com.template.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email      = (String) attributes.get("email");
        String name       = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub");

        User user = userRepository.findByEmail(email)
                .map(existing -> mergeGoogleAccount(existing, providerId))
                .orElseGet(() -> createGoogleUser(email, name, providerId));

        return new DefaultOAuth2User(
                Set.of(new OAuth2UserAuthority(user.getRole().name(), attributes)),
                attributes,
                "email"
        );
    }

    private User mergeGoogleAccount(User existing, String providerId) {
        if (existing.getProvider() == User.AuthProvider.LOCAL) {
            existing.setProvider(User.AuthProvider.GOOGLE);
            existing.setProviderId(providerId);
            userRepository.save(existing);
        }
        return existing;
    }

    private User createGoogleUser(String email, String name, String providerId) {
        return userRepository.save(User.builder()
                .email(email)
                .name(name)
                .provider(User.AuthProvider.GOOGLE)
                .providerId(providerId)
                .role(User.Role.ROLE_USER)
                .build());
    }
}
