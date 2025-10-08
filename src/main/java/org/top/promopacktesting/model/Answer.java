package org.top.promopacktesting.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "answer")
@NoArgsConstructor
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answerNum", nullable = false)
    private Integer answerNum;

    @Column(name = "answerText", nullable = false)
    private String answerText;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    public Answer(Question question,Integer answerNum, String answerText, boolean isCorrect) {
        this.question = question;
        this.answerNum = answerNum;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }
}
