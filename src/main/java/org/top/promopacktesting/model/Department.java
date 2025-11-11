package org.top.promopacktesting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="department")
@NoArgsConstructor
@Getter
@Setter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final String dafaultName = "DEFAULT";

    @Column(name="department_name", nullable=false, unique = true)
    private String departmentName;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;


/*    public Department getDepartmentName() {
        this.departmentName = dafaultName;
        return this;
    }*/

    public Department (String departmentName) {
        this.departmentName = departmentName;
    }
}
