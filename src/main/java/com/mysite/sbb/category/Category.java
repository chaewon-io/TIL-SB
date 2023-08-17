//package com.mysite.sbb.category;
//
//import com.mysite.sbb.question.Question;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Setter
//@Entity
//public class Category {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true)
//    private String name;
//
//    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
//    private List<Question> questionList = new ArrayList<>();
//
//}