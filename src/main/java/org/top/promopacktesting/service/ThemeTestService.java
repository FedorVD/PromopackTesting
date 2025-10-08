package org.top.promopacktesting.service;

import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.ThemeTest;
import org.top.promopacktesting.repository.ThemeTestRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ThemeTestService {

    private final ThemeTestRepository themeTestRepository;
    private ThemeTestRepository repository;

    public ThemeTestService(ThemeTestRepository themeTestRepository) {
        this.themeTestRepository = themeTestRepository;
    }

    public List<ThemeTest> getAllThemeTests() {
        return repository.findAll();
    }

    public Optional<ThemeTest> getThemeTestById(Long id) {
        return repository.findById(id);
    }

    public ThemeTest saveThemeTest(ThemeTest themeTest) {
        return repository.save(themeTest);
    }

    public void deleteThemeTestById(Long themeId) {
        themeTestRepository.deleteById(themeId);
    }

    public boolean existsThemeTestById(Long themeId) {
        return themeTestRepository.existsById(themeId);
    }
}
