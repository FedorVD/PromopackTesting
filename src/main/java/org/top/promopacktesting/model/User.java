package org.top.promopacktesting.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="username", unique=true)
    private String username;

    @Column(name="password")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name="employee_id")
    private String employeeId;

    @Column(name="name")
    private String name;

    @Column(name="department")
    private String department;

    @Column(name="position")
    private String position;

    @Column(name="hire_date")
    private LocalDateTime hireDate;

    @Column(name="dismissal_date")
    private LocalDateTime dismissalDate;

    public User(String employeeId, String name, String department, String position, LocalDateTime hireDate) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
    }

    public User(String username, String password, Role role,
                String employeeId, String name, String department,
                String position, LocalDateTime hireDate) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.position = position;
        this.hireDate = hireDate;
    }

    public enum Role {
        ADMIN, USER
    }
}

