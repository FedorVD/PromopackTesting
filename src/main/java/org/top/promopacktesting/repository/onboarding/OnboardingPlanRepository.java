package org.top.promopacktesting.repository.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.AssignStatus;
import org.top.promopacktesting.model.onboarding.Equipment;
import org.top.promopacktesting.model.onboarding.OnboardingPlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingPlanRepository extends JpaRepository<OnboardingPlan, Long> {

    Optional<OnboardingPlan> findById(long id);
    List<OnboardingPlan> findByEquipment(Equipment equipment);
    List<OnboardingPlan> findAll();
    List<OnboardingPlan> findAllByStatusNot(AssignStatus status);

    @Query("SELECT DISTINCT p FROM OnboardingPlan p LEFT JOIN FETCH p.stages WHERE p.id = :id")
    Optional<OnboardingPlan> findByIdWithStages(@Param("id") Long id);

/*
    OnboardingPlan create(OnboardingPlan onboardingPlan);
    OnboardingPlan update(OnboardingPlan onboardingPlan);

    void deleteById(Long id);
    OnboardingPlan save(OnboardingPlan onboardingPlan);
*/
}
