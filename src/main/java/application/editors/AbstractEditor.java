package application.editors;

import application.entity.Base;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

@SpringComponent
@UIScope
public abstract class AbstractEditor<T extends Base> extends VerticalLayout implements KeyNotifier {

    private JpaRepository<T, Long> repository;
    private T entity;
    private ChangeHandler changeHandler;
    private String dialogWidth = "300px";
    private String dialogHeight = "300px";

    @Autowired
    public AbstractEditor(JpaRepository<T, Long> repository) {
        this.repository = repository;

//        bindFields();
        setSpacing(true);
        addKeyPressListener(Key.ENTER, e -> save());
        setVisible(false);
    }

    protected abstract void bindFields();

    void delete() {
        repository.delete(entity);
        changeHandler.onChange();
    }

    void save() {
        repository.save(entity);
        changeHandler.onChange();
    }

    public void edit(T t) {
        if (t == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = t.getId() != null;
        if (persisted) {
            entity = repository.findById(t.getId()).get();
        }
        else {
            entity = t;
        }
        wireBinder(entity);
        openDialog(persisted);
    }

    protected abstract void wireBinder(T entity);

    protected void openDialog(boolean persisted){
        Dialog dialog = createDialog();
        HorizontalLayout actions = createDialogButtons(persisted, dialog);
        dialog.add(this);
        dialog.add(actions);
        dialog.open();
        setVisible(true);
        setDialogFocusField();
    }

    protected HorizontalLayout createDialogButtons(boolean persisted, Dialog dialog) {
        Button dialogSave = new Button("Save", VaadinIcon.CHECK.create());
        Button dialogCancel = new Button("Cancel");
        Button dialogDelete = new Button("Delete", VaadinIcon.TRASH.create());

        addClickListenersToButtons(dialog, dialogSave, dialogCancel, dialogDelete);

        dialogDelete.setVisible(persisted);
        dialogSave.getElement().getThemeList().add("primary");
        dialogDelete.getElement().getThemeList().add("error");

        return new HorizontalLayout(dialogSave, dialogCancel, dialogDelete);
    }

    protected void addClickListenersToButtons(Dialog dialog, Button dialogSave, Button dialogCancel, Button dialogDelete) {
        dialogSave.addClickListener(listener -> {
            save();
            dialog.close();
        });
        dialogCancel.addClickListener(listener -> dialog.close());
        dialogDelete.addClickListener(listener -> {
            delete();
            dialog.close();
        });
    }

    protected abstract void setDialogFocusField();

    public interface ChangeHandler {
        void onChange();
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    protected Dialog createDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setWidth(dialogWidth);
        dialog.setHeight(dialogHeight);
        return dialog;
    }

    protected void setDialogDimensions(String width, String height){
        dialogWidth = width;
        dialogHeight = height;
    }

}
