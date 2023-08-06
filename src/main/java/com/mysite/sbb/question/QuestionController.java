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
    public String List(Model model, @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "category", defaultValue = "질문") String category) {
        System.out.println("kw: " + kw);
        System.out.println("category: " + category);
        Page<Question> paging = this.questionService.getList(page, kw, category);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
    }


    @GetMapping("/freepost/list")
    public String freepostList(Model model, @RequestParam(value="page", defaultValue="0") int page,
                               @RequestParam(value = "kw", defaultValue = "") String kw,
                               @RequestParam(value = "category", defaultValue = "자유") String category) {
        Page<Question> paging = this.questionService.getList(page, kw, category);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "question_list";
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

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);

        return "question_detail";
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
