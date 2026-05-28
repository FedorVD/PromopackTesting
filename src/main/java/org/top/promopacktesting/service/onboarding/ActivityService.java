package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.onboarding.Activity;
import org.top.promopacktesting.repository.onboarding.ActivityRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public Optional<Activity> getByActivityId(Long id) {
        return activityRepository.findById(id);
    }
    public Optional<Activity> getByActivityName(String name) {
        return activityRepository.findByActivityName(name);
    }
    public List<Activity> getAll() {
        return activityRepository.findAll();
    }

    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }

/*    public void updateActivity(Long id, Activity activity) {
        Optional<Activity> existingActivityOpt = activityRepository.findById(id);
        if (existingActivityOpt.isPresent()) {
            Activity existingActivity = existingActivityOpt.get();
            existingActivity.setActivityName(activity.getActivityName());
            activityRepository.save(existingActivity);
        }
    }*/

/*
    public void deleteActivityById(Long id) {
        activityRepository.deleteActivityById(id);
    }
*/
}
