package org.top.promopacktesting.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_selected_answer")
@Getter
@Setter
@NoArgsConstructor
public class UserSelectedAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_answer_id", nullable = false)
    private UserAnswer userAnswer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    public UserSelectedAnswer(UserAnswer userAnswer, Answer answer, Boolean isCorrect) {
        this.userAnswer = userAnswer;
        this.answer = answer;
        this.isCorrect = isCorrect;
    }

    public UserSelectedAnswer(Answer answer, Boolean isCorrect) {
        this.answer = answer;
        this.isCorrect = isCorrect;
    }
}
