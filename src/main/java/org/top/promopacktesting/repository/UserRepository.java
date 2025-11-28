package org.top.promopacktesting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"department", "position"})
    Optional<User> findByEmployeeId(String employeeId);
    Optional<User> findByUsername(String username);
    List<User> findByDismissalDateIsNull();

    // Поиск по ID подразделения и/или должности
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByPositionId(Long positionId);
    List<User> findByDepartmentIdAndPositionId(Long departmentId, Long positionId);


    List<User> findByRole(User.Role role);
    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByNameContainingIgnoreCaseAndDepartmentId(String name, Long departmentId);
    List<User> findByNameContainingIgnoreCaseAndPositionId(String name, Long positionId);
    List<User> findByNameContainingIgnoreCaseAndDepartmentIdAndPositionId(String name, Long departmentId, Long positionId);

    boolean existsByEmployeeId(String employeeId);
    boolean existsByUsername(String username);


    //List<User> findByNameContainingIgnoreCaseAndDepartmentContainingIgnoreCaseAndPositionContainingIgnoreCase(String name, String department, String position);

    //List<User> findByDepartmentContainingIgnoreCaseAndPositionContainingIgnoreCase(String department, String position);

    //List<User> findByNameContainingIgnoreCaseAndDepartmentContainingIgnoreCase(String name, String department);

    //List<User> findByNameContainingIgnoreCaseAndPositionContainingIgnoreCase(String name, String position);

    //List<User> findByDepartmentContainingIgnoreCase(String keyword);

    //List<User> findByPositionContainingIgnoreCase(String position);
}
