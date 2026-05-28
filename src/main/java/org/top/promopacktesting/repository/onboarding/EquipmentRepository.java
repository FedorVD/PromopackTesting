package org.top.promopacktesting.repository.onboarding;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.onboarding.Equipment;
import org.top.promopacktesting.model.onboarding.OnboardingPlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    public Optional<Equipment> findById(Long id);
    public Optional<Equipment> findByName(String name);

    List<Equipment> findAll();

/*    Equipment createEquipment(Equipment equipment);
    Equipment updateEquipment(Equipment equipment);*/
    //void deleteEquipmentById(Long id);

    //void save(Equipment equipment);
}
