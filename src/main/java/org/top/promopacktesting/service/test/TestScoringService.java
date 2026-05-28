package org.top.promopacktesting.service.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.test.Answer;
import org.top.promopacktesting.model.test.Question;
import org.top.promopacktesting.model.test.UserAnswer;
import org.top.promopacktesting.model.test.UserSelectedAnswer;
import org.top.promopacktesting.repository.test.AssignedTestRepository;
import org.top.promopacktesting.repository.test.QuestionRepository;
import org.top.promopacktesting.repository.test.UserAnswerRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TestScoringService {

    @Autowired
    private AssignedTestRepository assignedTestRepository;
    @Autowired
    private UserAnswerRepository userAnswerRepository;
    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private QuestionRepository questionRepository;


/*    public Double calculateScore(Long assignmentId) {

        List<UserAnswer> userAnswers = userAnswerRepository.findByAssignedTestId(assignmentId);
        AssignedTest assignedTest = assignedTestRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Назначенный тест не найден"));
        double totalPoints = 0;
        double maxPossiblePoints = (double)assignedTest.getTest().getQuestions().size();
        for (UserAnswer userAnswer : userAnswers) {
            try {
                double questionPoints = calculateQuestionPoints(userAnswer);
                totalPoints += questionPoints;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        double score = (totalPoints / maxPossiblePoints) * 10;
        assignedTest.setTestScore(score);
        assignedTest.setCompletedAt(LocalDateTime.now());
        if (assignedTest.getStatus() != AssignedTest.TestStatus.COMPLETED) {
            assignedTest.setStatus(AssignedTest.TestStatus.COMPLETED);
            assignmentService.updateAssignedTest(assignedTest);
        }
        assignedTestRepository.save(assignedTest);
        return score;
    }*/

    public double calculateQuestionPoints(UserAnswer userAnswer) {
        Question question = userAnswer.getQuestion();
        List<Answer> correctAnswers = question.getAnswers().stream()
                .filter(Answer::getIsCorrect)
                .toList();

        Set<Long> correctAnswersIds = correctAnswers.stream()
                .map(Answer::getId)
                .collect(Collectors.toSet());

        List<UserSelectedAnswer> selectedAnswers = userAnswer.getSelectedAnswers();
        Set<Long> selectedAnswersIds = selectedAnswers.stream()
                .map(UserSelectedAnswer::getAnswer)
                .map(Answer::getId)
                .collect(Collectors.toSet());

        if (selectedAnswersIds.isEmpty()) {
            return 0.0;
        }

        boolean hasWrongSelection = selectedAnswersIds.stream()
                .anyMatch(id -> !correctAnswersIds.contains(id));

        boolean missedCorrectAnswers = correctAnswersIds.stream()
                .anyMatch(id -> !selectedAnswersIds.contains(id));

        if (hasWrongSelection || missedCorrectAnswers) {
            return 0.0;
        }

        return 1.0;
    }
}
