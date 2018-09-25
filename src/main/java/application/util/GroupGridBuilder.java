package application.util;

import application.entity.Group;
import com.vaadin.flow.component.grid.Grid;

public class GroupGridBuilder {

    public static Grid<Group> buildGrid(){
        Grid<Group> groupGrid = new Grid<>(Group.class);
        groupGrid.setHeightByRows(true);
        setUsualColumns(groupGrid);
        groupGrid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        return groupGrid;
    }

    private static void setUsualColumns(Grid<Group> groupGrid) {
        groupGrid.setColumns("id", "groupNumber", "facultyName");
    }
}
