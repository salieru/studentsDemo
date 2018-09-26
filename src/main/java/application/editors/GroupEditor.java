package application.editors;

import application.entity.Base;
import application.entity.Group;
import application.repository.GroupRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@SpringComponent
@UIScope
public class GroupEditor extends AbstractEditor {

    private TextField groupNumber = new TextField("Group number");
    private TextField facultyName = new TextField("Faculty name");
    private Label errorLabel = new Label();
    private String errorDeletionText = "cannot delete group with students";
    private String errorCreatingText = "cannot create group without number";

    private Binder<Group> binder = new Binder<>(Group.class);

    @Autowired
    public GroupEditor(GroupRepository repository) {
        super(repository);
        add(groupNumber, facultyName);
        setDialogDimensions("300px", "300px");
        bindFields();
    }

    @Override
    protected void bindFields() {
        binder.bind(facultyName, Group::getFacultyName, Group::setFacultyName);
        binder.forField(groupNumber)
                .withConverter(new StringToIntegerConverter("Must be Integer"))
                .bind(Group::getGroupNumber, Group::setGroupNumber);
    }

    @Override
    protected void wireBinder(Base entity) {
        binder.setBean((Group) entity);
    }

    @Override
    protected void setDialogFocusField() {
        groupNumber.focus();
    }

    @Override
    protected void addClickListenersToButtons(Dialog dialog, Button dialogSave, Button dialogCancel, Button dialogDelete) {
        dialog.add(errorLabel);
        dialogSave.addClickListener(listener -> {
            if (groupNumber.getValue()==null){
                errorLabel.setText(errorCreatingText);
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
            try {
                delete();
                errorLabel.setText("");
                dialog.close();
            } catch (DataIntegrityViolationException e){
                errorLabel.setText(errorDeletionText);
            }
        });
    }
}
