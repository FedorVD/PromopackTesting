package org.top.promopacktesting.model;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="position")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private final String defaultPosition = "DEFAULT";

    @Column(name="position_name", nullable=false, unique = true)
    private String positionName;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

/*    public Position getPositionName() {
        this.positionName = defaultPosition;
        return this;
    }*/

    public Position (String positionName) {
        this.positionName = positionName;
    }
}
