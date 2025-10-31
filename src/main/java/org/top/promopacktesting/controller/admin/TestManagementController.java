package org.top.promopacktesting.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.top.promopacktesting.model.*;
import org.top.promopacktesting.service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    @Autowired
    private ImageService imageService;

    @Value("${app.image.upload-dir}")
    private String uploadDir;

    @Value("${app.image.base-url}")
    private String baseUrl;

    @GetMapping("/tests")
    public String showTests(@RequestParam(required = false) String search,
                            @RequestParam(required = false) Long themeId,
                            @RequestParam(required = false) String status,
                            Model model) {
        sendCurrentUsername(model);
        List<Test> tests = new ArrayList<>();

        if (themeId != null) {
            tests = testService.getTestsByThemeId(themeId);
        } else if (status != null && !status.isEmpty()) {
            if ("active".equals(status)) {
                tests = testService.getAllActiveTests();
                model.addAttribute("selectedStatus", true);
            } else if ("inactive".equals(status)) {
                tests = testService.getAllInactiveTests();
                model.addAttribute("selectedStatus", false);
            }
        } else if (search != null) {
            if (search.isEmpty()) {
                tests = testService.getAllTests();
            } else {
                tests = testService.findByNameContainingIgnoreCase(search);
            }
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
        sendCurrentUsername(model);
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
        sendCurrentUsername(model);
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
                           HttpServletRequest request,
                           @RequestParam Long themeId,
                           @RequestParam Map<String, String> requestParams,
                           @RequestParam String testName,

                           Model model) {
        try{
            System.out.println("=== Полученные параметры ===");
            request.getParameterMap().forEach((key, values) -> {
                System.out.println(key + " = " + Arrays.toString(values));
            });

            Optional<Test> testOpt = testService.getTestById(testId);
            if (testOpt.isEmpty()){
                model.addAttribute("error", "Тест не найден!");
                return "admin/tests/editTest";
            } else {
                Test test = testOpt.get();
                test.setName(testName);
                test.setThemeTest(themeTestService.getThemeTestById(themeId).orElseThrow(() -> new RuntimeException("Тема не найдена")));
                Boolean isActive = requestParams.containsKey("isActive");
                test.setIsActive(isActive);
                testService.updateTest(test);
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
        sendCurrentUsername(model);
        Test test = testService.getTestById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        model.addAttribute("test", test);
        return "/admin/tests/viewTest";
    }

    @GetMapping("/{testId}/addQuestion")
    public String showAddQuestionForm(@PathVariable Long testId, Model model) {
        sendCurrentUsername(model);
        Test test = testService.getTestById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));
        model.addAttribute("question", new Question());
        model.addAttribute("test", test);
        return "/admin/tests/addQuestion";
    }

    @PostMapping("/{testId}/addQuestion")
    public String addQuestion(@RequestParam Long testId,
                              HttpServletRequest request,
                              @RequestParam String questionText,
                              @RequestParam Map<String, String> requestParams,
                              @RequestParam String action,
                              @RequestParam("image") MultipartFile image,
                              Model model) {
        try {
            Test test = testService.getTestById(testId)
                    .orElseThrow(() -> new RuntimeException("Тест не найден"));

            String imagePath = null;
            if (image != null && !image.isEmpty()) {
                imagePath = imageService.saveImage(image);
            }

            Integer orderNum = questionService.getMaxOrderNum(test) + 1;

            Question newQuestion = new Question();
            newQuestion.setText(questionText);
            newQuestion.setTest(test);
            newQuestion.setOrderNum(orderNum);
            newQuestion.setImagePath(imagePath);

            List<Answer> answerList = new ArrayList<>();
            int i = 0;
            while (requestParams.containsKey("answers[" + i + "]")) {
                Answer answer = new Answer();
                String paramKey = "correctAnswers[" + i + "]";
                Boolean isCorrect = requestParams.containsKey(paramKey);
                answer.setAnswerText(requestParams.get("answers[" + i + "]"));
                answer.setIsCorrect(isCorrect);
                answerList.add(answer);
                i++;
                answer.setAnswerNum(i);
            }

            for (Answer answer : answerList) {
                answer.setQuestion(newQuestion);
            }

            newQuestion.setAnswers(answerList);
            questionService.saveQuestionWithAnswers(newQuestion);
            model.addAttribute("message", "Вопрос успешно добавлен к тесту!");
            model.addAttribute("test", testService.getTestById(testId));

            if ("finish".equals(action)) {
                return "redirect:/admin/tests/" + testId + "/viewTest";
            } else {
                return "redirect:/admin/tests/" + testId + "/addQuestion";
            }
        } catch (IOException e) {
            model.addAttribute("error", "Ошибка при сохранении изображения: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при добавлении вопроса: " + e.getMessage());
        }
        return "redirect:/admin/tests/" + testId + "/addQuestion";
    }

    @GetMapping("/{questionId}/editQuestion")
    public String showEditQuestionForm(@PathVariable Long questionId, Model model) {
        sendCurrentUsername(model);
        Question question = questionService.getQuestionById(questionId)
                .orElseThrow(()-> new RuntimeException("Вопрос не найден"));
        model.addAttribute("question", question);
        return "admin/tests/editQuestion";
    }

    @PostMapping("/{questionId}/editQuestion")
    public String editQuestion(@PathVariable Long questionId,
                               @RequestParam Map<String, String> requestParams,
                               @RequestParam("image") MultipartFile image,
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
                i++;
            }

            if (i < existingAnswers.size()) {
                for (int j = i; j < existingAnswers.size(); j++) {
                    Answer toRemove = existingAnswers.get(j);
                    answerService.deleteAnswerById(toRemove.getId());
                }
                existingAnswers.subList(i, existingAnswers.size()).clear();
            }

            question.setAnswers(existingAnswers);



            if (!image.isEmpty()) {

                String oldImagePath = question.getImagePath();


                if (oldImagePath != null && !oldImagePath.isEmpty()) {
                    Path oldFilePath = Paths.get(uploadDir).resolve(oldImagePath.replace(baseUrl, ""));
                    try {
                        Files.deleteIfExists(oldFilePath);
                    } catch (IOException e) {
                        System.err.println("Не удалось удалить старое изображение: " + e.getMessage());
                    }
                }
                String newImagePath = imageService.saveImage(image);
                question.setImagePath(newImagePath);
            }

            questionService.updateQuestion(question.getId(), question);
            model.addAttribute("message", "Вопрос успешно обновлен!");
            model.addAttribute("question", question);
            return "redirect:/admin/tests/" + question.getTest().getId() + "/viewTest";
        }catch (Exception e){
            model.addAttribute("error", "Ошибка при обновлении вопроса: " + e.getMessage());
        }
        return "admin/tests/editQuestion";
    }

    @PostMapping("/saveQuestion")
    public String saveQuestion(@RequestParam String text,
                               @RequestParam("image") MultipartFile image,
                               Model model) {

        try {
            String imagePath = imageService.saveImage(image);

            Question question = new Question();
            question.setText(text);
            question.setImagePath(imagePath);

            questionService.updateQuestion(question.getId(), question);
            model.addAttribute("message", "Вопрос успешно сохранён");

        } catch (IOException e) {
            model.addAttribute("error", "Ошибка при сохранении изображения");
        }

        return "admin/questions/addQuestion";
    }

    public void sendCurrentUsername(Model model) {
        if (Objects.equals(userService.getCurrentUsername(), "Пользователь не найден")){
            model.addAttribute("error", "Пользователь не найден");
            model.addAttribute("username", "Ошибка входа");
        } else {
            model.addAttribute("username", userService.getCurrentUsername());
        }
    }
}
