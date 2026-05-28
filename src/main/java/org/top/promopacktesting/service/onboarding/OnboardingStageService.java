package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.onboarding.OnboardingStage;
import org.top.promopacktesting.repository.onboarding.OnboardingStageRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OnboardingStageService {

    @Autowired
    private OnboardingStageRepository onboardingStageRepository;

    public Optional<OnboardingStage> findByOnboardingStageId(Long id) {
        return onboardingStageRepository.findById(id);
    }

    public List<OnboardingStage> findByOnboardingPlanId(Long onboardingPlanId) {
        return onboardingStageRepository.findByOnboardingPlanId(onboardingPlanId);
    }

    public OnboardingStage save(OnboardingStage onboardingStage) {
        return onboardingStageRepository.save(onboardingStage);
    }

    public void update(Long id, OnboardingStage onboardingStage) {
        Optional<OnboardingStage> existingStageOpt = onboardingStageRepository.findById(id);
        if (existingStageOpt.isPresent()) {
            OnboardingStage existingStage = existingStageOpt.get();
            existingStage.setShiftName(onboardingStage.getShiftName());
            existingStage.setStartTime(onboardingStage.getStartTime());
            existingStage.setEndTime(onboardingStage.getEndTime());
            existingStage.setActivity(onboardingStage.getActivity());
            existingStage.setRegulatoryDoc(onboardingStage.getRegulatoryDoc());
            existingStage.setActivityDetails(onboardingStage.getActivityDetails());
            existingStage.setMentor(onboardingStage.getMentor());
            existingStage.setStatus(onboardingStage.getStatus());
            if (onboardingStage.getFinishedAt() != null) {
                existingStage.setFinishedAt(onboardingStage.getFinishedAt());
            }
            onboardingStageRepository.save(existingStage);
        }
    }
}
