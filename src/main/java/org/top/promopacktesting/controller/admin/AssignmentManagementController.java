package org.top.promopacktesting.controller.admin;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;
import org.top.promopacktesting.model.AssignedTest;
import org.top.promopacktesting.model.Test;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.service.AssignmentService;
import org.top.promopacktesting.service.TestService;
import org.top.promopacktesting.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

    @GetMapping("/assignTest")
    public String showAssignTestForm(Model model) {
        model.addAttribute("users", userService.getAllActiveUsers());
        model.addAttribute("tests", testService.getAllActiveTests());
        model.addAttribute("assignments",assignmentService.getAssignedTests());
        return "admin/assignments/assignTest";
    }

    @PostMapping("/assignTest")
    public String assignTestToUser(@RequestParam Long testId,
                                   @RequestParam Long userId,
                                   Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();
            User assigner = userService.getUserByUsername(currentUsername).get();
            assignmentService.assignTestToUser(testId, userId, assigner);
            List<User> users = userService.getAllActiveUsers();
            List<Test> tests = testService.getAllActiveTests();
            model.addAttribute("message", "Тест успешно назначен пользователю: " + userService.getUserById(userId).get().getName());
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при назначении теста: " + e.getMessage());
        }
        return "redirect:/admin/assignments/assignments";
    }

    @GetMapping("/{assignmentId}/assignedTest")
    public String showAssignedTestForm(@PathVariable Long assignmentId, Model model) {
        AssignedTest assignedTest = assignmentService.getAssignedTestById(assignmentId).get();
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

        if (assignments.isEmpty()) {
            model.addAttribute("message", "По выбранным параметрам поиска тесты не найдены");
        } else {
            model.addAttribute("assignments", assignments);
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
    public void exportToExcel(HttpServletResponse response,
                              @RequestParam List<Long> assignmentsIds) throws IOException {
        List<AssignedTest> assignments = new ArrayList<>();
        for (Long id : assignmentsIds) {
            assignments.add(assignmentService.getAssignedTestById(id).orElseThrow(() -> new RuntimeException("Тест не найден")));
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
    }
}
