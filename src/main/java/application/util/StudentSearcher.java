package application.util;

import application.entity.Student;
import application.repository.StudentRepository;
import com.vaadin.flow.component.grid.Grid;
import org.springframework.util.StringUtils;

import java.util.List;

public class StudentSearcher {
    public static List<Student> listStudents(StudentRepository studentRepository, String lastName, Integer groupNumber){
        if (StringUtils.isEmpty(lastName)&&groupNumber==null) {
            return studentRepository.findAll();
        }
        if (!StringUtils.isEmpty(lastName)&&groupNumber==null){
            return studentRepository.findByLastNameStartsWithIgnoreCase(lastName);
        }
        if (StringUtils.isEmpty(lastName)&&groupNumber!=null){
            return studentRepository.findByGroupNumber(groupNumber);
        }
        return studentRepository.findByFilters(lastName, groupNumber);
    }
}
