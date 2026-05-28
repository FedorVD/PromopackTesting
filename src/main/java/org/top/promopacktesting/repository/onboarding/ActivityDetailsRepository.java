package org.top.promopacktesting.repository.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.onboarding.Activity;
import org.top.promopacktesting.model.onboarding.ActivityDetails;
import org.top.promopacktesting.model.onboarding.OnboardingRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityDetailsRepository extends JpaRepository<ActivityDetails, Long> {

    Optional<ActivityDetails> findById(Long id);
    Optional<ActivityDetails> findByActivityDetailsName(String activityName);
    List<ActivityDetails> findAll();
    List<ActivityDetails> findAllByOnboardingRole(OnboardingRole onboardingRole);
    List<ActivityDetails> findAllByActivity(Activity activity);

/*
    ActivityDetails createActivityDetails(ActivityDetails activityDetails);
    ActivityDetails updateActivityDetails(ActivityDetails activityDetails);
*/
    /*void deleteActivityDetailsById(Long id);

    void save(ActivityDetails activityDetails);*/
}
