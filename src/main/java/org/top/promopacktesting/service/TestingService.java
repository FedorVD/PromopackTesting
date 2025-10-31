package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.top.promopacktesting.model.AssignedTest;
import org.top.promopacktesting.model.Question;
import org.top.promopacktesting.model.UserAnswer;
import org.top.promopacktesting.repository.AssignedTestRepository;
import org.top.promopacktesting.repository.QuestionRepository;
import org.top.promopacktesting.repository.UserAnswerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TestingService {

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Autowired
    private AssignedTestRepository assignedTestRepository;

    @Autowired
    private QuestionRepository questionRepository;


    @Autowired
    private AssignmentService assignmentService;

/*    public UserAnswer saveUserAnswer(Long assignmentId, Long questionId, List<Boolean>selectedAnswers){
        AssignedTest assignedTest = assignedTestRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Назначение не найдено"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Вопрос не найден"));

        UserAnswer userAnswer = userAnswerRepository.findByAssignedTestAndQuestion(assignedTest, question).get();

        userAnswer.setAssignedTest(assignedTest);
        userAnswer.setQuestion(question);
        userAnswer.setAnsweredAt(LocalDateTime.now());

        return userAnswerRepository.save(userAnswer);
    }*/

/*    public Double completeTest(Long assignmentId){
        AssignedTest assignedTest = assignedTestRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Назначение не найдено"));

        Double score = assignmentService.calculateScore(assignmentId);

        assignedTest.setTestScore(score);
        assignedTest.setCompletedAt(LocalDateTime.now());

        return assignedTestRepository.save(assignedTest).getTestScore();
    }*/

    public void saveUserAnswer (UserAnswer userAnswer){
            userAnswerRepository.save(userAnswer);
    }

    @GetMapping("/test/{id}/results")
    public String showResults(@PathVariable Long id, Model model) {
        AssignedTest assignedTest = assignmentService.getAssignedTestById(id)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        Double score = assignmentService.calculateScore(id);
        List<UserAnswer> userAnswers = userAnswerRepository.findByAssignedTestId(id);

        model.addAttribute("assignedTest", assignedTest);
        model.addAttribute("score", score);
        model.addAttribute("userAnswers", userAnswers);

        return "user/testResult";
    }

    public List<UserAnswer> getUserAnswersByAssignedTestId(Long assignedTestId) {
        return userAnswerRepository.findByAssignedTestId(assignedTestId);
    }
}
