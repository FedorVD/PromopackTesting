package org.top.promopacktesting.repository.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.test.AssignedTest;
import org.top.promopacktesting.model.test.Question;
import org.top.promopacktesting.model.test.UserAnswer;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    List<UserAnswer> findByAssignedTest(AssignedTest assignedTest);
    List<UserAnswer> findByAssignedTestId(Long assignedTestId);
    List<UserAnswer> findByQuestion(Question question);

    Optional<UserAnswer> findByAssignedTestAndQuestion(AssignedTest assignedTest, Question question);
}
