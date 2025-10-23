package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.*;
import org.top.promopacktesting.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentService {

    @Autowired
    private AssignedTestRepository assignedTestRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAnswerRepository userAnswerRepository;

    @Autowired
    private AnswerRepository answerRepository;

/*    @Autowired
    private TestScoringService testScoringService;*/

    public List<AssignedTest> getAssignedTests() {
        return assignedTestRepository.findAll();
    }

    public List<AssignedTest> getAssignedTestsByUserId(Long userId) {
        return assignedTestRepository.findByUserId(userId);
    }

    public List<AssignedTest> getAssignedTestsByTestId(Long testId){
        return assignedTestRepository.findByTestId(testId);
    }

    public List<AssignedTest> getAssignedTestsByStatus(AssignedTest.TestStatus status) {
        return assignedTestRepository.findByStatus(status);
    }

    public List<AssignedTest> getAssignedTestsByUserIdAndStatus(Long userId, AssignedTest.TestStatus status) {
        return assignedTestRepository.findByUserIdAndStatus(userId, status);
    }

    public List<AssignedTest> getAssignedTestsByTestIdAndStatus(Long testId, AssignedTest.TestStatus status) {
        return assignedTestRepository.findByTestIdAndStatus(testId, status);
    }

    public List<AssignedTest> getAssignedTestsByUserIdAndTestIdAndStatus(Long userId, Long testId, AssignedTest.TestStatus status) {
        return assignedTestRepository.findByUserIdAndTestIdAndStatus(userId, testId, status);
    }

    public List<AssignedTest> getAssignedTestsByTestNameAndUserNameAndStatus(String testName, String userName, AssignedTest.TestStatus status) {
        return assignedTestRepository.findByTestNameAndUserNameAndStatus(testName, userName, status);
    }

    public Optional<AssignedTest> getAssignedTestById(Long assignmentId) {
        return assignedTestRepository.findById(assignmentId);
    }
    public List<AssignedTest> getAssignedTestsByUserIdAndTestId(Long userId, Long testId) {
        return assignedTestRepository.findByUserIdAndTestId(userId, testId);
    }

    public List<AssignedTest> getNotCompletedAssignedTestsByUserId(Long userId) {
        return assignedTestRepository.findNotCompletedByUserId(userId);
    }

    public void assignTestToUser(Long testId, Long userId, User assignedBy){
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        List<AssignedTest> existAssignment = assignedTestRepository.findByUserIdAndTestId(userId, testId);

        if (!existAssignment.isEmpty()) {
            for (AssignedTest assignedTest : existAssignment) {
                if (assignedTest.getStatus() != AssignedTest.TestStatus.COMPLETED) {
                    throw new RuntimeException("Этот тест пользователю уже назначен и не завершен");
                }
            }
        } else {
            System.out.println("Существующее назначение не найдено.");
        }
        AssignedTest assignedTest = new AssignedTest(test, user, assignedBy);
        assignedTest.setStatus(AssignedTest.TestStatus.ASSIGNED);
        assignedTestRepository.save(assignedTest);
    }

    public void startTest(Long assignmentId){
        AssignedTest assignment = assignedTestRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Назначение не найдено"));
        assignment.setStatus(AssignedTest.TestStatus.IN_PROGRESS);
        assignedTestRepository.save(assignment);
    }

    public Optional<AssignedTest> getAssignmentById(Long id) {
        return assignedTestRepository.findById(id);
    }

    public void updateAssignedTest(AssignedTest assignedTest) {
        assignedTestRepository.save(assignedTest);
    }

    public List<AssignedTest> getCompletedTestsByUserId(User user) {
        return assignedTestRepository.findByUserIdAndStatus(user.getId(), AssignedTest.TestStatus.COMPLETED);
    }

    public List<AssignedTest> getCompletedByTestScoreLessThanEqual(Double score) {
        return assignedTestRepository.findCompletedByScoreLessThan(score);
    }

    public List<AssignedTest> getAssignedTestsByTestName(String testName) {
        return assignedTestRepository.findByTestName(testName);
    }

    public List<AssignedTest> getAssignedTestsByUserName(String userName) {
        return assignedTestRepository.findByUserName(userName);
    }

    public List<AssignedTest> getAssignedTestsByTestNameAndStatus(String testName, AssignedTest.TestStatus status) {
        return assignedTestRepository.findByTestNameAndStatus(testName, status);
    }

    public List<AssignedTest> getAssignedTestsByUserNameAndStatus(String userName, AssignedTest.TestStatus status) {
        return assignedTestRepository.findByUserNameAndStatus(userName, status);
    }

    public AssignedTest getAssignedTestByTestNameAndUserName(String testName, String userName) {
        return assignedTestRepository.findByTestNameAndUserName(testName, userName);
    }

    public void assignTestToUsers(Long testId, List<Long> userIds, User assigner) {
        for (Long userId : userIds) {
            assignTestToUser(testId, userId, assigner);
        }
    }

    public List<QuestionWithAnswer> getQuestionsWithUserAndCorrectAnswers(Long assignmentId) {
        AssignedTest assignedTest = assignedTestRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Тест не найден"));

        Test test = assignedTest.getTest();
        List<Question> testQuestions = test.getQuestions();
        List<QuestionWithAnswer> result = new ArrayList<>();
        for (Question question : testQuestions) {
            QuestionWithAnswer questionWithAnswer = new QuestionWithAnswer();
            questionWithAnswer.setQuestion(question);
            UserAnswer userAnswer = userAnswerRepository.findByAssignedTestAndQuestion(assignedTest, question).orElseThrow(() -> new RuntimeException("Ответ не найден"));
            questionWithAnswer.setUserAnswer(userAnswer);
            questionWithAnswer.setCorrectAnswers(answerRepository.findCorrectByQuestionId(question.getId()));
            double questionPint = calculateQuestionPoints(userAnswer);
            if (questionPint != 0) {
                questionWithAnswer.setCorrect(true);
            }else {
                questionWithAnswer.setCorrect(false);
            }
            result.add(questionWithAnswer);
        }
        return result;
    }

    public Double calculateScore(Long assignmentId) {

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
            updateAssignedTest(assignedTest);
        }
        assignedTestRepository.save(assignedTest);
        return score;
    }

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
