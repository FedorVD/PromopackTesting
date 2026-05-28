package org.top.promopacktesting.repository.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.onboarding.Equipment;
import org.top.promopacktesting.model.onboarding.OnboardingPlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingPlanRepository extends JpaRepository<OnboardingPlan, Long> {

    Optional<OnboardingPlan> findById(long id);
    List<OnboardingPlan> findByEquipment(Equipment equipment);
    List<OnboardingPlan> findAll();

/*
    OnboardingPlan create(OnboardingPlan onboardingPlan);
    OnboardingPlan update(OnboardingPlan onboardingPlan);

    void deleteById(Long id);
    OnboardingPlan save(OnboardingPlan onboardingPlan);
*/
}
