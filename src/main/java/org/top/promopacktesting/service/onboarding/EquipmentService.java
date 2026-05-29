package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.onboarding.Equipment;
import org.top.promopacktesting.repository.onboarding.EquipmentRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    public Optional<Equipment> getById(Long id) {
        return equipmentRepository.findById(id);
    }

    public Optional<Equipment> getByName(String name) {
        return equipmentRepository.findByName(name);
    }

    public List<Equipment> getAll() {
        return equipmentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public void save(Equipment equipment) {
        equipmentRepository.save(equipment);
    }

/*
    public void updateEquipment(Long id, Equipment equipment) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(id);
        if (equipmentOpt.isPresent()) {
            Equipment existEquipment = equipmentOpt.get();
            existEquipment.setName(equipment.getName());
            save(existEquipment);
        }
    }
*/

/*
    public void deleteEquipmentById(Long id) {
        equipmentRepository.deleteEquipmentById(id);
    }
*/
}

