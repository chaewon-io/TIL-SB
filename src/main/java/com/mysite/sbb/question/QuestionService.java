package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final CategoryService categoryService;

        private Specification<Question> search(String kw, String categoryName) {
            System.out.println("kw in search: " + kw);
            System.out.println("categoryName in search: " + categoryName);
            return new Specification<>() {
                private static final long serialVersionUID = 1L;
                @Override
                public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    query.distinct(true);  // 중복을 제거
                    Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                    Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                    Join<Question, Category> c = q.join("category", JoinType.LEFT);
                    Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                    return cb.and(cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                                    cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                                    cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                                    cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                                    cb.like(u2.get("username"), "%" + kw + "%")),		// 답변 작성자
                            // and
                            cb.like(c.get("name"), "%" + categoryName + "%"));		// 카테고리 이름
                }
            };
        }

        public Page<Question> getList(int page, String kw, String categoryName) {
            System.out.println("kw in getList: " + kw);
            System.out.println("categoryName in getList: " + categoryName);

            List<Sort.Order> sorts = new ArrayList<>();
            sorts.add(Sort.Order.desc("createDate"));
            Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
            Specification<Question> spec = search(kw, categoryName);
            return this.questionRepository.findAll(spec, pageable);
        }

        public Category getCategory(String categoryName) {
            return categoryService.getCategoryByName(categoryName);
        }

    public Question getQuestion(Integer id) {
        Optional<Question> oq = questionRepository.findById(id);

        if (oq.isPresent() == false) {
            throw new DataNotFoundException("question not found");
        }

        Question question = oq.get();
        question.setView(question.getView() + 1);
        questionRepository.save(question);

        return question;
    }

    public Question create(String subject, String content, SiteUser author, Category category) {
        Question q = new Question();
        q.setCreateDate(LocalDateTime.now());
        q.setSubject(subject);
        q.setContent(content);
        q.setAuthor(author);
        q.setCategory(category);
        questionRepository.save(q);

        return q;

    }
    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser voter) {
        question.addVoter(voter);
        questionRepository.save(question);
    }

    public Object getCurrentListByUser(String username, int num) {
        Pageable pageable = PageRequest.of(0, num);
        return questionRepository.findCurrentQuestion(username, pageable);
    }

}


