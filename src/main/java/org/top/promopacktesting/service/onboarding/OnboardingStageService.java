package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.AssignStatus;
import org.top.promopacktesting.model.onboarding.OnboardingStage;
import org.top.promopacktesting.repository.onboarding.OnboardingStageRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OnboardingStageService {

    @Autowired
    private OnboardingStageRepository onboardingStageRepository;

    public Optional<OnboardingStage> findById(Long id) {
        return onboardingStageRepository.findById(id);
    }

    public List<OnboardingStage> findByOnboardingPlanId(Long onboardingPlanId) {
        return onboardingStageRepository.findByOnboardingPlanId(onboardingPlanId);
    }

    public List<OnboardingStage> getByOnboardingPlanId(Long onboardingPlanId) {
        return onboardingStageRepository.findByOnboardingPlanId(onboardingPlanId);
    }

    public OnboardingStage save(OnboardingStage stage) {
/*
        System.out.println("DEBUG: stage before save:");
        System.out.println("  activityId: " + (stage.getActivity() != null ? stage.getActivity().getId() : "null"));
        System.out.println("  activityDetailsId: " + (stage.getActivityDetails() != null ? stage.getActivityDetails().getId() : "null"));
        System.out.println("  regulatoryDocId: " + (stage.getRegulatoryDoc() != null ? stage.getRegulatoryDoc().getId() : "null"));
        System.out.println("  onboardingRoleId: " + (stage.getOnboardingRole() != null ? stage.getOnboardingRole().getId() : "null"));
        System.out.println("  mentorId: " + (stage.getMentor() != null ? stage.getMentor().getId() : "null"));
        System.out.println("  onboardingPlanId: " + (stage.getOnboardingPlan() != null ? stage.getOnboardingPlan().getId() : "null"));
        System.out.println("  shiftName: " + stage.getShiftName());
*/
        return onboardingStageRepository.save(stage);
    }

    public void update(Long id, OnboardingStage onboardingStage) {
        Optional<OnboardingStage> existingStageOpt = onboardingStageRepository.findById(id);
        if (existingStageOpt.isPresent()) {
            OnboardingStage existingStage = existingStageOpt.get();
            existingStage.setShiftName(onboardingStage.getShiftName());
            existingStage.setDeadline(onboardingStage.getDeadline());
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

    public void complete(Long stageId) {
        OnboardingStage onboardingStage = onboardingStageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Stage not found"));

        onboardingStage.setFinishedAt(LocalDate.now());
        onboardingStage.setStatus(AssignStatus.COMPLETED);
        onboardingStageRepository.save(onboardingStage);
    }

    public void delete(Long stageId) {
        onboardingStageRepository.deleteById(stageId);
    }
}
