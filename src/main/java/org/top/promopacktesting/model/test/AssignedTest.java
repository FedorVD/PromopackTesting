package org.top.promopacktesting.model.test;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.top.promopacktesting.model.AssignStatus;
import org.top.promopacktesting.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assigned_tests")
@Getter
@Setter
public class AssignedTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name="test_score")
    private Double testScore = 0.0;

    @Enumerated(EnumType.STRING)
    private AssignStatus status = AssignStatus.ASSIGNED;

    @OneToMany(mappedBy = "assignedTest", cascade = CascadeType.ALL)
    private List<UserAnswer> userAnswers = new ArrayList<>();

    public AssignedTest() {
        this.assignedAt = LocalDateTime.now();
    }

    public AssignedTest(Test test, User user, User assignedBy) {
        this();
        this.test = test;
        this.user = user;
        this.assignedBy = assignedBy;
    }

/*    public enum TestStatus {
        ASSIGNED, IN_PROGRESS, COMPLETED
    }*/

}
