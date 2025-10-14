package org.top.promopacktesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.Test;
import org.top.promopacktesting.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByIsActiveTrue();
    List<Test> findByIsActiveFalse();
    List<Test> findAll();
    List<Test> findByCreatedBy(User user);
    List<Test> findByNameContainingIgnoreCase(String name);
    Optional<Test> findById(Long id);


    @Query("SELECT t FROM Test t WHERE t.isActive = true AND t.id IN " +
            "(SELECT at.test.id FROM AssignedTest at WHERE at.user.id = :userId)")
    List<Test> findByAssignedUser(@Param("userId") Long userId);

    Test save(Test test);

    List<Test> findByThemeTestId(Long themeTestId);


}
