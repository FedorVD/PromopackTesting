package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.Answer;
import org.top.promopacktesting.model.Question;
import org.top.promopacktesting.model.Test;
import org.top.promopacktesting.repository.AnswerRepository;
import org.top.promopacktesting.repository.QuestionRepository;
import org.top.promopacktesting.repository.TestRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionService {


    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerService answerService;

    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    public List<Question> getQuestionsByTestId(Long testId) {
        return questionRepository.findByTestId(testId);
    }

    public void saveQuestionWithAnswers(Question question) {
        questionRepository.save(question);
        answerService.addAnswersToQuestion(question, question.getAnswers());
    }

    public List<Answer> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    public Optional<Question> getQuestionByTestIdAnOrderNum(Long testId, Long orderNum) {
        return questionRepository.findByTestIdAndOrderNum(testId, orderNum);
    }

    public void updateQuestion(Long id, Question updatedQuestion) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            question.setText(updatedQuestion.getText());
            question.setAnswers(updatedQuestion.getAnswers());
            questionRepository.save(question);
        }
    }

    public boolean isLastQuestion(Long questionId) {
        if (questionId == null) {
            return false;
        } else {
            Optional<Question> question = questionRepository.findById(questionId);
            if (question.isPresent()) {
                List<Question> questions = questionRepository.findByTestId(question.get().getTest().getId());
                if (question.get().getOrderNum() == questions.size()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public Integer getMaxOrderNum(Test test) {
        if (test != null) {
            List<Question> questions = questionRepository.findByTestId(test.getId());
            if (questions.size() > 0) {
                return questions.get(questions.size() - 1).getOrderNum();
            } else {
                return 0;
            }
        } else {
            throw new RuntimeException("Test is null");
        }
    }
}
