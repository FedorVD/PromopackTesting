package org.top.promopacktesting.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.top.promopacktesting.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByEmployeeId(String employeeId);
    Optional<User> findByUsername(String username);
    List<User> findByRole(User.Role role);
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findByDepartmentContainingIgnoreCase(String department);
    List<User> findByPositionContainingIgnoreCase(String position);
    List<User> findByDismissalDateIsNull();

    boolean existsByEmployeeId(String employeeId);
    boolean existsByUsername(String username);
    List<User> findByNameContainingIgnoreCaseAndDepartmentContainingIgnoreCaseAndPositionContainingIgnoreCase(String name, String department, String position);

    List<User> findByDepartmentContainingIgnoreCaseAndPositionContainingIgnoreCase(String department, String position);

    List<User> findByNameContainingIgnoreCaseAndDepartmentContainingIgnoreCase(String name, String department);

    List<User> findByNameContainingIgnoreCaseAndPositionContainingIgnoreCase(String name, String position);
}
