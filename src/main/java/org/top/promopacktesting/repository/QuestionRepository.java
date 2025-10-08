package org.top.promopacktesting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.Question;
import org.top.promopacktesting.model.Test;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    //List<Question> findByTestId(Long id);
    List<Question> findByTestOrderByOrderNumAsc(Test test);
    List<Question> findByTestIdOrderByOrderNumAsc(Long testId);

    Optional<Question> findById(Long questionId);

    @Query("SELECT q FROM Question q WHERE q.test.id = :testId ORDER BY q.orderNum ASC")
    List<Question> findByTestId(@Param("testId") Long testId);

    Optional<Question> findByTestIdAndOrderNum(Long testId, Long orderNum);

    Question save(Question question);
}
