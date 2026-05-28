package org.top.promopacktesting.repository.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.test.ThemeTest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeTestRepository extends JpaRepository<ThemeTest, Long> {

    Optional<ThemeTest> findThemeTestById(Long id);
    List<ThemeTest> findAll();
    Optional<ThemeTest> findThemeTestByThemeName(String themeName);

}
