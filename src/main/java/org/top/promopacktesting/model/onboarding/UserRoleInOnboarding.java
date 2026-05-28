package org.top.promopacktesting.model.onboarding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.top.promopacktesting.model.User;

@Entity
@Table(name = "user_role_in_onboarding")
@NoArgsConstructor
@Getter
@Setter
public class UserRoleInOnboarding {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_role_id", nullable = false)
    private OnboardingRole onboardingRole;
}
