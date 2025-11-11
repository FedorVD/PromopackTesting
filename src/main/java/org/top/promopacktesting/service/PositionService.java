package org.top.promopacktesting.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.Position;
import org.top.promopacktesting.repository.PositionRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    public List<Position> findAllPositions(){
        return positionRepository.findAll();
    }

    public Optional<Position> findByPositionId(Long positionId){
        return positionRepository.findById(positionId);
    }

    public Optional<Position> findByPositionName(String positionName){
        return positionRepository.findByPositionName(positionName);
    }
}
