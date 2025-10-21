package org.top.promopacktesting.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Setter
@Getter
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="theme_test")
    private ThemeTest themeTest;

    @Column(name = "passing_score", nullable = false)
    private Double passingScore;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<AssignedTest> assignedTests = new ArrayList<>();

    public Test() {
        this.createdAt = LocalDateTime.now();
    }

    public Test(String name, User createdBy, Double passingScore) {
        this();
        this.name = name;
        this.createdBy = createdBy;
        this.passingScore = passingScore;
    }
}