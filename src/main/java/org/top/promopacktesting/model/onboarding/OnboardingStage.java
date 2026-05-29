package org.top.promopacktesting.model.onboarding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.top.promopacktesting.model.AssignStatus;
import org.top.promopacktesting.model.User;

import java.time.LocalDateTime;

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

    @Column(name = "shift", nullable = false)
    private Shift shiftName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "onboarding_id", referencedColumnName = "id")
    private OnboardingPlan onboardingPlan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id", referencedColumnName = "id")
    private Activity activity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "regulatory_doc_id", referencedColumnName = "id")
    private RegulatoryDoc regulatoryDoc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_details_id", referencedColumnName = "id")
    private ActivityDetails activityDetails;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @Column(name = "status")
    private AssignStatus status;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

}
