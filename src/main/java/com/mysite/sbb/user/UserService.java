package com.mysite.sbb.user;


import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = userRepository.findByUsername(username);

        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            return null;
        }
    }

    @Transactional
    public SiteUser create(String username, String email, String password) {
        return join("SBB", username, email, password);
    }

    private SiteUser join(String providerTypeCode, String username, String email, String password) {
        if (getUser(username) !=null) {
            throw new RuntimeException("해당 ID는 이미 사용중입니다.");
        }

        if (StringUtils.hasText(password)) password = passwordEncoder.encode(password);


        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setProviderTypeCode(providerTypeCode);
        user.setPassword(password);
        userRepository.save(user);
        return user;
    }


    public SiteUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("User not found with username: " + username));
    }

    public void save(SiteUser user) {
        userRepository.save(user);
    }

    public void resetPassword(SiteUser modifyUser, String password) {
        modifyUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(modifyUser);
    }

    public boolean isSamePassword(SiteUser user, String password){
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Transactional
    public SiteUser whenSocialLogin(String providerTypeCode, String username) {
        SiteUser siteUser = getUser(username);

        if (siteUser != null) return siteUser;

        return join(providerTypeCode, username, "", "");
    }


}