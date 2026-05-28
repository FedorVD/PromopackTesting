package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.onboarding.Equipment;
import org.top.promopacktesting.model.onboarding.OnboardingPlan;
import org.top.promopacktesting.model.onboarding.OnboardingRole;
import org.top.promopacktesting.repository.onboarding.OnboardingPlanRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OnboardingPlanService {

    @Autowired
    private OnboardingPlanRepository onboardingPlanRepository;

    public Optional<OnboardingPlan> findByOnboardingPlanId(Long id) {
        return onboardingPlanRepository.findById(id);
    }

    public List<OnboardingPlan> findAll() {
        return onboardingPlanRepository.findAll();
    }

    public List<OnboardingPlan> findByEquipment(Equipment equipment) {
        return onboardingPlanRepository.findByEquipment(equipment);
    }

    public OnboardingPlan save(OnboardingPlan onboardingPlan) {
        return onboardingPlanRepository.save(onboardingPlan);
    }

/*    public void updateOnboardingPlan(Long id, OnboardingPlan onboardingPlan) {
        Optional<OnboardingPlan> existingOnboardingPlanOpt = onboardingPlanRepository.findByOnboardingPlanId(id);
        if (existingOnboardingPlanOpt.isPresent()) {
            OnboardingPlan existingOnboardingPlan = existingOnboardingPlanOpt.get();
            existingOnboardingPlan.setEquipment(onboardingPlan.getEquipment());
            existingOnboardingPlan.setEmployee(onboardingPlan.getEmployee());
            onboardingPlanRepository.save(existingOnboardingPlan);
        }
    }

    public void deleteOnboardingPlan(Long id) {
        onboardingPlanRepository.deleteById(id);
    }*/
}
