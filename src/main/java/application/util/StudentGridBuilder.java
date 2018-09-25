package application.util;

import application.entity.Student;
import com.vaadin.flow.component.grid.Grid;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class StudentGridBuilder {

    public static Grid<Student> buildGrid(){
        Grid<Student> studentGrid = new Grid<>(Student.class);
        studentGrid.setHeightByRows(true);
        setUsualColumns(studentGrid);
        setCustomColumns(studentGrid);
        studentGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        return studentGrid;
    }

    private static void setUsualColumns(Grid<Student> studentGrid) {
        studentGrid.setColumns("id", "firstName", "lastName", "middleName");
    }

    private static void setCustomColumns(Grid<Student> studentGrid) {
        studentGrid.addColumn((Student student) -> {
            LocalDate localDate = convertToLocalDate(student.getBirthDate());
            return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        });
        studentGrid.addColumn("group.groupNumber");
    }

    private static LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
