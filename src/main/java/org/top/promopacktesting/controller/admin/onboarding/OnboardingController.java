package org.top.promopacktesting.controller.admin.onboarding;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.onboarding.OnboardingPlan;
import org.top.promopacktesting.model.AssignStatus;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.model.onboarding.*;
import org.top.promopacktesting.service.UserService;
import org.top.promopacktesting.service.onboarding.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin/onboarding")
@PreAuthorize("hasRole('ADMIN')")
public class OnboardingController {

    @Autowired
    private UserService userService;

    @Autowired
    private OnboardingRoleService onboardingRoleService;

    @Autowired
    private OnboardingPlanService onboardingPlanService;

    @Autowired
    private OnboardingStageService onboardingStageService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityDetailsService activityDetailsService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private RegulatoryDocService regulatoryDocService;

    @GetMapping
    public String showOnboardingDashboard(Model model){
        model.addAttribute("username", userService.getCurrentUsername());
        System.out.println("Onboarding Dashboard");
        return "admin/onboarding/onboarding";
    }

    @GetMapping("/onboardingPlans/assigned-onboardings")
    public String assignedOnboardings(
            @RequestParam(value = "showClosed", defaultValue = "false") boolean showClosed,
            Model model) {

        List<OnboardingPlan> onboardingPlans = onboardingPlanService.getOnboardingPlans(showClosed);

        model.addAttribute("username", userService.getCurrentUsername());
        model.addAttribute("onboardingPlans", onboardingPlans);
        model.addAttribute("showClosed", showClosed);
        return "admin/onboarding/onboardingPlans/assigned-onboardings";
    }

    @GetMapping("/onboardingPlans/add-onboarding")
    public String showCreatePlanForm(Model model) {
        model.addAttribute("username", userService.getCurrentUsername());
        model.addAttribute("plan", new OnboardingPlan());
        model.addAttribute("users", userService.getAllActiveUsers());
        model.addAttribute("equipment", equipmentService.getAll());
        return "admin/onboarding/onboardingPlans/add-onboarding";
    }

    @PostMapping("/onboardingPlans/{id}/edit")
    public String updateOnboardingPlan(
            @PathVariable Long id,
            @RequestParam Long equipmentId,
            @RequestParam LocalDate assignedAt,
            Model model) {

        Optional<OnboardingPlan> planOpt = onboardingPlanService.getById(id);
        Optional<Equipment> equipmentOpt = equipmentService.getById(equipmentId);

        if (planOpt.isEmpty()) {
            model.addAttribute("error", "Программа адаптации не найдена");
            model.addAttribute("equipment", equipmentService.getAll());
            return "admin/onboarding/onboardingPlans/edit-onboarding";
        }

        if (equipmentOpt.isEmpty()) {
            model.addAttribute("error", "Оборудование не найдено");
            model.addAttribute("plan", planOpt.get());
            model.addAttribute("equipment", equipmentService.getAll());
            return "admin/onboarding/onboardingPlans/edit-onboarding";
        }

        OnboardingPlan plan = planOpt.get();
        plan.setEquipment(equipmentOpt.get());
        plan.setAssignedAt(assignedAt);
        
        onboardingPlanService.save(plan);

        return "redirect:/admin/onboarding/onboardingPlans/assigned-onboardings";
    }

    @PostMapping("/onboardingPlans/add-onboarding")
    public String createOnboardingPlan(
            @RequestParam Long employeeId,
            @RequestParam Long equipmentId,
            @RequestParam LocalDate assignedAt,
            Model model) {

        Optional<User> employeeOpt = userService.getUserById(employeeId);
        Optional<Equipment> equipmentOpt = equipmentService.getById(equipmentId);

        if (employeeOpt.isEmpty()) {
            model.addAttribute("error", "Сотрудник не найден");
            model.addAttribute("users", userService.getAllActiveUsers());
            model.addAttribute("equipment", equipmentService.getAll());
            return "admin/onboarding/onboardingPlans/add-onboarding";
        }

        if (equipmentOpt.isEmpty()) {
            model.addAttribute("error", "Оборудование не найдено");
            model.addAttribute("users", userService.getAllActiveUsers());
            model.addAttribute("equipment", equipmentService.getAll());
            return "admin/onboarding/onboardingPlans/add-onboarding";
        }

        OnboardingPlan plan = new OnboardingPlan();
        plan.setEmployee(employeeOpt.get());
        plan.setEquipment(equipmentOpt.get());
        plan.setStatus(AssignStatus.ASSIGNED);
        plan.setAssignedAt(assignedAt);
        
        onboardingPlanService.save(plan);

        return "redirect:/admin/onboarding/onboardingPlans/assigned-onboardings";
    }

