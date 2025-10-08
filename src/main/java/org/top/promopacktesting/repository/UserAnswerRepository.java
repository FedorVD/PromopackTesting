package org.top.promopacktesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.AssignedTest;
import org.top.promopacktesting.model.Question;
import org.top.promopacktesting.model.UserAnswer;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    List<UserAnswer> findByAssignedTest(AssignedTest assignedTest);
    List<UserAnswer> findByAssignedTestId(Long assignedTestId);

    Optional<UserAnswer> findByAssignedTestAndQuestion(AssignedTest assignedTest, Question question);

   /* @Query("SELECT ua FROM UserAnswer ua WHERE ua.assignedTest.id = :assignedTestId " +
            "AND ua.question.id = :questionId")
    List<UserAnswer> findByAssignedTestAndQuestion(@Param("assignedTestId") Long assignedTestId,
                                                   @Param("questionId") Long questionId);

    @Query("SELECT ua, SUM(ua.answerScore) FROM UserAnswer ua WHERE ua.assignedTest.id = :assignedTestId")
    Double sumAnswerScoreByAssignedTestId(@Param("assignedTestId") Long assignedTestId);*/
}
