package org.top.promopacktesting.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.*;
import org.top.promopacktesting.service.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class TestController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private TestingService testingService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @GetMapping("/userAssignedTests")
    public String showMyTests(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Optional<User> userOpt = userService.getUserByUsername(currentUsername);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Пользователь не найден");
            return "/user/userAssignedTests";
        }

        User user = userOpt.get();
        List<AssignedTest> assignments = assignmentService.getNotCompletedAssignedTestsByUserId(user.getId());

        model.addAttribute("assignments", assignments);
        if (assignments.isEmpty()) {
            model.addAttribute("error", "Вам тесты не назначались");
        }
        return "/user/userAssignedTests";
    }

    @GetMapping("/{id}/userPathTest")
    public String startTest(@PathVariable Long id, Model model) {
        AssignedTest assignedTest = assignmentService.getAssignedTestById(id).orElseThrow(() -> new RuntimeException("Тест не найден"));

        if (assignedTest.getStatus() == AssignedTest.TestStatus.COMPLETED) {
            model.addAttribute("error", "Тест уже пройден");
            return "/user/userAssignedTests";
        }

        List<Question> questions = questionService.getQuestionsByTestId(assignedTest.getTest().getId());
        if (questions.isEmpty()) {
            model.addAttribute("error", "Тест не содержит вопросов");
            return "/user/userAssignedTests";
        }


        int currentIndex = 0;

        // Если тест в состоянии IN_PROGRESS — найти первый неотвеченный вопрос
        if (assignedTest.getStatus() == AssignedTest.TestStatus.IN_PROGRESS) {
            List<UserAnswer> userAnswers = testingService.getUserAnswersByAssignedTestId(id);
            Set<Long> answeredQuestionIds = userAnswers.stream()
                    .map(UserAnswer::getQuestion)
                    .map(Question::getId)
                    .collect(Collectors.toSet());

            for (int i = 0; i < questions.size(); i++) {
                if (!answeredQuestionIds.contains(questions.get(i).getId())) {
                    currentIndex = i;
                    break;
                }
            }
        }
        if (assignedTest.getStatus() == AssignedTest.TestStatus.ASSIGNED) {
            assignedTest.setStatus(AssignedTest.TestStatus.IN_PROGRESS);
            assignmentService.updateAssignedTest(assignedTest);
        }
        model.addAttribute("assignedTest", assignedTest);
        model.addAttribute("questions", questions);
        model.addAttribute("currentIndex", currentIndex); // индекс текущего вопроса
        model.addAttribute("answers", new HashMap<>()); // хранение ответов пользователя

        return "/user/userPathTest";
    }

    @PostMapping("/{id}/answer")
    public String submitAnswer(
            @PathVariable Long id,
            @RequestParam int currentIndex,
            @RequestParam Map<String, String> requestParam,
            Model model) {
        try {
            AssignedTest assignedTest = assignmentService.getAssignedTestById(id)
                    .orElseThrow(() -> new RuntimeException("Тест не найден"));

            List<Question> questions = questionService.getQuestionsByTestId(assignedTest.getTest().getId());
            if (questions.isEmpty()) {
                model.addAttribute("error", "Тест не содержит вопросов");
                return "/user/userPathTest";
            }

            Question currentQuestion = questions.get(currentIndex);
            List<Answer> answers = questionService.getAnswersByQuestionId(currentQuestion.getId());
            if (answers.isEmpty()) {
                model.addAttribute("error", "Вопрос не содержит ответов");
                return "/user/userPathTest";
            }

            List<Long> selectedAnswerIds = new ArrayList<>();
            for (int i = 0; i < answers.size(); i++) {
                if (requestParam.containsKey("answers[" + i + "]")) {
                    selectedAnswerIds.add(answers.get(i).getId());
                }
            }

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setAssignedTest(assignedTest);
            userAnswer.setQuestion(currentQuestion);

            List<UserSelectedAnswer> userSelectedAnswers = selectedAnswerIds.stream()
                    .map(answerId -> {
                        Answer answer = answerService.getAnswerById(answerId)
                                .orElseThrow(() -> new RuntimeException("Ответ не найден"));
                        boolean isCorrect = answer.getIsCorrect();
                        UserSelectedAnswer usa = new UserSelectedAnswer(answer, isCorrect);
                        usa.setUserAnswer(userAnswer);
                        return usa;
                    })
                    .collect(Collectors.toList());

            userAnswer.setSelectedAnswers(userSelectedAnswers);

            try {
                testingService.saveUserAnswer(userAnswer);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "Ошибка при сохранении ответа: " + e.getMessage());
                return "/user/userPathTest";
            }

            Map<String, List<Boolean>> userAnswers = (Map<String, List<Boolean>>) model.getAttribute("answers");
            if (userAnswers == null) {
                userAnswers = new HashMap<>();
            }

            List<Boolean> selectedOptions = new ArrayList<>();
            for (Answer a : answers) {
                selectedOptions.add(selectedAnswerIds.contains(a.getId()));
            }
            userAnswers.put(currentQuestion.getText(), selectedOptions);
            model.addAttribute("answers", userAnswers);

            // Переход к следующему вопросу или завершение теста
            model.addAttribute("assignedTest", assignedTest);
            model.addAttribute("questions", questions);
            model.addAttribute("answers", userAnswers);

            if (currentIndex + 1 < questions.size()) {
                model.addAttribute("currentIndex", currentIndex + 1);
            } else {
                model.addAttribute("completed", true);
                model.addAttribute("assignedTest", assignedTest);
                model.addAttribute("userAnswers", userAnswers);
                return "redirect:/user/" + id + "/result";
            }
            return "/user/userPathTest";
        }catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при обработке ответа " + e.getMessage());
            return "/user/userPathTest";
        }
    }

    @GetMapping("/{id}/result")
    public String showResult(@PathVariable Long id, Model model) {
        try{
            System.out.println("===> Запуск метода showResult для ID: " + id);

            AssignedTest assignedTest = assignmentService.getAssignedTestById(id)
                    .orElseThrow(() -> new RuntimeException("Тест не найден"));

            System.out.println("AssignedTest: " + assignedTest);
            System.out.println("Test ID: " + (assignedTest.getTest() != null ? assignedTest.getTest().getId() : "null"));

            List<UserAnswer> userAnswers = testingService.getUserAnswersByAssignedTestId(id);
            System.out.println("UserAnswers size: " + userAnswers.size());

            Double score = assignmentService.calculateScore(assignedTest.getId());
            Double formattedScore = Math.round(score * 10.0) / 10.0;
            System.out.println("Рассчитанный балл: " + score);

            assignedTest.setStatus(AssignedTest.TestStatus.COMPLETED);
            assignmentService.updateAssignedTest(assignedTest);

            model.addAttribute("assignedTest", assignedTest);
            model.addAttribute("userAnswers", userAnswers);
            model.addAttribute("score", formattedScore);
            return "/user/testResult";
        }catch (Exception e){
            e.printStackTrace(); // Это покажет точное место ошибки
            model.addAttribute("error", "Ошибка при получении результатов" + e.getMessage());
            return "/user/userAssignedTests";
        }
    }
}
