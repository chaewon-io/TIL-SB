package com.mysite.sbb.user;


import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private final JavaMailSender mailSender;

    @Transactional(readOnly = true)
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


    public MailForm createMailForm(String email) {
        String password = createRandomPassword();
        updateTempPassword(password, email);

        MailForm mailForm = new MailForm();
        mailForm.setAddress(email);
        mailForm.setTitle("임시비밀번호 안내 메일입니다.");
        mailForm.setMessage("회원님의 임시 비밀번호는 " + password + "입니다. 감사합니다");

        return mailForm;
    }

    public String createRandomPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append((int) (Math.random() * 10));
        }

        return sb.toString();
    }

    public void updateTempPassword(String password, String email) {
        SiteUser user = userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("User not found with email: " + email));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user); // 실제 비밀번호를 저장
    }


    public void sendEmail(MailForm mailForm) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailForm.getAddress());
        message.setSubject(mailForm.getTitle());
        message.setText(mailForm.getMessage());
        message.setFrom("0101angela@naver.com");
        message.setReplyTo("0101angela@naver.com");
        mailSender.send(message);
    }
}