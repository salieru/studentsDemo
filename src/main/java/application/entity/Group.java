package application.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "GROUPZ")
public class Group extends Base implements Serializable {

    @Column(name = "group_number")
    private Integer groupNumber;

    @Column(name = "faculty_name")
    private String facultyName;

    @OneToMany(mappedBy = "group")
    private Set<Student> students = new HashSet<>();

    protected Group(){}

    public Group(Integer groupNumber, String facultyName) {
        this.groupNumber = groupNumber;
        this.facultyName = facultyName;
    }


    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students){
        this.students = students;
    }

    public Integer getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (!getId().equals(group.getId())) return false;
        if (!groupNumber.equals(group.groupNumber)) return false;
        return facultyName.equals(group.facultyName);
    }
}
