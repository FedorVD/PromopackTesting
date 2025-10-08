package org.top.promopacktesting.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.top.promopacktesting.model.ThemeTest;
import org.top.promopacktesting.service.ThemeTestService;

import java.util.Optional;

@Controller
@RequestMapping("/admin/themeTest")
public class ThemeTestController {

    @Autowired
    private ThemeTestService themeTestService;

    @GetMapping("/list")
    public String getAllThemeTests(Model model) {
        model.addAttribute("themeTests", themeTestService.getAllThemeTests());
        return "admin/themes/themes";
    }

    @GetMapping("/addTheme")
    public String showAddThemeTestForm(Model model) {
        model.addAttribute("themeTest", new ThemeTest());
        return "admin/themes/addTheme";
    }

    @PostMapping("/addTheme")
    public String addThemeTest(@ModelAttribute("themeTest") ThemeTest themeTest) {
        themeTestService.saveThemeTest(themeTest);
        return "redirect:/admin/themes/themes";
    }

    @GetMapping("/editTheme/{id}")
    public String showEditThemeTestForm(@PathVariable Long id, Model model) {
        Optional<ThemeTest> themeTest = themeTestService.getThemeTestById(id);
        if (themeTest.isEmpty()) {
            model.addAttribute("Ошибка", "Тема не найдена");
            return "redirect:/admin/themes/themes";
        }
        model.addAttribute("themeTest", themeTest.get());
        return "admin/themes/editTheme";
    }

    @PostMapping("/editTheme/{id}")
    public String editThemeTest(@PathVariable Long id,
                                @ModelAttribute("themeTest") ThemeTest updatedThemeTest) {
        updatedThemeTest.setId(id);
        themeTestService.saveThemeTest(updatedThemeTest);
        return "redirect:/admin/themes/themes";
    }
}
