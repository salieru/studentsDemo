package application.repository;

import application.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByLastNameStartsWithIgnoreCase(String lastName);

    @Query("SELECT s FROM Student s WHERE lower(s.lastName) like CONCAT(lower(:lastName),'%') AND s.group.groupNumber = :groupNumber")
    List<Student> findByFilters(@Param("lastName") String lastName, @Param("groupNumber") Integer groupNumber);

    @Query("SELECT s FROM Student s WHERE s.group.groupNumber = :groupNumber")
    List<Student> findByGroupNumber(@Param("groupNumber") Integer groupNumber);
}
