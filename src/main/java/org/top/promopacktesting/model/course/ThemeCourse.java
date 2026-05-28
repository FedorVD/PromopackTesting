package org.top.promopacktesting.model.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theme_course")
@NoArgsConstructor
@Getter
@Setter
public class ThemeCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "theme_course_name")
    private String themeCourseName;

    @OneToMany(mappedBy = "themeCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    public ThemeCourse(String themeCourseName) {
        this.themeCourseName = themeCourseName;
    }
}
