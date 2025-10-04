package org.top.promopacktesting.service;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.Answer;
import org.top.promopacktesting.model.Question;
import org.top.promopacktesting.repository.AnswerRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    public void save(Answer answer) {
        answerRepository.save(answer);
    }

    public void saveAll(List<Answer> answers) {
        answerRepository.saveAll(answers);
    }

    public List<Answer>getAnswersByQuestionId(Long questionId){
        return answerRepository.findByQuestionId(questionId);
    }

    public Optional<Answer> getAnswerById(Long answerId){
        return answerRepository.findById(answerId);
    }

    public void delete(Answer answer){
        answerRepository.delete(answer);
    }

    public void deleteByQuestionId(Long questionId){

    }

    public void addAnswersToQuestion(Question newQuestion, List<Answer> answer){
    }
}
