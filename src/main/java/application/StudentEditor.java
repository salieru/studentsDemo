package application;

import application.entity.Group;
import application.entity.Student;
import application.repository.GroupRepository;
import application.repository.StudentRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SpringComponent
@UIScope
public class StudentEditor extends VerticalLayout implements KeyNotifier {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

	private final StudentRepository repository;
	private final GroupRepository groupRepository;

	private Student student;

	private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last name");
    private TextField middleName = new TextField("Middle name");
    private DatePicker birthDate = new DatePicker("Birth date");
    private ComboBox<Group> groupSelector = new ComboBox<>("Group number");

	private Binder<Student> binder = new Binder<>(Student.class);
	private ChangeHandler changeHandler;

	@Autowired
	public StudentEditor(StudentRepository repository, GroupRepository groupRepository) {
		this.repository = repository;
		this.groupRepository = groupRepository;

        add(firstName, lastName, middleName, birthDate, groupSelector);
        bindFields();
        setSpacing(true);
		addKeyPressListener(Key.ENTER, e -> save());
		setVisible(false);
	}

    private void bindFields() {
        binder.bind(firstName, "firstName");
        binder.bind(lastName, "lastName");
        binder.bind(middleName, "middleName");
        binder.forField(birthDate)
                .withConverter(new LocalDateToDateConverter())
                .bind("birthDate");
        binder.bind(groupSelector, "group");
    }

    private void delete() {
		repository.delete(student);
		changeHandler.onChange();
	}

	private void save() {
		repository.save(student);
		changeHandler.onChange();
	}

	public interface ChangeHandler {
		void onChange();
	}

	final void editStudent(Student s) {
		if (s == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = s.getId() != null;
		if (persisted) {
			student = repository.findById(s.getId()).get();
		}
		else {
			student = s;
		}

        List<Group> groupList = groupRepository.findAll();
        groupSelector.setItems(groupList);
        groupSelector.setItemLabelGenerator(group -> group.getGroupNumber().toString());
        binder.setBean(student);
        openDialog(persisted);
    }

    private void openDialog(boolean persisted) {
        Dialog dialog = createDialog();
        HorizontalLayout actions = createDialogButtons(persisted, dialog);
        dialog.add(this);
        dialog.add(actions);
        dialog.open();
        setVisible(true);
        firstName.focus();
    }

    private Dialog createDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("340px");
        dialog.setHeight("540px");
        return dialog;
    }

    private HorizontalLayout createDialogButtons(boolean persisted, Dialog dialog) {
        Button dialogSave = new Button("Save", VaadinIcon.CHECK.create());
        Button dialogCancel = new Button("Cancel");
        Button dialogDelete = new Button("Delete", VaadinIcon.TRASH.create());

        dialogSave.addClickListener(listener -> {
            save();
            dialog.close();
        });
        dialogCancel.addClickListener(listener -> {
            dialog.close();
        });
        dialogDelete.addClickListener(listener -> {
            delete();
            dialog.close();
        });

        dialogDelete.setVisible(persisted);
        dialogSave.getElement().getThemeList().add("primary");
        dialogDelete.getElement().getThemeList().add("error");

        return new HorizontalLayout(dialogSave, dialogCancel, dialogDelete);
    }

    void setChangeHandler(ChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}

}
