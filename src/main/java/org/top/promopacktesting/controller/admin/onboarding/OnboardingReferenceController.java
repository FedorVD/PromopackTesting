package org.top.promopacktesting.controller.admin.onboarding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.onboarding.*;
import org.top.promopacktesting.service.onboarding.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/onboarding/references")
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public class OnboardingReferenceController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityDetailsService activityDetailsService;

    @Autowired
    private OnboardingRoleService onboardingRoleService;

    @Autowired
    private RegulatoryDocService regulatoryDocService;

    // Страница управления справочниками
    @GetMapping
    public String showReferences(Model model) {
        model.addAttribute("equipment", equipmentService.getAll());
        model.addAttribute("activities", activityService.getAll());
        model.addAttribute("activityDetails", activityDetailsService.getAll());
        model.addAttribute("onboardingRoles", onboardingRoleService.getAll());
        model.addAttribute("regulatoryDocs", regulatoryDocService.getAll());
        return "admin/onboarding/references/references";
    }

    // Справочник Оборудование (Станки)

    @GetMapping("/equipment")
    public String showAllEquipments(Model model) {
        List<Equipment> equipment = equipmentService.getAll();
        model.addAttribute("equipment", equipment);
        return "admin/onboarding/references/equipment";
    }

    @GetMapping("/add-equipment")
    public String showEquipmentAddForm(Model model) {
        model.addAttribute("equipment", new Equipment());
        return "admin/onboarding/references/add-equipment";
    }

    @PostMapping("/add-equipment")
    public String addEquipment(@RequestParam String name) {
        Equipment equipment = new Equipment();
        equipment.setName(name);
        equipmentService.save(equipment);
        return "redirect:/admin/onboarding/references/equipment";
    }

    @GetMapping("/{equipmentId}/edit-equipment")
    public String showEquipmentEditForm(@PathVariable Long equipmentId, Model model) {
        Optional<Equipment> equipmentOpt = equipmentService.getById(equipmentId);
        if (equipmentOpt.isEmpty()) {
            model.addAttribute("error", "Оборудование не найдено");
            return "redirect:/admin/onboarding/equipments";
        } else{
            model.addAttribute("equipment", equipmentOpt.get());
            return "admin/onboarding/references/edit-equipment";
        }
    }

    @PostMapping("/{equipmentId}/edit-equipment")
    public String editEquipment(@RequestParam Long equipmentId,
                                @RequestParam String name,
                                Model model) {
        Optional<Equipment> equipmentOpt = equipmentService.getById(equipmentId);
        if (equipmentOpt.isEmpty()) {
            model.addAttribute("error", "Оборудование не найдено");
            return "admin/onboarding/references/equipment";
        } else {
            Equipment equipment = equipmentOpt.get();
            equipment.setName(name);
            equipmentService.save(equipment);
            model.addAttribute("success", "Наименование оборудования успешно изменено");
            return "redirect:/admin/onboarding/references/equipment";
        }
    }

/*    @DeleteMapping("/equipments/{equipmentId}/delete")
    public String deleteEquipment(@RequestParam Long equipmentId) {
        equipmentService.deleteEquipmentById(equipmentId);
        return "redirect:/admin/onboarding/equipments";
    }*/

    // Роли в адаптации
    @GetMapping("/onboardingRoles")
    public String showAllOnboardingRoles(Model model) {
        List<OnboardingRole> onboardingRoles = onboardingRoleService.getAll();
        model.addAttribute("onboardingRoles", onboardingRoles);
        return "admin/onboarding/references/onboardingRoles";
    }

    @GetMapping("/add-onboardingRoles")
    public String showOnboardingRoleAddForm(Model model) {
        model.addAttribute("onboardingRole", new OnboardingRole());
        return "admin/onboarding/references/add-onboardingRoles";
    }

    @PostMapping("/add-onboardingRole")
    public String addOnboardingRole(@RequestParam String name) {
        OnboardingRole onboardingRole = new OnboardingRole();
        onboardingRole.setRoleName(name);
        onboardingRoleService.save(onboardingRole);
        return "redirect:/admin/onboarding/references/onboardingRoles";
    }

    @GetMapping("/{roleId}/edit-onboardingRole")
    public String showOnboardingRoleEditForm(@PathVariable Long roleId,
                                             Model model) {
        Optional<OnboardingRole> onboardingRoleOpt = onboardingRoleService.getByRoleId(roleId);
        if (onboardingRoleOpt.isEmpty()) {
            model.addAttribute("error", "Роль не найдена");
            return "admin/onboarding/references/onboardingRoles";
        } else {
            model.addAttribute("onboardingRole", onboardingRoleOpt.get());
            return "admin/onboarding/references/edit-onboardingRoles";
        }
    }

    @PostMapping("/{roleId}/edit-onboardingRole")
    public String editOnboardingRole(@RequestParam Long roleId,
                                     @RequestParam String name,
                                     Model model) {
        Optional<OnboardingRole> onboardingRoleOpt = onboardingRoleService.getByRoleId(roleId);
        if (onboardingRoleOpt.isEmpty()) {
            model.addAttribute("error", "Роль не найдена");
            return "admin/onboarding/references/onboardingRoles";
        } else{
            OnboardingRole onboardingRole = onboardingRoleOpt.get();
            onboardingRole.setRoleName(name);
            onboardingRoleService.save(onboardingRole);
            model.addAttribute("success", "Роль успешно изменена");
            return "redirect:/admin/onboarding/references/onboardingRoles";
        }
    }

