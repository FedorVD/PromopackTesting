package org.top.promopacktesting.repository.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.onboarding.OnboardingRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingRoleRepository extends JpaRepository<OnboardingRole, Long> {

    Optional<OnboardingRole> findById(Long id);
    Optional<OnboardingRole> findByRoleName(String rileName);
    List<OnboardingRole> findAll();

/*
    OnboardingRole create(OnboardingRole onboardingRole);
    OnboardingRole update(OnboardingRole onboardingRole);
    void deleteById(Long id);
    OnboardingRole save(OnboardingRole onboardingRole);
*/
}
