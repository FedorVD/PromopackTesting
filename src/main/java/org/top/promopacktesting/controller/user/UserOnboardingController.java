package org.top.promopacktesting.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.model.onboarding.OnboardingPlan;
import org.top.promopacktesting.model.onboarding.OnboardingRole;
import org.top.promopacktesting.model.onboarding.OnboardingStage;
import org.top.promopacktesting.model.onboarding.UserRoleInOnboarding;
import org.top.promopacktesting.service.UserService;
import org.top.promopacktesting.service.onboarding.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
public class UserOnboardingController {

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

    @Autowired
    private UserRoleInOnboardingService userRoleInOnboardingService;

    @GetMapping("/userOnboarding/onboarding-list")
    public String viewActiveOnboardingPlans(Authentication authentication, Model model) {
        // 1. Получаем текущего пользователя
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        // 2. Получаем все роли пользователя в адаптации
        List<UserRoleInOnboarding> userRolesInOnboarding = userRoleInOnboardingService.findByUserId(currentUser.getId());
        if (userRolesInOnboarding.isEmpty()) {
            model.addAttribute("message", "Вам не назначена роль в программах адаптации.");
            model.addAttribute("plans", Collections.emptyList());
            return "user/user";
        }

        List<OnboardingRole> userRoles = userRolesInOnboarding.stream()
                .map(UserRoleInOnboarding::getOnboardingRole)
                .toList();

        // 3. Находим все не завершённые этапы (finishedAt == null) по ролям пользователя

        List<OnboardingStage> activeStages = new ArrayList<>(); //
        for (OnboardingRole userRole : userRoles){
            List<OnboardingStage> stageForThisRle = onboardingStageService.findByOnboardingRoleAndFinishedAtIsNull(userRole);
            activeStages.addAll(stageForThisRle);
        }

        if (activeStages.isEmpty()) {
            model.addAttribute("message", "У вас нет незавершённых этапов в программах адаптации.");
            model.addAttribute("plans", Collections.emptyList());
            return "user/userOnboarding/onboarding-list";
        }
        // 4. Собираем уникальные планы адаптации
        Set<OnboardingPlan> activePlans = activeStages.stream()
                .map(OnboardingStage::getOnboardingPlan)
                .collect(Collectors.toSet());

        // 5. Добавляем в модель
        model.addAttribute("plans", activePlans);
        model.addAttribute("user", currentUser);

        // Дополнительно: можно передать этапы с разбивкой по планам
        Map<OnboardingPlan, List<OnboardingStage>> planToStages = activeStages.stream()
                .collect(Collectors.groupingBy(OnboardingStage::getOnboardingPlan));
        model.addAttribute("planToStages", planToStages);

        return "user/userOnboarding/onboarding-list";
    }
}
