package org.top.promopacktesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.ThemeTest;

@Repository
public interface ThemeTestRepository extends JpaRepository<ThemeTest, Long> {
}
