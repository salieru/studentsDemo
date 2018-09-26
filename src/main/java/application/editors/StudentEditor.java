package application.editors;

import application.entity.Base;
import application.entity.Group;
import application.entity.Student;
import application.repository.GroupRepository;
import application.repository.StudentRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

@SpringComponent
@UIScope
public class StudentEditor extends AbstractEditor {

    private final GroupRepository groupRepository;

    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last name*");
    private TextField middleName = new TextField("Middle name");
    private DatePicker birthDate = new DatePicker("Birth date");
    private ComboBox<Group> groupSelector = new ComboBox<>("Group number*");

    private Label errorLabel = new Label();
    private String errorText = "please fill field marked with *";

    private Binder<Student> binder = new Binder<>(Student.class);

    @Autowired
    public StudentEditor(StudentRepository repository, GroupRepository groupRepository) {
        super(repository);
        this.groupRepository = groupRepository;

        add(firstName, lastName, middleName, birthDate, groupSelector);
        setDialogDimensions("320px", "540px");
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
    protected void wireBinder(Base entity) {
        List<Group> groupList = groupRepository.findAll();
        groupSelector.setItems(groupList);
        groupSelector.setItemLabelGenerator(group -> group.getGroupNumber().toString());
        binder.setBean((Student) entity);
    }

    @Override
    protected void setDialogFocusField() {
        firstName.focus();
    }

    @Override
    protected void addClickListenersToButtons(Dialog dialog, Button dialogSave, Button dialogCancel, Button dialogDelete) {
        dialog.add(errorLabel);
        dialogSave.addClickListener(listener -> {
            if (groupSelector.getValue()==null || lastName.getValue()==null || StringUtils.isEmpty(lastName.getValue())){
                errorLabel.setText(errorText);
            } else {
                errorLabel.setText("");
                save();
                dialog.close();
            }
        });
        dialogCancel.addClickListener(listener -> {
            errorLabel.setText("");
            dialog.close();
        });
        dialogDelete.addClickListener(listener -> {
            errorLabel.setText("");
            delete();
            dialog.close();
        });
    }
}
