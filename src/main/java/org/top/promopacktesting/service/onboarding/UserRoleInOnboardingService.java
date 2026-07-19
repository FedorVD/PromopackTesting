package org.top.promopacktesting.service.onboarding;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.top.promopacktesting.model.onboarding.UserRoleInOnboarding;
import org.top.promopacktesting.repository.onboarding.UserRoleInOnboardingRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserRoleInOnboardingService {

    @Autowired
    private UserRoleInOnboardingRepository userRoleInOnboardingRepository;

    public Optional<UserRoleInOnboarding> getById(Long id) {
        return userRoleInOnboardingRepository.findById(id);
    }

    public List<UserRoleInOnboarding> getAll() {
        return userRoleInOnboardingRepository.findAll();
    }

    public List<UserRoleInOnboarding> findByOnboardingRoleId(Long roleId) {
        return userRoleInOnboardingRepository.findByOnboardingRoleId(roleId);
    }

    public List<UserRoleInOnboarding> findByUserId(Long userId) {
        return userRoleInOnboardingRepository.findByUserId(userId);
    }

    public UserRoleInOnboarding save(UserRoleInOnboarding userRoleInOnboarding) {
        return userRoleInOnboardingRepository.save(userRoleInOnboarding);
    }

    public void deleteById(Long id) {
        userRoleInOnboardingRepository.deleteById(id);
    }

    public void update(Long id, UserRoleInOnboarding userRoleInOnboarding) {
        Optional<UserRoleInOnboarding> optional = userRoleInOnboardingRepository.findById(id);
        if (optional.isPresent()) {
            UserRoleInOnboarding userRole = optional.get();
            userRole.setOnboardingRole(userRoleInOnboarding.getOnboardingRole());
            userRole.setUser(userRoleInOnboarding.getUser());
            userRoleInOnboardingRepository.save(userRole);
        }
    }

}
