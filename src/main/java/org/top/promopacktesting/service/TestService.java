package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.Question;
import org.top.promopacktesting.model.Test;
import org.top.promopacktesting.repository.AssignedTestRepository;
import org.top.promopacktesting.repository.QuestionRepository;
import org.top.promopacktesting.repository.TestRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AssignedTestRepository assignedTestRepository;

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public List<Test> getAllActiveTests() {
        return testRepository.findByIsActiveTrue();
    }

    public Optional<Test> getTestById(Long id) {
        return testRepository.findById(id);
    }

    public void createTest(Test test) {
        testRepository.save(test);
    }

    public void updateTest(Long id, Test testDetails) {
        testRepository.findById(id).map(test -> {
            test.setName(testDetails.getName());
            test.setIsActive(testDetails.getIsActive());
            return testRepository.save(test);
        }).orElseThrow(() -> new RuntimeException("Тест не найден"));
    }

    public List<Test> getTestsByThemeId(Long themeTestId) {
        return testRepository.findByThemeTestId(themeTestId);
    }

/*    @Transactional
    public Question addQuestionToTest(Long testId, Question question) {
        return testRepository.findById(testId).map(test ->{
            question.setTest(test);
            return questionRepository.save(question);
        }).orElseThrow(() -> new RuntimeException("Тест не найден"));
    }*/
}
