package org.top.promopacktesting.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_answers")
@Getter
@Setter
public class UserAnswer {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_test_id", nullable = false)
    private AssignedTest assignedTest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @OneToMany(mappedBy = "userAnswer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserSelectedAnswer> selectedAnswers = new ArrayList<>();

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    public UserAnswer(){
        this.answeredAt = LocalDateTime.now();
    }

    public UserAnswer(AssignedTest assignedTest, Question question, List<UserSelectedAnswer> selectedAnswers){
        this();
        this.assignedTest = assignedTest;
        this.question = question;
        this.selectedAnswers = selectedAnswers;
    }

    public void addSelectedAnswer(UserSelectedAnswer selectedAnswer){
        selectedAnswer.setUserAnswer(this);
        this.selectedAnswers.add(selectedAnswer);
    }
}

