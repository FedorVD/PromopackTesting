package org.top.promopacktesting.model.onboarding;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.top.promopacktesting.model.User;

//Сущночть записи справочника Роль в адаптации
@Entity
@Table(name = "onboarding_role")
@NoArgsConstructor
@Getter
@Setter
public class OnboardingRole {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String roleName;
}
