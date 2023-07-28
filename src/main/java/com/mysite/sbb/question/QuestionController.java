package com.mysite.sbb.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Question> questionList = questionService.getList();

        model.addAttribute("questionList", questionList);

        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Question question = questionService.getQuestion(id);

        model.addAttribute("question", question);

        return "question_detail";
    }

    @GetMapping("/create")
    public String questionCreate() {
        return "question_form";
    }

    @PostMapping("/create")
    public String questionCreate(String subject, String content) {

        if ( subject == null || subject.trim().length() == 0 ) {
            throw new RuntimeException("subject(을)를 입력해주세요.");
        }

        if ( subject.trim().length() > 200 ) {
            throw new RuntimeException("subject(을)를 200자 이하로 입력해주세요.");
        }

        if ( content == null || content.trim().length() == 0 ) {
            throw new RuntimeException("content(을)를 입력해주세요.");
        }

        if ( content.trim().length() > 20_000 ) {
            throw new RuntimeException("content(을)를 20,000자 이하로 입력해주세요.");
        }

        questionService.create(subject, content);

        return "redirect:/question/list"; // 질문 저장후 질문목록으로 이동
    }

}