package org.top.promopacktesting.repository.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.onboarding.OnboardingStage;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingStageRepository extends JpaRepository<OnboardingStage, Long> {

    Optional<OnboardingStage> findById(Long id);
    List<OnboardingStage> findByOnboardingPlanId(Long onboardingPlanId);

/*
    OnboardingStage create (OnboardingStage onboardingStage);
    OnboardingStage update(OnboardingStage onboardingStage);
    void delete(OnboardingStage onboardingStage);
    OnboardingStage save(OnboardingStage onboardingStage);
*/
}
