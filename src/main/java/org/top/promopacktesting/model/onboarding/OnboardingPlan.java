package org.top.promopacktesting.model.onboarding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.top.promopacktesting.model.AssignStatus;
import org.top.promopacktesting.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//Сущность документа План адаптации
@Entity
@Table(name = "onboarding_plan")
@NoArgsConstructor
@Getter
@Setter
public class OnboardingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private User employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name="assigned_at")
    private LocalDate assignedAt;

    @Enumerated(EnumType.STRING)
    private AssignStatus status = AssignStatus.ASSIGNED;

    @OneToMany(mappedBy = "onboardingPlan", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OnboardingStage> stages = new ArrayList<>();
}
