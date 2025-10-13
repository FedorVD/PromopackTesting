package org.top.promopacktesting.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.ThemeTest;
import org.top.promopacktesting.service.ThemeTestService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/themes")
public class ThemeTestController {

    @Autowired
    private ThemeTestService themeTestService;

    @GetMapping("/themes")
    public String getAllThemeTests(@RequestParam(value = "search", required = false) String search,
                                   Model model) {
        List<ThemeTest> themeTests = themeTestService.getAllThemeTests();
        model.addAttribute("themeTests", themeTests);
        model.addAttribute("search", search);
        return "admin/themes/themes";
    }

    @GetMapping("/addTheme")
    public String showAddThemeTestForm(Model model) {
        model.addAttribute("themeTest", new ThemeTest());
        return "admin/themes/addTheme";
    }

    @PostMapping("/addTheme")
    public String addThemeTest(@RequestParam String themeName,
                               Model model) {
        try {
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
}