/*    @GetMapping("/onboardingRole/{roleId}/delete")
    public String deleteOnboardingRole(@RequestParam Long roleId) {
        onboardingRoleService.deleteRoleById(roleId);
        return "redirect:/admin/onboarding/references";
    }*/

    // Документы
    @GetMapping("/regulatoryDocs")
    public String showAllRegulatoryDocs(Model model) {
        List<RegulatoryDoc> regulatoryDocs = regulatoryDocService.getAll();
        model.addAttribute("regulatoryDocs", regulatoryDocs);
        return "admin/onboarding/references/regulatoryDocs";
    }

    @GetMapping("/add-regulatoryDocs")
    public String showRegulatoryDocAddForm(Model model) {
        model.addAttribute("regulatoryDoc", new RegulatoryDoc());
        return "admin/onboarding/references/add-regulatoryDocs";
    }

    @PostMapping("/add-regulatoryDocs")
    public String addRegulatoryDoc(@RequestParam String docName,
                                   @RequestParam String docUrl) {
        RegulatoryDoc regulatoryDoc = new RegulatoryDoc();
        regulatoryDoc.setDocName(docName);
        regulatoryDoc.setDocUrl(docUrl);
        regulatoryDocService.save(regulatoryDoc);
        return "redirect:/admin/onboarding/references/regulatoryDocs";
    }

    @GetMapping("/{docId}/edit-regulatoryDocs")
    public String showRegulatoryDocEditForm(@PathVariable Long docId,
                                            Model model) {
        Optional<RegulatoryDoc> regulatoryDocOpt = regulatoryDocService.getByDocId(docId);
        if (regulatoryDocOpt.isEmpty()) {
            model.addAttribute("error", "Документ не найден");
            return "admin/onboarding/references/regulatoryDocs";
        } else {
            model.addAttribute("regulatoryDoc", regulatoryDocOpt.get());
            return "admin/onboarding/references/edit-regulatoryDocs";
        }
    }

    @PostMapping("/{docId}/edit-regulatoryDocs")
    public String editRegulatoryDoc(@RequestParam Long docId,
                                    @RequestParam String docName,
                                    @RequestParam String docUrl,
                                    Model model){
        Optional<RegulatoryDoc> regulatoryDocOpt = regulatoryDocService.getByDocId(docId);
        if (regulatoryDocOpt.isEmpty()) {
            model.addAttribute("error", "Документ не найден");
            return "admin/onboarding/references/regulatoryDocs";
        } else {
            RegulatoryDoc regulatoryDoc = regulatoryDocOpt.get();
            regulatoryDoc.setDocName(docName);
            regulatoryDoc.setDocUrl(docUrl);
            regulatoryDocService.save(regulatoryDoc);
            model.addAttribute("success", "Документ успешно изменен");
            return "redirect:/admin/onboarding/references/regulatoryDocs";
        }
    }

    // Действия
    @GetMapping("/activities")
    public String showAllActivities(Model model) {
        List<Activity> activities = activityService.getAll();
        model.addAttribute("activities", activities);
        return "admin/onboarding/references/activities";
    }

    @GetMapping("/add-activities")
    public String showActivitiesAddForm(Model model) {
        model.addAttribute("activities", new Activity());
        return "admin/onboarding/references/add-activities";
    }

    @PostMapping("/add-activities")
    public String addActivity(@RequestParam String name) {
        Activity activity = new Activity();
        activity.setActivityName(name);
        activityService.save(activity);
        return "redirect:/admin/onboarding/references/activities";
    }

    @GetMapping("/{activityId}/edit-activities")
    public String showActivityEditForm(@PathVariable Long activityId,
                                       Model model) {
        Optional<Activity> activityOpt = activityService.getByActivityId(activityId);
        if (activityOpt.isEmpty()) {
            model.addAttribute("error", "Действие не найдено");
            return "admin/onboarding/references/activities";
        } else {
            model.addAttribute("activity", activityOpt.get());
            return "admin/onboarding/references/edit-activities";
        }
    }

    @PostMapping("/{activityId}/edit-activities")
    public String editActivity(@RequestParam Long activityId,
                               @RequestParam String name,
                               Model model) {
        Optional<Activity> activityOpt = activityService.getByActivityId(activityId);
        if (activityOpt.isEmpty()) {
            model.addAttribute("error", "Действие не найдено");
            return "admin/onboarding/references/activities";
        } else {
            Activity activity = activityOpt.get();
            activity.setActivityName(name);
            activityService.save(activity);
            model.addAttribute("success", "Действие успешно изменено");
            return "redirect:/admin/onboarding/references/activities";
        }
    }

    // Расшифровки действий
    @GetMapping("/activityDetails")
    public String showAllActivityDetails(Model model) {
        List<Activity> activities = activityService.getAll();
        model.addAttribute("activities", activities);
        return "admin/onboarding/references/activityDetails";
    }

    @GetMapping("/add-activityDetails")
    public String showActivityDetailsAddForm(Model model) {
        model.addAttribute("activities", new Activity());
        return "admin/onboarding/references/add-activityDetails";
    }

    @PostMapping("/add-activityDetails")
    public String addActivityDetails(@RequestParam String name,
                                     @RequestParam Long activityId,
                                     @RequestParam Long OnboardingRoleId,
                                     Model model) {
        Optional<Activity> activityOpt = activityService.getByActivityId(activityId);
        Optional<OnboardingRole> onboardingRoleOpt = onboardingRoleService.getByRoleId(OnboardingRoleId);
        if (activityOpt.isEmpty()) {
            model.addAttribute("error", "Действие не найдено");
            return "admin/onboarding/references/activityDetails";
        } else if (onboardingRoleOpt.isEmpty()) {
            model.addAttribute("error", "Роль не найдена");
            return "admin/onboarding/references/activityDetails";
        }else {
            ActivityDetails activityDetails = new ActivityDetails();
            activityDetails.setActivityDetailsName(name);
            activityDetails.setActivity(activityOpt.get());
            activityDetails.setOnboardingRole(onboardingRoleOpt.get());
            activityDetailsService.save(activityDetails);
            return "redirect:/admin/onboarding/references/activityDetails";
        }
    }

    @GetMapping("/{activityDetailsId}/edit-activityDetails")
    public String showActivityDetailsEditForm(@PathVariable Long activityDetailsId,
                                              Model model) {
        Optional<Activity> activityOpt = activityService.getByActivityId(activityDetailsId);
        if (activityOpt.isEmpty()) {
            model.addAttribute("error", "Действие не найдено");
            return "admin/onboarding/references/activityDetails";
        } else {
            model.addAttribute("activity", activityOpt.get());
            return "admin/onboarding/references/edit-activityDetails";
        }
    }

    @PostMapping("/{activityDetailsId}/edit-activityDetails")
    public String editActivityDetails(@RequestParam Long activityDetailsId,
                                      @RequestParam String name,
                                      @RequestParam Long activityId,
                                      @RequestParam Long OnboardingRoleId,
                                      Model model) {
        Optional<ActivityDetails> activityDetailsOpt = activityDetailsService.getActivityDetailsById(activityDetailsId);
        Optional<Activity> activityOpt = activityService.getByActivityId(activityId);
        Optional<OnboardingRole> onboardingRoleOpt = onboardingRoleService.getByRoleId(OnboardingRoleId);
        if (activityDetailsOpt.isEmpty()) {
            model.addAttribute("error", "Расшифровка действия не найдена");
            return "admin/onboarding/references/activityDetails";
        } else if (activityOpt.isEmpty()) {
            model.addAttribute("error", "Действие не найдено");
            return "admin/onboarding/references/activityDetails";
        } else if (onboardingRoleOpt.isEmpty()) {
            model.addAttribute("error", "Роль не найдена");
            return "admin/onboarding/references/activityDetails";
        } else {
            ActivityDetails activityDetails = activityDetailsOpt.get();
            activityDetails.setActivityDetailsName(name);
            activityDetails.setActivity(activityOpt.get());
            activityDetails.setOnboardingRole(onboardingRoleOpt.get());
            activityDetailsService.save(activityDetails);
            return "redirect:/admin/onboarding/references/activityDetails";
        }
    }
}
