package org.top.promopacktesting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.Test;
import org.top.promopacktesting.model.ThemeTest;
import org.top.promopacktesting.repository.ThemeTestRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ThemeTestService {

    @Autowired
    private ThemeTestRepository themeTestRepository;

    public List<ThemeTest> getAllThemeTests() {
        return themeTestRepository.findAll();
    }

    public Optional<ThemeTest> getThemeTestById(Long id) {
        return themeTestRepository.findById(id);
    }

/*
    public Optional <ThemeTest> getThemeTestByTest(Test test) {
        return themeTestRepository.findByTest(test);
    }
*/

    public ThemeTest saveThemeTest(ThemeTest themeTest) {
        return themeTestRepository.save(themeTest);
    }

    public void deleteThemeTestById(Long themeId) {
        themeTestRepository.deleteById(themeId);
    }

    public boolean existsThemeTestById(Long themeId) {
        return themeTestRepository.existsById(themeId);
    }
}
