package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.top.promopacktesting.model.AssignedTest;
import org.top.promopacktesting.model.UserAnswer;
import org.top.promopacktesting.repository.AssignedTestRepository;
import org.top.promopacktesting.repository.QuestionRepository;
import org.top.promopacktesting.repository.UserAnswerRepository;

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
