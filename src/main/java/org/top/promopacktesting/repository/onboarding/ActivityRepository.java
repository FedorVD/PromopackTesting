package org.top.promopacktesting.repository.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.onboarding.Activity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Optional<Activity> findById(Long id);
    Optional<Activity> findByActivityName(String activityName);

    List<Activity> findAll();

    //Activity save(Activity activity);
    //void deleteActivityById(Long id);
}
