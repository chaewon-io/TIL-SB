package com.mysite.sbb.answer;


import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(
            Model model,
            @PathVariable Integer id,
            @Valid AnswerForm answerForm,
            BindingResult bindingResult,
            Principal principal //현재 로그인한 유저 정보 얻기
    ) {

        // 관련 질문을 얻어온다.
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());


        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        // 답변객체
        Answer answer = answerService.create(question, answerForm.getContent(), siteUser);

        return "redirect:/question/detail/%d".formatted(id);
    }
}