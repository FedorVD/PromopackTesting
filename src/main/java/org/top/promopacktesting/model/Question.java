package org.top.promopacktesting.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "questions")
@NoArgsConstructor
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(length = 500)
    private String imagePath;

    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Column(name = "order_num")
    private Integer orderNum;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;


    public Question(Test test, String text, Integer orderNum) {
        this.test = test;
        this.text = text;
        this.orderNum = orderNum;
    }
}
