package com.mysite.sbb.user;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final QuestionService questionService;

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


}