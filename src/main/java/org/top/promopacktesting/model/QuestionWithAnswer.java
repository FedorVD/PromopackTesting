package org.top.promopacktesting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class QuestionWithAnswer {

    private Question question;
    private UserAnswer userAnswer;
    private List<Answer> correctAnswers;
    private boolean isCorrect;


}
