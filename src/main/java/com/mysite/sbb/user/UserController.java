package com.mysite.sbb.user;

import com.mysite.sbb.Message.Message;
import com.mysite.sbb.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final QuestionService questionService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("profile")
    public String profile(Principal principal, Model model) {
        String username = principal.getName();
        SiteUser user = userService.findByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("introduction", user.getIntroduction());
        model.addAttribute("questionList",
                questionService.getCurrentListByUser(username, 5));

        return "profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("settings")
    public String settings(Principal principal, Model model) {
        String username = principal.getName();
        SiteUser user = userService.findByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("introduction", user.getIntroduction());
        model.addAttribute("passwordResetForm", new PasswordResetForm());

        return "settings";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("settings")
    public String saveSettings(Principal principal, @RequestParam String introduction) {
        String username = principal.getName();
        SiteUser user = userService.findByUsername(username);
        user.setIntroduction(introduction);
        userService.save(user);

        return "redirect:/user/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/password/reset")
    public String resetPassword(PasswordResetForm passwordResetForm) {
        return "password_reset_form"; // 비밀번호 변경 페이지를 렌더링
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/password/reset")
    public String resetPassword(@Valid @ModelAttribute("passwordResetForm")
                                 PasswordResetForm passwordResetForm, BindingResult bindingResult, Principal principal, Model model) {
        SiteUser user = this.userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            return "password_reset_form"; // 비밀번호 변경 페이지를 다시 렌더링하고 오류 표시
        }

        if (!this.userService.isSamePassword(user, passwordResetForm.getCurrentPassword())) {
            bindingResult.rejectValue("currentPassword", "notCurrentPassword", "현재 비밀번호와 일치하지 않습니다.");
            return "password_reset_form";
        }

        if (passwordResetForm.getNewPassword().equals(passwordResetForm.getCurrentPassword())) {
            bindingResult.rejectValue("newPassword", "sameAsCurrentPassword", "현재 비밀번호와 동일한 비밀번호입니다.");
            return "password_reset_form";
        }

        if (!passwordResetForm.getNewPassword().equals(passwordResetForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "password_reset_form";
        }

        try {
            userService.resetPassword(user, passwordResetForm.getNewPassword());
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("modifyPasswordFailed", e.getMessage());
            return "password_reset_form";
        }

        model.addAttribute("data", new Message("비밀번호 변경 되었습니다.", "/"));
        return "password_reset_form"; // 변경 성공
    }

}