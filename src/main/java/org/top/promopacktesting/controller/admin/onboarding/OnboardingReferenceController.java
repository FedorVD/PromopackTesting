package org.top.promopacktesting.controller.admin.onboarding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.User;
import org.top.promopacktesting.model.onboarding.*;
import org.top.promopacktesting.service.UserService;
import org.top.promopacktesting.service.onboarding.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
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
    private UserService userService;

    @Autowired
    private UserRoleInOnboardingService userRoleInOnboardingService;

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
        List<UserRoleInOnboarding> userRolesInOnboarding = userRoleInOnboardingService.getAll();
        model.addAttribute("onboardingRoles", onboardingRoles);
        model.addAttribute("userRolesInOnboarding", userRolesInOnboarding);
        return "admin/onboarding/references/onboardingRoles";
    }

    @GetMapping("/add-onboardingRole")
    public String showOnboardingRoleAddForm(Model model) {
        model.addAttribute("onboardingRole", new OnboardingRole());
        return "admin/onboarding/references/add-onboardingRole";
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

    @GetMapping("/assign-role")
    public String showAssignRoleForm(Model model) {
        List<User> users = userService.getAllActiveUsers();
        // Сортировка по имени (по алфавиту)
        users.sort(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER));

        model.addAttribute("users", users);
        model.addAttribute("onboardingRoles", onboardingRoleService.getAll());
        return "admin/onboarding/references/assign-role";
    }

    @PostMapping("/assign-role")
    public String assignRoleToUser(
            @RequestParam Long userId,
            @RequestParam Long roleId,
            Model model) {

        Optional<User> userOpt = userService.getUserById(userId);
        Optional<OnboardingRole> roleOpt = onboardingRoleService.getByRoleId(roleId);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Сотрудник не найден");
            return "redirect:/admin/onboarding/references/onboardingRoles";
        }
        if (roleOpt.isEmpty()) {
            model.addAttribute("error", "Роль не найдена");
            return "redirect:/admin/onboarding/references/onboardingRoles";
        }

        UserRoleInOnboarding userRole = new UserRoleInOnboarding();
        userRole.setUser(userOpt.get());
        userRole.setOnboardingRole(roleOpt.get());

        userRoleInOnboardingService.save(userRole);

        return "redirect:/admin/onboarding/references/onboardingRoles";
    }

    @PostMapping("/onboardingRoles/delete/{userRoleInOnboardingId}")
    public String deleteAssignedRole(@PathVariable Long userRoleInOnboardingId) {
        if (userRoleInOnboardingService.getById(userRoleInOnboardingId).isPresent()) {
            userRoleInOnboardingService.deleteById(userRoleInOnboardingId);
        }
        return "redirect:/admin/onboarding/references/onboardingRoles";
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

    @GetMapping("/add-regulatoryDoc")
    public String showRegulatoryDocAddForm(Model model) {
        model.addAttribute("regulatoryDoc", new RegulatoryDoc());
        return "admin/onboarding/references/add-regulatoryDoc";
    }

    @PostMapping("/add-regulatoryDoc")
    public String addRegulatoryDoc(@RequestParam String docName,
                                   @RequestParam String docPath, // Путь к файлу на сервере или URL,
                                   Model model) {
        // Валидация: проверяем, что путь не пустой
        if (docPath == null || docPath.trim().isEmpty()) {
            model.addAttribute("error", "Требуется указать путь к документу.");
            return "admin/onboarding/references/add-regulatoryDoc";
        }

        try {
            RegulatoryDoc doc = new RegulatoryDoc();
            doc.setDocName(docName);
            doc.setDocUrl(docPath.trim()); // Сохраняем как есть
            regulatoryDocService.save(doc);

            return "redirect:/admin/onboarding/references/regulatoryDocs";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при сохранении: " + e.getMessage());
            return "admin/onboarding/references/add-regulatoryDoc";
        }
    }

    @GetMapping("/download-doc/{id}")
    public ResponseEntity<byte[]> downloadDoc(@PathVariable Long id) throws IOException {

        RegulatoryDoc doc = regulatoryDocService.getByDocId(id)
                .orElseThrow(() -> new RuntimeException("Документ не найден"));
        // Проверяем, что путь к файлу не пустой
        String filePath = doc.getDocUrl();
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new RuntimeException("Путь к документу пуст");
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new RuntimeException("Файл не найден: " + filePath);
        }

        byte[] fileBytes = Files.readAllBytes(path);
        String fileName = extractFileName(filePath);
        System.out.println("=== СКАЧИВАНИЕ ДОКУМЕНТА ===");
        System.out.println("doc.id = " + id);
        System.out.println("doc.docUrl = " + filePath);
        System.out.println("Извлечённое имя файла: " + fileName);

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(fileName, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .contentLength(fileBytes.length)
                .body(fileBytes);
    }

    @GetMapping("/{docId}/edit-regulatoryDoc")
    public String showRegulatoryDocEditForm(@PathVariable Long docId,
                                            Model model) {
        Optional<RegulatoryDoc> regulatoryDocOpt = regulatoryDocService.getByDocId(docId);
        if (regulatoryDocOpt.isEmpty()) {
            model.addAttribute("error", "Документ не найден");
            return "admin/onboarding/references/regulatoryDocs";
        } else {
            model.addAttribute("regulatoryDoc", regulatoryDocOpt.get());
            return "admin/onboarding/references/edit-regulatoryDoc";
        }
    }

    @PostMapping("/{docId}/edit-regulatoryDoc")
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

    @GetMapping("/add-activity")
    public String showActivitiesAddForm(Model model) {
        model.addAttribute("activities", new Activity());
        return "admin/onboarding/references/add-activity";
    }

    @PostMapping("/add-activity")
    public String addActivity(@RequestParam String name) {
        Activity activity = new Activity();
        activity.setActivityName(name);
        activityService.save(activity);
        return "redirect:/admin/onboarding/references/activities";
    }

    @GetMapping("/{activityId}/edit-activity")
    public String showActivityEditForm(@PathVariable Long activityId,
                                       Model model) {
        Optional<Activity> activityOpt = activityService.getById(activityId);
        if (activityOpt.isEmpty()) {
            model.addAttribute("error", "Действие не найдено");
            return "admin/onboarding/references/activities";
        } else {
            model.addAttribute("activity", activityOpt.get());
            return "admin/onboarding/references/edit-activity";
        }
    }

    @PostMapping("/{activityId}/edit-activity")
    public String editActivity(@RequestParam Long activityId,
                               @RequestParam String name,
                               Model model) {
        Optional<Activity> activityOpt = activityService.getById(activityId);
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
        List<ActivityDetails> activityDetails = activityDetailsService.getAll();
        model.addAttribute("activities", activities);
        model.addAttribute("activityDetails", activityDetails);
        return "admin/onboarding/references/activityDetails";
    }

    @GetMapping("/add-activityDetails")
    public String showActivityDetailsAddForm(Model model) {
        List<Activity> activities = activityService.getAll();
        List<OnboardingRole> onboardingRoles = onboardingRoleService.getAll();
        model.addAttribute("activities", activities);
        model.addAttribute("onboardingRoles", onboardingRoles);
        return "admin/onboarding/references/add-activityDetails";
    }

    @PostMapping("/add-activityDetails")
    public String addActivityDetails(@RequestParam String name,
                                     @RequestParam Long activityId,
                                     @RequestParam Long OnboardingRoleId,
                                     Model model) {
        Optional<Activity> activityOpt = activityService.getById(activityId);
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
        List<Activity> activities = activityService.getAll();
        List<OnboardingRole> onboardingRoles = onboardingRoleService.getAll();

        Optional<Activity> activityOpt = activityService.getById(activityDetailsId);
        Optional<ActivityDetails> activityDetailsOpt = activityDetailsService.getById(activityDetailsId);
        Optional<OnboardingRole> onboardingRoleOpt = onboardingRoleService.getByRoleId(activityDetailsId);
        if (activityOpt.isEmpty()) {
            model.addAttribute("error", "Действие не найдено");
            return "admin/onboarding/references/activityDetails";
        } else if (activityDetailsOpt.isEmpty()) {
            model.addAttribute("error", "Расшифровка действия не найдена");
            return "admin/onboarding/references/activityDetails";
        } else if (onboardingRoleOpt.isEmpty()) {
            model.addAttribute("error", "Роль не найдена");
            return "admin/onboarding/references/activityDetails";
        } else {
            model.addAttribute("activities", activities);
            model.addAttribute("activityDetails", activityDetailsOpt.get());
            model.addAttribute("onboardingRoles", onboardingRoles);
            return "admin/onboarding/references/edit-activityDetails";
        }
    }

    @PostMapping("/{activityDetailsId}/edit-activityDetails")
    public String editActivityDetails(@RequestParam Long activityDetailsId,
                                      @RequestParam String name,
                                      @RequestParam Long activityId,
                                      @RequestParam Long OnboardingRoleId,
                                      Model model) {
        Optional<ActivityDetails> activityDetailsOpt = activityDetailsService.getById(activityDetailsId);
        Optional<Activity> activityOpt = activityService.getById(activityId);
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

    private String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) return "unknown.bin";

        // Обработка UNC-пути (\\server\share\folder\file.ext)
        // Ищем последний \ или /
        int lastBackslash = filePath.lastIndexOf('\\');
        int lastSlash = filePath.lastIndexOf('/');

        int lastSeparator = Math.max(lastBackslash, lastSlash);

        if (lastSeparator >= 0 && lastSeparator < filePath.length() - 1) {
            return filePath.substring(lastSeparator + 1);
        } else {
            return filePath;
        }
    }
}
