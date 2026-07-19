package org.top.promopacktesting.model.onboarding;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Сущность записи справочника Расшифровка действия плана адаптации
@Entity
@Table(name = "activity_details")
@NoArgsConstructor
@Getter
@Setter
public class ActivityDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_details_name", nullable = false, length = 1024)
    private String activityDetailsName;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "onboarding_role_id", nullable = false, referencedColumnName = "id")
    private OnboardingRole onboardingRole;
}
