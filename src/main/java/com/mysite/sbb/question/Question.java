package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
public class Question {
    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200) //VARCHAR(200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    //실제 DB 테이블에 칼럼이 생성되지 않는다. -> DB는 배열이나 리스트를 칼럼에 저장할 수 없기 때문
    //CascadeType.REMOVE : 질문을 삭제하면 그에 달린 답변들도 모두 함께 삭제하기 위해서
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.EXTRA) // answerList.size(); 함수가 실행될 때 SELECT COUNT 실행
    //OneToMany는 직접 객체 초기화
    private List<Answer> answerList = new ArrayList<>();

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    // 중복 허용하도록 변경하려면 - private List<SiteUser> voter = new ArrayList<>();
    // 중복 처리 : 하나의 질문에 한 사람이 여러 개 작성하는 것을 방지
    // LinkedHashSet : 순서를 가지고 있다.
    private Set<SiteUser> voters = new LinkedHashSet<>();

    public void addAnswer(Answer a) {
        a.setQuestion(this);
        answerList.add(a);
    }

    public void addVoter(SiteUser voter) {
        voters.add(voter);
    }


}

