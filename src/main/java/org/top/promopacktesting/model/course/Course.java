package org.top.promopacktesting.model.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ui.context.Theme;
import org.top.promopacktesting.model.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "courses")
@Setter
@Getter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name")
    private String courseName;

    @Column (name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_course_id")
    private ThemeCourse themeCourse;

    @Column(name = "course_duration")
    private Long courseDuration;

    @Column(name = "course_path")
    private String coursePath;

    //public static final long MAX_COURSE_SIZE = 5 * 1024 * 1024; // 5 MB
}
