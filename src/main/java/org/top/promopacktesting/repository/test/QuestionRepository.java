package org.top.promopacktesting.repository.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.test.Question;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findById(Long questionId);

    @Query("SELECT q FROM Question q WHERE q.test.id = :testId ORDER BY q.orderNum ASC")
    List<Question> findByTestId(@Param("testId") Long testId);

    Optional<Question> findByTestIdAndOrderNum(Long testId, Long orderNum);

    Question save(Question question);
}