    @GetMapping("/onboardingPlans/{id}/edit")
    public String showEditPlanForm(@PathVariable Long id,
                                   Model model,
                                   Authentication authentication,
                                   HttpServletRequest request) {

        Optional<OnboardingPlan> planOpt = onboardingPlanService.findByOnboardingPlanIdWithStages(id);

        if (planOpt.isEmpty()) {
            model.addAttribute("error", "Программа адаптации не найдена");
            return "admin/onboarding/onboardingPlans/assigned-onboardings";
        }

        OnboardingPlan plan = planOpt.get();

        model.addAttribute("username", userService.getCurrentUsername());
        model.addAttribute("plan", plan);
        model.addAttribute("equipment", equipmentService.getAll());
        model.addAttribute("stagesWithFlag", getStagesByPlan(id));
        return "admin/onboarding/onboardingPlans/edit-onboarding";
    }

    @GetMapping("/onboardingPlans/{id}/add-stage")
    public String showAddStageForm(@PathVariable Long id, Model model) {
        Optional<OnboardingPlan> planOpt = onboardingPlanService.findByOnboardingPlanIdWithStages(id);
        if (planOpt.isEmpty()) {
            model.addAttribute("error", "Программа адаптации не найдена");
            return "admin/onboarding/onboardingPlans/assigned-onboardings";
        }

        model.addAttribute("username", userService.getCurrentUsername());
        model.addAttribute("plan", planOpt.get());
        model.addAttribute("users", userService.getAllActiveUsers());
        model.addAttribute("equipment", equipmentService.getAll());
        model.addAttribute("activities", activityService.getAll());
        model.addAttribute("regulatoryDocs", regulatoryDocService.getAll());
        model.addAttribute("activityDetails", activityDetailsService.getAll());
        model.addAttribute("onboardingRoles", onboardingRoleService.getAll());
        model.addAttribute("now", LocalDate.now());
        model.addAttribute("contextPath", "");

        model.addAttribute("now", LocalDate.now());
        return "admin/onboarding/onboardingPlans/add-stage";
    }

    @GetMapping("/onboardingPlans/{id}/view")
    public String showViewPlanForm(@PathVariable Long id, Model model) {
        Optional<OnboardingPlan> planOpt = onboardingPlanService.findByOnboardingPlanIdWithStages(id);

        if (planOpt.isEmpty()) {
            model.addAttribute("error", "Программа адаптации не найдена");
            return "admin/onboarding/onboardingPlans/assigned-onboardings";
        }

        OnboardingPlan plan = planOpt.get();

        model.addAttribute("username", userService.getCurrentUsername());
        model.addAttribute("plan", plan);
        model.addAttribute("equipment", equipmentService.getAll());
        model.addAttribute("stagesWithFlag", getStagesByPlan(id));
        return "admin/onboarding/onboardingPlans/view-onboarding";
    }

