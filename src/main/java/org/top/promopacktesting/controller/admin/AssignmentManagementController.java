package org.top.promopacktesting.controller.admin;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.util.StringUtils;
import org.top.promopacktesting.model.*;
import org.top.promopacktesting.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Controller
@RequestMapping("/admin/assignments")
@PreAuthorize("hasRole('ADMIN')")
public class AssignmentManagementController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PositionService positionService;

    @GetMapping("/assignTest")
    public String showAssignTestForm(@RequestParam(required = false) Long departmentId,
                                     @RequestParam(required = false) Long positionId,
                                     Model model) {
        sendCurrentUsername(model);
        List <User> users;
        if (departmentId != null && positionId != null) {
            users = userService.searchUsersByDepartmentIdAndPositionId(departmentId, positionId);
        } else if (departmentId != null) {
            users = userService.searchUsersByDepartmentId(departmentId);
        } else if (positionId != null) {
            users = userService.searchUsersByPositionId(positionId);
        } else {
            users = userService.getAllActiveUsers();
        }

        List<Test> tests = testService.getAllActiveTests();

        model.addAttribute("users", users);
        model.addAttribute("tests", tests);
        model.addAttribute("departments", departmentService.findAllDepartments());
        model.addAttribute("positions", positionService.findAllPositions());
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("positionId", positionId);
        model.addAttribute("currentUser", SecurityContextHolder.getContext().getAuthentication().getName());
        return "admin/assignments/assignTest";
    }

    @PostMapping("/assignTest")
    public String assignTestToUser(@RequestParam Long testId,
                                   @RequestParam List<Long> userIds,
                                   Model model) {

        if (userIds.isEmpty()) {
            model.addAttribute("message", "Не выбраны сотрудники для назначения теста");
            return "admin/assignments/assignTest";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User assigner = userService.getUserByUsername(currentUsername).orElseThrow(()-> new RuntimeException("Пользователь не найден"));


        try {
            assignmentService.assignTestToUsers(testId, userIds, assigner);
            model.addAttribute("message", "Тест успешно назначен " + userIds.size() + " сотрудникам");
        } catch (Exception e) {
            model.addAttribute("message", "Ошибка при назначении теста: " + e.getMessage());
        }

        return "redirect:/admin/assignments/assignments";
    }

    @GetMapping("/{assignmentId}/assignedTest")
    public String showAssignedTestForm(@PathVariable Long assignmentId, Model model) {
        sendCurrentUsername(model);
        AssignedTest assignedTest = assignmentService.getAssignedTestById(assignmentId).orElseThrow(()-> new RuntimeException("Назначенный тест не найден"));
        if (assignedTest == null) {
            model.addAttribute("error", "Назанченные тесты не найдены");
        }else {
            model.addAttribute("assignment", assignedTest);
        }
        return "admin/assignments/assignedTest";
    }

    @GetMapping("/assignments")
    public String getAssignments(@RequestParam (required = false) String testName,
                                    @RequestParam (required = false) String userName,
                                    @RequestParam (required = false) AssignedTest.TestStatus status,
                                    @RequestParam (required = false) Double testScore,
                                    Model model) {
        sendCurrentUsername(model);
        List<AssignedTest> assignments = new ArrayList<>();
        AssignedTest assignedTest;
        if (!StringUtils.isEmpty(testName) && !StringUtils.isEmpty(userName) && status != null) {
            assignments = assignmentService.getAssignedTestsByTestNameAndUserNameAndStatus(testName, userName, status);
        }else if(!StringUtils.isEmpty(testName) && !StringUtils.isEmpty(userName)){
            assignedTest = assignmentService.getAssignedTestByTestNameAndUserName(testName, userName);
            assignments.add(assignedTest);
        }else if (!StringUtils.isEmpty(userName) && status != null){
            assignments = assignmentService.getAssignedTestsByUserNameAndStatus(userName, status);
        }else if (!StringUtils.isEmpty(testName) && status != null){
            assignments = assignmentService.getAssignedTestsByTestNameAndStatus(testName, status);
        }else if (!StringUtils.isEmpty(userName)){
            assignments = assignmentService.getAssignedTestsByUserName(userName);
        }else if (!StringUtils.isEmpty(testName)){
            assignments = assignmentService.getAssignedTestsByTestName(testName);
        }else if (status != null){
            assignments = assignmentService.getAssignedTestsByStatus(status);
        }else if (testScore != null){
            assignments = assignmentService.getCompletedByTestScoreLessThanEqual(testScore);
        }
        else {
            assignments = assignmentService.getAssignedTests();
        }

        if (testScore != null) {
            assignments = assignments.stream()
                    .filter(a -> a.getStatus().equals(AssignedTest.TestStatus.COMPLETED)
                            && a.getTestScore() <= testScore)
                    .toList();
        }

        model.addAttribute("assignments", assignments);

        if (assignments.isEmpty()) {
            model.addAttribute("message", "По выбранным параметрам поиска тесты не найдены");
        }
        if (status != null) {
            model.addAttribute("status", status.name());
        }
        if (!StringUtils.isEmpty(testName)) {
            model.addAttribute("testName", testName);
        }
        if (!StringUtils.isEmpty(userName)){
            model.addAttribute("userName", userName);
        }
        if (testScore != null) {
            model.addAttribute("testScore", testScore);
        }
        return "admin/assignments/assignments";
    }

    @GetMapping("/exportToExcel")
    public RedirectView exportToExcel(HttpServletResponse response,
                                      @RequestParam(required = false) List<Long> assignmentsIds,
                                      RedirectAttributes redirectAttrs) throws IOException {

        List<AssignedTest> assignments = new ArrayList<>();
        if (assignmentsIds == null || assignmentsIds.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Тесты для экспорта не выбраны");
            return new RedirectView("/admin/assignments/assignments");
        }
        for (Long id : assignmentsIds) {
            assignments.add(assignmentService.getAssignedTestById(id).orElseThrow(() -> new RuntimeException("Тест не найден")));
        }
        if (assignments.size() == 0) {
            redirectAttrs.addFlashAttribute("error", "Нет назначенных тестов для экспорта");
            return new RedirectView("/admin/assignments/assignments");
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Назначенные тесты");

        Row headerRow = sheet.createRow(0);
        String[] header = {"Тема теста", "Тест", "Кому назначено", "Кто назначил", "Дата назначения", "Статус", "Результат"};
        for (int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
        }

        int rowNum = 1;
        for (AssignedTest assignedTest : assignments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(assignedTest.getTest().getThemeTest().getThemeName());
            row.createCell(1).setCellValue(assignedTest.getTest().getName());
            row.createCell(2).setCellValue(assignedTest.getUser().getName());
            row.createCell(3).setCellValue(assignedTest.getAssignedBy().getName());
            row.createCell(4).setCellValue(assignedTest.getAssignedAt().toString());
            row.createCell(5).setCellValue(assignedTest.getStatus().name().equals("ASSIGNED") ? "Назначен"
                    : assignedTest.getStatus().name().equals("IN_PROGRESS") ? "Начат"
                    : "Завершен");
            if (assignedTest.getStatus().equals(AssignedTest.TestStatus.COMPLETED)) {
                double result = Math.round(assignedTest.getTestScore()*10)/10;
                row.createCell(6).setCellValue(result);
            }else {
                row.createCell(6).setCellValue("-");
            }
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Assignments.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
        return null;
    }

    @GetMapping("/{id}/results")
    public String showTestResults(@PathVariable Long id, Model model) {
        sendCurrentUsername(model);
        Optional<AssignedTest> optionalAssignedTest = assignmentService.getAssignedTestById(id);
        if (optionalAssignedTest.isEmpty()) {
            model.addAttribute("error", "Назначенный тест не найден");
            return "admin/assignments/assignments";
        }

        AssignedTest assignedTest = optionalAssignedTest.get();
        List<QuestionWithAnswer> questionsWithAnswers = assignmentService.getQuestionsWithUserAndCorrectAnswers(id);

        model.addAttribute("assignedTest", assignedTest);
        model.addAttribute("questions", questionsWithAnswers);
        return "admin/assignments/adminTestResult";
    }

    @GetMapping("/{id}/exportResults")
    public void exportTestResultsToExcel(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<AssignedTest> optionalAssignedTest = assignmentService.getAssignedTestById(id);
        if (optionalAssignedTest.isEmpty()) {
            throw new RuntimeException("Назначенный тест не найден");
        }

        AssignedTest assignedTest = optionalAssignedTest.get();
        List<QuestionWithAnswer> questionsWithAnswers = assignmentService.getQuestionsWithUserAndCorrectAnswers(id);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Результаты теста");

        Row userDataRow = sheet.createRow(0);
        userDataRow.createCell(0).setCellValue("Тест:");
        userDataRow.createCell(1).setCellValue(assignedTest.getTest().getName());
        userDataRow = sheet.createRow(1);
        userDataRow.createCell(0).setCellValue("Назначен:");
        userDataRow.createCell(1).setCellValue(assignedTest.getUser().getName());
        userDataRow = sheet.createRow(2);
        userDataRow.createCell(0).setCellValue("Дата назначения:");
        userDataRow.createCell(1).setCellValue(assignedTest.getAssignedAt().toString());
        Row headerRow = sheet.createRow(3);
        String[] headers = {
                "Вопрос", "Пользователь прав", "Ответы пользователя", "Правильные ответы"
        };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 4;
        for (QuestionWithAnswer qwa : questionsWithAnswers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(qwa.getQuestion().getText());
            row.createCell(1).setCellValue(qwa.isCorrect() ? "✅" : "❌");

            StringBuilder userAnswers = new StringBuilder();
            for (UserSelectedAnswer selected : qwa.getUserAnswer().getSelectedAnswers()) {
                if (userAnswers.length() > 0){
                    userAnswers.append("\n");
                }
                userAnswers.append(selected.getAnswer().getAnswerText());
            }
            row.createCell(2).setCellValue(userAnswers.toString());

            StringBuilder correctAnswers = new StringBuilder();
            for (Answer answer : qwa.getCorrectAnswers()) {
                if (correctAnswers.length() > 0){
                    correctAnswers.append("\n");
                }
                correctAnswers.append(answer.getAnswerText());
            }
            row.createCell(3).setCellValue(correctAnswers.toString());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=results_" + id + ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
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
