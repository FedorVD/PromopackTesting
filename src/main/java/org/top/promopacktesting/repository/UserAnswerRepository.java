package org.top.promopacktesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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


}
