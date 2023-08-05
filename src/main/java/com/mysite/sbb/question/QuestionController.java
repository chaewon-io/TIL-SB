package com.mysite.sbb.question;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

//import static org.yaml.snakeyaml.TypeDescription.log;


@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
//@Validated 컨트롤러에서 생략가능
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;
    private final CategoryService categoryService;


    @GetMapping("/list")
    public String list(Model model, @RequestParam(defaultValue = "0") int page, String kw) {
        Page<Question> paging = questionService.getList(page, kw, null);

        // 스프링에서 제공하는 자료구조로 List와 유사하며 보통 page의 결과로 담는다.
        model.addAttribute("paging", paging);

        return "question_list";
    }


    @GetMapping("/freepost/list")
    public String freepostList(Model model, @RequestParam(value="page", defaultValue="0") int page,
                               @RequestParam(value = "kw", defaultValue = "") String kw) {
        Page<Question> paging = this.questionService.getList(page, kw, "자유");
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
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
    public String questionCreate(Model model) {
        model.addAttribute("categoryList", categoryService.getList());
        model.addAttribute("questionForm", new QuestionForm()); // QuestionForm을 직접 모델에 추가
        return "question_form";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(
            @Valid @ModelAttribute("questionForm") QuestionForm questionForm,
            BindingResult bindingResult,
            Principal principal,
            Model model
    ) {
        if ( bindingResult.hasErrors() ) {
            model.addAttribute("categoryList", categoryService.getList());
            return "question_form";
        }
        SiteUser siteUser = userService.getUser(principal.getName());
        Category category = categoryService.getCategory(questionForm.getCategory());

        questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, category);

        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());

        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/create")
    public String questionCreate(Model model, @Valid QuestionForm questionForm,
                                 BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryList", categoryService.getList());
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Category category = this.categoryService.getCategory(questionForm.getCategory());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, category);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        questionService.delete(question);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = questionService.getQuestion(id);
        SiteUser siteUser = userService.getUser(principal.getName());

        questionService.vote(question, siteUser);

        return "redirect:/question/detail/%d".formatted(id);
    }

}
