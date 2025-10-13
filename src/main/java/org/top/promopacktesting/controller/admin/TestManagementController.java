package org.top.promopacktesting.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.*;
import org.top.promopacktesting.service.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/tests")
@PreAuthorize("hasRole('ADMIN')")
public class TestManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private ThemeTestService themeTestService;

    @GetMapping("/tests")
    public String showTests(@RequestParam(required = false) String search,
                            @RequestParam(required = false) Long themeId,
                            Model model) {
        List<Test> tests = new ArrayList<>();
        if (themeId != null) {
            tests = testService.getTestsByThemeId(themeId);
        } else {
            tests = testService.getAllTests();
        }
        model.addAttribute("tests", tests);
        model.addAttribute("search", search);
        model.addAttribute("selectedThemeId", themeId);
        model.addAttribute("themes", themeTestService.getAllThemeTests());
        return "/admin/tests/tests";
    }

    @GetMapping("/addTest")
    public String showAddTestForm(Model model) {
        List<ThemeTest> themeTests = themeTestService.getAllThemeTests();
        if (themeTests.isEmpty()) {
            model.addAttribute("error", "Нет доступных тем для создания теста");
            return "/admin/tests/tests";
        }
        model.addAttribute("test", new Test());
        model.addAttribute("themeTests", themeTests);
        return "/admin/tests/addTest";
    }

    @PostMapping("/addTest")
    public String addTest(@RequestParam Long themeId,
                          @RequestParam String testName,
                          @RequestParam Double passingScore,
                          Model model) {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();

            Optional<User> userOpt = userService.getUserByUsername(currentUsername);

            if (userOpt.isEmpty()) {
                model.addAttribute("error", "Пользователь не найден");
                return "/admin/tests/addTest";
            }
            User creator = userOpt.get();
            Test newTest = new Test(testName, creator, passingScore);
            newTest.setThemeTest(themeTestService.getThemeTestById(themeId).orElseThrow(() -> new RuntimeException("Тема не найдена")));
            testService.createTest(newTest);
            Long testId = newTest.getId();
            return "redirect:/admin/tests/" + testId + "/addQuestion";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при добавлении теста: " + e.getMessage());
            return "admin/tests/addTest";
        }
    }

    @GetMapping("/{testId}/edit")
    public String showEditTestForm(@PathVariable Long testId, Model model) {
        Optional<Test> testOpt = testService.getTestById(testId);
        if (testOpt.isEmpty()) {
            model.addAttribute("error", "Тест не найден!");
            return "admin/tests/editTest";
        } else {
            Optional<ThemeTest> themeTestOpt = themeTestService.getThemeTestById(testOpt.get().getThemeTest().getId());
            model.addAttribute("test", testOpt.orElseThrow(() -> new RuntimeException("Тест не найден")));
            model.addAttribute("themeTests", themeTestService.getAllThemeTests());
            model.addAttribute("themeTest", themeTestOpt.orElseThrow(() -> new RuntimeException("Тема не найдена")));
            return "admin/tests/editTest";
        }
    }

    @PostMapping("/{testId}/edit")
    public String editTest(@PathVariable Long testId,
                           @RequestParam String testName,
                           @RequestParam Boolean isActive,
                           Model model) {
        try{
            Optional<Test> testOpt = testService.getTestById(testId);
            if (testOpt.isEmpty()){
                model.addAttribute("error", "Тест не найден!");
                return "admin/tests/editTest";
            } else {
                Test test = testOpt.get();
                test.setName(testName);
                test.setIsActive(isActive);
                testService.updateTest(testId, test);
                model.addAttribute("message", "Тест успешно обновлен!");
            }
        }catch (Exception e){
            model.addAttribute("error", "Ошибка при обновлении теста: " + e.getMessage());
        }
        model.addAttribute("test", testService.getTestById(testId).orElseThrow(() -> new RuntimeException("Тест не найден")));
        return "admin/tests/viewTest";
    }

    @GetMapping("/{testId}/viewTest")
    public String viewTest(@PathVariable Long testId, Model model) {
        Test test = testService.getTestById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        model.addAttribute("test", test);
        return "/admin/tests/viewTest";
    }

    @GetMapping("/{testId}/addQuestion")
    public String showAddQuestionForm(@PathVariable Long testId, Model model) {
        Test test = testService.getTestById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));
        model.addAttribute("test", test);
        return "/admin/tests/addQuestion";
    }

    @PostMapping("/{testId}/addQuestion")
    public String addQuestion(@RequestParam Long testId,
                              HttpServletRequest request,
                              @RequestParam String questionText,
                              @RequestParam Map<String, String> requestParams,
                              @RequestParam String action,
                              Model model) {
        try {
            Test test = testService.getTestById(testId)
                    .orElseThrow(() -> new RuntimeException("Тест не найден"));

            System.out.println("=== Полученные параметры ===");
            request.getParameterMap().forEach((key, values) -> {
                System.out.println(key + " = " + Arrays.toString(values));
            });

            List<Answer> answerList = new ArrayList<>();
            int i = 0;
            while (requestParams.containsKey("answers[" + i + "]")) {
                Answer answer = new Answer();
                String paramKey =  "correctAnswers[" + i + "]";
                Boolean isCorrect = requestParams.containsKey(paramKey);
                answer.setAnswerText(requestParams.get("answers[" + i + "]"));
                answer.setIsCorrect(isCorrect);
                answerList.add(answer);
                i++;
                answer.setAnswerNum(i);
            }

            Integer orderNum = questionService.getMaxOrderNum(test) + 1;

            Question newQuestion = new Question();
            newQuestion.setText(questionText);
            newQuestion.setTest(test);
            newQuestion.setOrderNum(orderNum);
            for (Answer answer : answerList) {
                answer.setQuestion(newQuestion);
            }
            newQuestion.setAnswers(answerList);
            questionService.saveQuestionWithAnswers(newQuestion);
            model.addAttribute("message", "Вопрос успешно добавлен к тесту!");

            if ("finish".equals(action)) {
                return "redirect:/admin/tests/" + testId + "/viewTest";
            } else {
                return "redirect:/admin/tests/" + testId + "/addQuestion";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при добавлении вопроса: " + e.getMessage());
        }
        return "admin/tests/addQuestion";
    }

    @GetMapping("/{questionId}/editQuestion")
    public String showEditQuestionForm(@PathVariable Long questionId, Model model) {
        Question question = questionService.getQuestionById(questionId).orElseThrow(()-> new RuntimeException("Вопрос не найден"));
        model.addAttribute("question", question);
        //model.addAttribute("answers", question.getAnswers());
        return "admin/tests/editQuestion";
    }

    @PostMapping("/{questionId}/editQuestion")
    public String editQuestion(@PathVariable Long questionId,
                               @RequestParam Map<String, String> requestParams,
                               Model model) {
        Question question;
        try {
            Optional<Question> questionOpt = questionService.getQuestionById(questionId);
            if (questionOpt.isEmpty()) {
                model.addAttribute("error", "Вопрос не найден!");
                return "admin/tests/editQuestion";
            }
            question = questionOpt.get();
            String questionText = requestParams.get("questionText");
            question.setText(questionText);
            List<Answer> existingAnswers = question.getAnswers();
            if (existingAnswers.isEmpty()) {
                existingAnswers = new ArrayList<>();
            }
            //List<Answer> answerList = new ArrayList<>();
            int i = 0;
            while (requestParams.containsKey("answers[" + i + "]")) {
                String answerText = requestParams.get("answers[" + i + "]");
                boolean isCorrect = requestParams.containsKey("correctAnswers[" + i + "]");

                if (i < existingAnswers.size()) {
                    Answer existingAnswer = existingAnswers.get(i);
                    existingAnswer.setAnswerText(answerText);
                    existingAnswer.setIsCorrect(isCorrect);
                } else {
                    Answer newAnswer = new Answer();
                    newAnswer.setAnswerText(answerText);
                    newAnswer.setIsCorrect(isCorrect);
                    existingAnswers.add(newAnswer);
                }
/*
                Answer answer = new Answer();
                String paramKey =  "correctAnswers[" + i + "]";
                Boolean isCorrect = requestParams.containsKey(paramKey);
                answer.setAnswerText(requestParams.get("answers[" + i + "]"));
                answer.setIsCorrect(isCorrect);
                answerList.add(answer);
*/
                i++;
            }

           /* Optional<Question> questionOpt = questionService.getQuestionById(questionId);
            if (questionOpt.isEmpty()) {
                model.addAttribute("error", "Вопрос не найден!");
                return "admin/tests/editQuestion";
            }
            question = questionOpt.get();
            question.setText(questionText);

            for (Answer answer : answerList) {
                answer.setQuestion(question);
            }*/

            if (i < existingAnswers.size()) {
                for (int j = i; j < existingAnswers.size(); j++) {
                    Answer toRemove = existingAnswers.get(j);
                    answerService.deleteAnswerById(toRemove.getId());
                }
                existingAnswers.subList(i, existingAnswers.size()).clear();
            }

            question.setAnswers(existingAnswers);
            questionService.updateQuestion(question.getId(), question);
            model.addAttribute("message", "Вопрос успешно обновлен!");
            model.addAttribute("question", question);
            return "redirect:/admin/tests/" + question.getTest().getId() + "/viewTest";
        }catch (Exception e){
            model.addAttribute("error", "Ошибка при обновлении вопроса: " + e.getMessage());
        }
        return "admin/tests/editQuestion";
    }
}
