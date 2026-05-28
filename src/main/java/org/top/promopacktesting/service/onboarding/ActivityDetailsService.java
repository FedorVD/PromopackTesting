package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.onboarding.Activity;
import org.top.promopacktesting.model.onboarding.ActivityDetails;
import org.top.promopacktesting.model.onboarding.OnboardingRole;
import org.top.promopacktesting.repository.onboarding.ActivityDetailsRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActivityDetailsService {

    private ActivityDetailsRepository activityDetailsRepository;

    public Optional<ActivityDetails> getActivityDetailsById(Long id) {
        return activityDetailsRepository.findById(id);
    }

    public Optional<ActivityDetails> getActivityDetailsByName(String name) {
        return activityDetailsRepository.findByActivityDetailsName(name);
    }

    public List<ActivityDetails> getAll() {
        return activityDetailsRepository.findAll();
    }

    public List<ActivityDetails> getAllActivityDetailsByOnboardingRole(OnboardingRole onboardingRole) {
        return activityDetailsRepository.findAllByOnboardingRole(onboardingRole);
    }

    public List<ActivityDetails> getAllActivityDetailsByActivity(Activity activity) {
        return activityDetailsRepository.findAllByActivity(activity);
    }

    public void save(ActivityDetails activityDetails) {
        activityDetailsRepository.save(activityDetails);
    }

    public void updateActivityDetails(Long id, ActivityDetails activityDetails) {
        Optional<ActivityDetails> existingActivityDetailsOpt = activityDetailsRepository.findById(id);
        if (existingActivityDetailsOpt.isPresent()) {
            ActivityDetails existingActivityDetails = existingActivityDetailsOpt.get();
            existingActivityDetails.setActivityDetailsName(activityDetails.getActivityDetailsName());
            existingActivityDetails.setActivity(activityDetails.getActivity());
            existingActivityDetails.setOnboardingRole(activityDetails.getOnboardingRole());
            activityDetailsRepository.save(existingActivityDetails);
        }
    }

/*
    public void deleteActivityDetailsById(Long id) {
        activityDetailsRepository.deleteActivityDetailsById(id);
    }
*/

    public List<ActivityDetails> findAll() {
        return activityDetailsRepository.findAll();
    }
}
