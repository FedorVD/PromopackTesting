package org.top.promopacktesting.repository.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.onboarding.OnboardingRole;
import org.top.promopacktesting.model.onboarding.UserRoleInOnboarding;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleInOnboardingRepository extends JpaRepository<UserRoleInOnboarding, Long> {

    Optional<UserRoleInOnboarding> findById(Long id);
    List<UserRoleInOnboarding> findAll();
    List<UserRoleInOnboarding> findByOnboardingRoleId(Long roleId);
    List<UserRoleInOnboarding> findByUserId(Long userId);
}
