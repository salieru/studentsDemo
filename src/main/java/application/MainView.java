package application;

import application.editors.GroupEditor;
import application.editors.StudentEditor;
import application.entity.Group;
import application.entity.Student;
import application.repository.GroupRepository;
import application.repository.StudentRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Route
public class MainView extends VerticalLayout {

    private static final Logger log = LoggerFactory.getLogger(MainView.class);

	private final StudentRepository studentRepository;
	private final GroupRepository groupRepository;

	private final StudentEditor studentEditor;
	private final GroupEditor groupEditor;

	private final Grid<Student> studentGrid = new Grid<>(Student.class);
	private final Grid<Group> groupGrid = new Grid<>(Group.class);

	private final TextField filterByLastName = new TextField();
	private final ComboBox<Group> filterByGroupNumber = new ComboBox<>();

	private final Button addNewBtn = new Button("New student", VaadinIcon.PLUS.create());
	private final Button addNewGrp = new Button("New group", VaadinIcon.PLUS.create());
	private final ComboBox<String> mainMenu = new ComboBox<>();

	public MainView(StudentRepository studentRepository, GroupRepository groupRepository, StudentEditor studentEditor, GroupEditor groupEditor) {
		this.studentRepository = studentRepository;
		this.groupRepository = groupRepository;
		this.studentEditor = studentEditor;
		this.groupEditor = groupEditor;

        setFilters();
        initGrids();
        initButtons();
        setChangeHandlers();

        HorizontalLayout studentActions = new HorizontalLayout(filterByLastName, filterByGroupNumber, addNewBtn);
        HorizontalLayout groupActions = new HorizontalLayout(addNewGrp);

        setMainMenu(studentActions, groupActions);

        clear();
        add(studentActions, studentGrid, studentEditor, groupEditor);
        listStudents(null, null);
    }

    private void setMainMenu(HorizontalLayout studentActions, HorizontalLayout groupActions) {
        mainMenu.setPlaceholder("Menu");
        mainMenu.setItems("students", "groups");
        mainMenu.addValueChangeListener(event -> {
            if ("groups".equals(mainMenu.getValue())){
                clear();
                add(groupActions, groupGrid);
                listGroups();
            } else if ("students".equals(mainMenu.getValue())){
                clear();
                add(studentActions, studentGrid);
                Group group = filterByGroupNumber.getValue();
                listStudents(filterByLastName.getValue(), group==null?null:group.getGroupNumber());
                filterByGroupNumber.setItems(groupRepository.findAll());
            }
        });
    }

    private void setFilters() {
        filterByLastName.setPlaceholder("Filter by last name");
        filterByLastName.setValueChangeMode(ValueChangeMode.EAGER);
        filterByLastName.addValueChangeListener(e -> {
            Group group = filterByGroupNumber.getValue();
            listStudents(e.getValue(), group==null?null:group.getGroupNumber());
        });

        List<Group> groupList = groupRepository.findAll();
        filterByGroupNumber.setItemLabelGenerator(group -> group.getGroupNumber().toString());
        filterByGroupNumber.setItems(groupList);
        filterByGroupNumber.setPlaceholder("Filter by group");
        filterByGroupNumber.addValueChangeListener(e -> {
            Group group = e.getValue();
            listStudents(filterByLastName.getValue(), group==null?null:group.getGroupNumber());
        });
    }

    private void setChangeHandlers() {
        studentEditor.setChangeHandler(() -> {
            studentEditor.setVisible(false);
            Group group = filterByGroupNumber.getValue();
            listStudents(filterByLastName.getValue(), group==null?null:group.getGroupNumber());
        });

        groupEditor.setChangeHandler(() -> {
            groupEditor.setVisible(false);
            listGroups();
        });
    }

    private void initButtons() {
        addNewBtn.addClickListener(e -> {
            List<Group> groups = groupRepository.findAll();
            Student newStudent = new Student("", "", "", new Date(), groups.get(0));
            studentEditor.edit(newStudent);
        });
        addNewGrp.addClickListener(e -> groupEditor.edit(new Group(0,"")));
    }

    private void clear() {
        removeAll();
        add(mainMenu);
    }

    private void initGrids() {
        studentGrid.setHeight("300px");
        studentGrid.setColumns("id", "firstName", "lastName", "middleName");
        studentGrid.addColumn((Student student) -> {
            LocalDate localDate = convertToLocalDate(student.getBirthDate());
            return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        });
        studentGrid.addColumn("group.groupNumber");
        studentGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        groupGrid.setHeight("300px");
        groupGrid.setColumns("id", "groupNumber", "facultyName");
        groupGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);

        studentGrid.asSingleSelect().addValueChangeListener(e -> studentEditor.edit(e.getValue()));

        groupGrid.asSingleSelect().addValueChangeListener(e -> groupEditor.edit(e.getValue()));
    }

    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

	private void listStudents(String lastName, Integer groupNumber) {
		if (StringUtils.isEmpty(lastName)&&groupNumber==null) {
            studentGrid.setItems(studentRepository.findAll());
            return;
		}
		if (!StringUtils.isEmpty(lastName)&&groupNumber==null){
			studentGrid.setItems(studentRepository.findByLastNameStartsWithIgnoreCase(lastName));
			return;
		}
		if (StringUtils.isEmpty(lastName)&&groupNumber!=null){
            studentGrid.setItems(studentRepository.findByGroupNumber(groupNumber));
		    return;
        }
        studentGrid.setItems(studentRepository.findByFilters(lastName, groupNumber));
	}

    private void listGroups() {
        List<Group> allGroups = groupRepository.findAll();
        filterByGroupNumber.setItems(allGroups);
        groupGrid.setItems(allGroups);
    }

}
