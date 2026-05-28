package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.onboarding.OnboardingRole;
import org.top.promopacktesting.model.onboarding.OnboardingStage;
import org.top.promopacktesting.repository.onboarding.OnboardingRoleRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OnboardingRoleService {

    @Autowired
    private OnboardingRoleRepository onboardingRoleRepository;

    public Optional<OnboardingRole> getByRoleId(Long id) {
        return onboardingRoleRepository.findById(id);
    }

    public Optional<OnboardingRole> getByRoleName(String roleName) {
        return onboardingRoleRepository.findByRoleName(roleName);
    }

    public List<OnboardingRole> getAll() {
        return onboardingRoleRepository.findAll();
    }

    public OnboardingRole save(OnboardingRole onboardingRole) {
        return onboardingRoleRepository.save(onboardingRole);
    }

    public void update(Long id, OnboardingRole onboardingRole) {
        Optional<OnboardingRole> onboardingRoleOpt = onboardingRoleRepository.findById(id);
        if (onboardingRoleOpt.isPresent()) {
            OnboardingRole existingOnboardingRole = onboardingRoleOpt.get();
            existingOnboardingRole.setRoleName(onboardingRole.getRoleName());
            onboardingRoleRepository.save(existingOnboardingRole);
        }
    }

    public void deleteRoleById(Long id) {
        onboardingRoleRepository.deleteById(id);
    }
}
