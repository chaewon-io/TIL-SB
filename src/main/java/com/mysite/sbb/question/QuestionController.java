package com.mysite.sbb.question;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
//@Validated 컨트롤러에서 생략가능
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Question> paging = questionService.getList(page);
        // 스프링에서 제공하는 자료구조로 List와 유사하며 보통 page의 결과로 담는다.
        model.addAttribute("paging", paging);

        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);

        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm QuestionForm) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(
            @Valid QuestionForm questionForm,
            BindingResult bindingResult,
            Principal principal
    ) {
        if ( bindingResult.hasErrors() ) {
            return "question_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());

        questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        
        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }
}