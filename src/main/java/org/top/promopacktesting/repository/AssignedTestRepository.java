package org.top.promopacktesting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.top.promopacktesting.model.AssignedTest.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.AssignedTest;
import org.top.promopacktesting.model.AssignedTest.TestStatus;
import org.top.promopacktesting.model.Test;
import org.top.promopacktesting.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignedTestRepository extends JpaRepository<AssignedTest, Long> {

    List<AssignedTest> findByUserId(Long userId);
    List<AssignedTest> findByUserIdAndStatus(Long userId, TestStatus status);
    List<AssignedTest> findByStatus(TestStatus status);
    List<AssignedTest> findByTestId(Long testId);
    List<AssignedTest> findByTestIdAndStatus(Long testId, TestStatus status);
    List<AssignedTest> findByUserIdAndTestIdAndStatus(Long userId, Long testId, TestStatus status);
    List<AssignedTest> findByTestNameAndUserNameAndStatus(String testName, String userName, AssignedTest.TestStatus status);
    Optional<AssignedTest> findByUserIdAndTestId(Long userId, Long testId);

    @Query("SELECT at FROM AssignedTest at WHERE at.user.id = :userId AND at.test.isActive = true")
    List<AssignedTest> findActiveAssignedTestsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(at) FROM AssignedTest at WHERE at.test.id = :testId AND at.status = 'COMPLETED'")
    Long countCompletedTestsByTestId(@Param("testId") Long tstId);
}
