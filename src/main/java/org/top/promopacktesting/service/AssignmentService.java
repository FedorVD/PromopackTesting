package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.AssignedTest;
import org.top.promopacktesting.model.Test;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.repository.AssignedTestRepository;
import org.top.promopacktesting.repository.TestRepository;
import org.top.promopacktesting.repository.UserAnswerRepository;
import org.top.promopacktesting.repository.UserRepository;

import java.util.List;
import java.util.Optional;

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

    public void assignTestToUser(Long testId, Long userId, User assignedBy){
        System.out.println("=== Метод assignTestToUser был вызван ===");
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<AssignedTest> existAssignment = assignedTestRepository.findByUserIdAndTestId(userId, testId);
        System.out.println("=== Проверка существующего назначения ===");
        if (existAssignment.size() > 0) {
            for (AssignedTest assignedTest : existAssignment) {
                System.out.println("Найдено существующее назначение:");
                System.out.println("ID: " + assignedTest.getId());
                System.out.println("Пользователь ID: " + assignedTest.getUser().getId());
                System.out.println("Тест ID: " + assignedTest.getTest().getId());
                System.out.println("Статус: " + assignedTest.getStatus());
                if (assignedTest != null && assignedTest.getStatus() != AssignedTest.TestStatus.COMPLETED) {
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
}
