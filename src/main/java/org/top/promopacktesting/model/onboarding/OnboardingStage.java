package org.top.promopacktesting.model.onboarding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.top.promopacktesting.model.AssignStatus;
import org.top.promopacktesting.model.User;

import javax.management.relation.Role;
import java.time.LocalDate;

//Сущность одной записи табличной части документа План адаптации
@Entity
@Table(name = "onboarding_stage")
@NoArgsConstructor
@Getter
@Setter
public class OnboardingStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Shift shiftName;

    @Column(name = "deadline")
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "onboarding_id", referencedColumnName = "id")
    private OnboardingPlan onboardingPlan;

    @Column(name = "onboarding_id", insertable = false, updatable = false)
    private Long onboardingPlanId;


    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_id", referencedColumnName = "id")
    private Activity activity;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "regulatory_doc_id", referencedColumnName = "id")
    private RegulatoryDoc regulatoryDoc;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_details_id", referencedColumnName = "id")
    private ActivityDetails activityDetails;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "onboardingRole_id", referencedColumnName = "id")
    private OnboardingRole onboardingRole;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "mentor_id", referencedColumnName = "id")
    private User mentor;

    @Column(name = "status")
    private AssignStatus status;

    @Column(name = "finished_at")
    private LocalDate finishedAt;

    public void setShift(Shift shift) {
        this.shiftName = shift;
    }
}
