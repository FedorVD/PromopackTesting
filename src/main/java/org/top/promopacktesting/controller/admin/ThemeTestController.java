package org.top.promopacktesting.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.ThemeTest;
import org.top.promopacktesting.service.ThemeTestService;
import org.top.promopacktesting.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/admin/themes")
public class ThemeTestController {

    @Autowired
    private ThemeTestService themeTestService;

    @Autowired
    private UserService userService;

    @GetMapping("/themes")
    public String getAllThemeTests(@RequestParam(value = "search", required = false) String search,
                                   Model model) {
        List<ThemeTest> themeTests = themeTestService.getAllThemeTests();
        sendCurrentUsername(model);
        model.addAttribute("themeTests", themeTests);
        model.addAttribute("search", search);
        return "admin/themes/themes";
    }

    @GetMapping("/addTheme")
    public String showAddThemeTestForm(Model model) {
        sendCurrentUsername(model);
        model.addAttribute("themeTest", new ThemeTest());
        return "admin/themes/addTheme";
    }

    @PostMapping("/addTheme")
    public String addThemeTest(@RequestParam String themeName,
                               Model model) {
        try {
            sendCurrentUsername(model);
            ThemeTest themeTest = new ThemeTest(themeName);
            themeTestService.saveThemeTest(themeTest);
            model.addAttribute("themeTest", themeTest);
            return "redirect:/admin/themes/themes";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при добавлении темы тестирования " + e.getMessage());
            return "admin/themes/addTheme";
        }
    }

    @GetMapping("/{id}/editTheme")
    public String showEditThemeTestForm(@PathVariable Long id, Model model) {
        Optional<ThemeTest> themeTest = themeTestService.getThemeTestById(id);
        sendCurrentUsername(model);
        if (themeTest.isEmpty()) {
            model.addAttribute("Ошибка", "Тема не найдена");
            return "redirect:/admin/themes/themes";
        }
        model.addAttribute("themeTest", themeTest.get());
        return "admin/themes/editTheme";
    }

    @PostMapping("/{id}/editTheme")
    public String editThemeTest(@PathVariable Long id,
                                @RequestParam String themeName,
                                Model model) {
        sendCurrentUsername(model);
        Optional<ThemeTest> themeTestOpt = themeTestService.getThemeTestById(id);
        if (themeTestOpt.isEmpty()) {
            model.addAttribute("error", "Тема не найдена");
            return "redirect:/admin/themes/themes";
        }
        ThemeTest themeTest = themeTestOpt.get();
        themeTest.setThemeName(themeName);
        themeTestService.saveThemeTest(themeTest);
        model.addAttribute("message", "Тема успешно изменена");
        return "redirect:/admin/themes/themes";
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