    @PostMapping("/onboardingPlans/{id}/add-stage")
    public String addOnboardingStage(
            @PathVariable Long id,
            @RequestParam String shift,
            @RequestParam String deadline,
            @RequestParam Long activityId,
            @RequestParam Long activityDetailsId,
            @RequestParam(required = false) Long regulatoryDocId,
            @RequestParam Long onboardingRoleId,
            @RequestParam(required = false) Long mentorId,

            Model model) {

        Optional<OnboardingPlan> planOpt = onboardingPlanService.findByOnboardingPlanId(id);
        Optional<Activity> activityOpt = activityService.getById(activityId);
        Optional<ActivityDetails> activityDetailsOpt = activityDetailsService.getById(activityDetailsId);
        Optional<OnboardingRole> onboardingRoleOpt = onboardingRoleService.getByRoleId(onboardingRoleId);

        if (planOpt.isEmpty()) {
            model.addAttribute("error", "Программа адаптации не найдена");
            return "admin/onboarding/onboardingPlans/edit-onboarding";
        }

        if (activityOpt.isEmpty()) {
            model.addAttribute("error", "Действие не найдено");
            return "admin/onboarding/onboardingPlans/edit-onboarding";
        }

        if (activityDetailsOpt.isEmpty()) {
            model.addAttribute("error", "Расшифровка не найдена");
            return "admin/onboarding/onboardingPlans/edit-onboarding";
        }

        if (onboardingRoleOpt.isEmpty()) {
            model.addAttribute("error", "Роль адаптации не найдена");
            return "admin/onboarding/onboardingPlans/edit-onboarding";
        }

        OnboardingPlan plan = planOpt.get();

        OnboardingStage stage = new OnboardingStage();
        Shift shiftEnum = Shift.valueOf(shift.toUpperCase());
        stage.setShiftName(shiftEnum);
        stage.setDeadline(LocalDate.parse(deadline));
        stage.setActivity(activityOpt.get());
        if (regulatoryDocId != null && regulatoryDocService.getByDocId(regulatoryDocId).isPresent()) {
            stage.setRegulatoryDoc(regulatoryDocService.getByDocId(regulatoryDocId).get());
        }
        stage.setActivityDetails(activityDetailsOpt.get());
        stage.setOnboardingRole(onboardingRoleOpt.get());
        if (mentorId != null && userService.getUserById(mentorId).isPresent()) {
            stage.setMentor(userService.getUserById(mentorId).get());
        }
        stage.setOnboardingPlan(plan);
        stage.setStatus(AssignStatus.ASSIGNED);

        onboardingStageService.save(stage);


        return "redirect:/admin/onboarding/onboardingPlans/{id}/edit";
    }

    @GetMapping("/api/activity-details")
    public ResponseEntity<List<ActivityDetails>> getAllActivityDetails() {
        return ResponseEntity.ok(activityDetailsService.getAll());
    }

    @PostMapping("/onboardingPlans/{id}/delete-stage")
    public String deleteActivityDetails(@PathVariable Long id,
                                        Model model) {
        Optional<OnboardingStage> stageOpt = onboardingStageService.findById(id);
        if (stageOpt.isEmpty()) {
            model.addAttribute("error", "Этап не найден");
            return "admin/onboarding/onboardingPlans/edit-onboarding";
        }

        onboardingStageService.delete(id);
        return "redirect:/admin/onboarding/onboardingPlans/edit-onboarding";
    }

    @PostMapping("/api/stage/complete/{stageId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // ← только админ
    public ResponseEntity<Void> completeStage(@PathVariable Long stageId) {
        onboardingStageService.complete(stageId);
        return ResponseEntity.ok().build();
    }

    private List<Map<String, Object>> getStagesByPlan(Long id){

        OnboardingPlan plan = onboardingPlanService.findByOnboardingPlanId(id).
                orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        LocalDate now = LocalDate.now();
        List<Map<String, Object>> stageList = new ArrayList<>();
        for (OnboardingStage stage : plan.getStages()) {
            Map<String, Object> stageMap = new HashMap<>();
            stageMap.put("stage", stage);

            String deadlineStr = (stage.getDeadline() != null)
                    ? DateTimeFormatter.ofPattern("yyyy-MM-dd").format(stage.getDeadline())
                    : "";
            stageMap.put("deadlineStr", deadlineStr);
            String shiftName = "Первая смена";
            if (stage.getShiftName().name().equals("EVENING")) {
                shiftName = "Вторая смена";
            }
            stageMap.put("shiftName", shiftName);
            boolean isOverdue = stage.getDeadline() != null && !stage.getDeadline().isAfter(now);
            stageMap.put("isOverdue", isOverdue);
            stageList.add(stageMap);

            String finished_at = (stage.getFinishedAt() != null)
                    ? DateTimeFormatter.ofPattern("yyyy-MM-dd").format(stage.getFinishedAt()) : "Не выполнено";
            stageMap.put("finished_at", finished_at);
        }
        return stageList;
    }
}
