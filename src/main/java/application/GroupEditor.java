package application;

import application.entity.Group;
import application.repository.GroupRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@SpringComponent
@UIScope
public class GroupEditor extends VerticalLayout implements KeyNotifier {

    private final GroupRepository repository;
    private Group group;
    private TextField groupNumber = new TextField("Group number");
    private TextField facultyName = new TextField("Faculty name");

    private Binder<Group> binder = new Binder<>(Group.class);
    private ChangeHandler changeHandler;

    @Autowired
    public GroupEditor(GroupRepository repository) {
        this.repository = repository;

        add(groupNumber, facultyName);
        bindFields();
        setSpacing(true);
        addKeyPressListener(Key.ENTER, e -> save());
        setVisible(false);
    }

    private void bindFields() {
        binder.bind(facultyName, Group::getFacultyName, Group::setFacultyName);
        binder.forField(groupNumber)
                .withConverter(new StringToIntegerConverter("Must be Integer"))
                .bind(Group::getGroupNumber, Group::setGroupNumber);
    }

    private void delete() {
        repository.delete(group);
        changeHandler.onChange();
    }

    private void save() {
        repository.save(group);
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }

    final void editGroup(Group g) {
        if (g == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = g.getId() != null;
        if (persisted) {
            group = repository.findById(g.getId()).get();
        }
        else {
            group = g;
        }
        binder.setBean(group);

        openDialog(persisted);
    }

    private void openDialog(boolean persisted) {
        Dialog dialog = createDialog();
        HorizontalLayout actions = getDialogButtonsLayout(persisted, dialog);
        dialog.add(this);
        dialog.add(actions);
        dialog.open();
        setVisible(true);
        groupNumber.focus();
    }

    private HorizontalLayout getDialogButtonsLayout(boolean persisted, Dialog dialog) {
        Button dialogSave = new Button("Save", VaadinIcon.CHECK.create());
        Button dialogCancel = new Button("Cancel");
        Button dialogDelete = new Button("Delete", VaadinIcon.TRASH.create());

        addClickListenersToButtons(dialog, dialogSave, dialogCancel, dialogDelete);

        dialogDelete.setVisible(persisted);
        dialogSave.getElement().getThemeList().add("primary");
        dialogDelete.getElement().getThemeList().add("error");

        return new HorizontalLayout(dialogSave, dialogCancel, dialogDelete);
    }

    private void addClickListenersToButtons(Dialog dialog, Button dialogSave, Button dialogCancel, Button dialogDelete) {
        dialogSave.addClickListener(listener -> {
            save();
            dialog.close();
        });
        dialogCancel.addClickListener(listener -> {
            dialog.close();
        });
        dialogDelete.addClickListener(listener -> {
            try {
                delete();
                dialog.close();
            } catch (DataIntegrityViolationException e){
                dialog.add(new Label("cannot delete group with students"));
            }

        });
    }

    private Dialog createDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth("300px");
        dialog.setHeight("300px");
        return dialog;
    }

    void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

}
