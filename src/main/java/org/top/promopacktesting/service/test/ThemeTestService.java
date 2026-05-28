package org.top.promopacktesting.service.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.test.ThemeTest;
import org.top.promopacktesting.repository.test.ThemeTestRepository;

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

    public void saveThemeTest(ThemeTest themeTest) {
        themeTestRepository.save(themeTest);
    }

    public void deleteThemeTestById(Long themeId) {
        themeTestRepository.deleteById(themeId);
    }

    public boolean existsThemeTestById(Long themeId) {
        return themeTestRepository.existsById(themeId);
    }
}
