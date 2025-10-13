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
    List<AssignedTest> findByUserIdAndTestId(Long userId, Long testId);

    @Query("SELECT at FROM AssignedTest at WHERE at.user.id = :userId AND at.test.isActive = true")
    List<AssignedTest> findActiveAssignedTestsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(at) FROM AssignedTest at WHERE at.test.id = :testId AND at.status = 'COMPLETED'")
    Long countCompletedTestsByTestId(@Param("testId") Long tstId);

    @Query("SELECT a FROM AssignedTest a WHERE a.status = 'COMPLETED' AND a.testScore <= :score")
    List<AssignedTest> findCompletedByScoreLessThan(@Param("score") Double score);

    List<AssignedTest> findByTestName(String testName);

    List<AssignedTest> findByUserName(String userName);

    List<AssignedTest> findByTestNameAndStatus(String testName, TestStatus status);

    List<AssignedTest> findByUserNameAndStatus(String userName, TestStatus status);

    AssignedTest findByTestNameAndUserName(String testName, String userName);
}
