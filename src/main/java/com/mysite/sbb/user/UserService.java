package com.mysite.sbb.user;


import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); //비밀번호는 보안을 위해 반드시 암호화하여 저장
        user.setPassword(passwordEncoder.encode(password));
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

}