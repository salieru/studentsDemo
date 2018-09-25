package application.editors;

import application.entity.Base;
import application.entity.Group;
import application.entity.Student;
import application.repository.GroupRepository;
import application.repository.StudentRepository;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringComponent
@UIScope
public class StudentEditor extends AbstractEditor {

	private final GroupRepository groupRepository;

	private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last name");
    private TextField middleName = new TextField("Middle name");
    private DatePicker birthDate = new DatePicker("Birth date");
    private ComboBox<Group> groupSelector = new ComboBox<>("Group number");

	private Binder<Student> binder = new Binder<>(Student.class);

	@Autowired
	public StudentEditor(StudentRepository repository, GroupRepository groupRepository) {
	    super(repository);
		this.groupRepository = groupRepository;

        add(firstName, lastName, middleName, birthDate, groupSelector);
        setDialogDimensions("350px", "540px");
        bindFields();
	}

    @Override
    protected void bindFields() {
        binder.bind(firstName, "firstName");
        binder.bind(lastName, "lastName");
        binder.bind(middleName, "middleName");
        binder.forField(birthDate)
                .withConverter(new LocalDateToDateConverter())
                .bind("birthDate");
        binder.bind(groupSelector, "group");
    }

    @Override
    public void edit(Base base) {
        List<Group> groupList = groupRepository.findAll();
        groupSelector.setItems(groupList);
        groupSelector.setItemLabelGenerator(group -> group.getGroupNumber().toString());
        super.edit(base);
    }

    @Override
    protected void wireBinder(Base entity) {
        binder.setBean((Student) entity);
    }

    @Override
    protected void setDialogFocusField() {
        firstName.focus();
    }
}
