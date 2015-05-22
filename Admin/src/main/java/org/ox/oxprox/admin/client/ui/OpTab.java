package org.ox.oxprox.admin.client.ui;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import org.ox.oxprox.admin.client.Properties;
import org.ox.oxprox.admin.shared.OP;

import java.util.ArrayList;
import java.util.List;

import static org.ox.oxprox.admin.client.gin.AppGinjector.injector;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class OpTab extends SimpleContainer {

    private final TextButton addButton = new TextButton("Add");
    private final TextButton editButton = new TextButton("Edit");
    private final TextButton removeButton = new TextButton("Remove");

    private final ColumnConfig nameColumn = new ColumnConfig(Properties.OP.displayName(), 200, "Name");
    private final OpDetailsPanel opDetailsPanel = injector.getOpDetailsPanel();
    private ListStore<OP> store = new ListStore<OP>(Properties.OP.dn());
    private Grid<OP> grid = new Grid<OP>(store, createColumnModel());
    private GridSelectionModel<OP> sm= new GridSelectionModel<OP>();

    public OpTab() {
        init();
        setWidget(createContent());
        setButtonsState();
    }

    private void init() {
        grid.setSelectionModel(sm);
        grid.getView().setAutoExpandColumn(nameColumn);
        grid.setStateful(false);

        sm.setSelectionMode(Style.SelectionMode.SINGLE);
        sm.addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<OP>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<OP> event) {
                opDetailsPanel.show(sm.getSelectedItem());
            }
        });

        injector.getService().getOpList(new AsyncCallback<List<OP>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<OP> opList) {
                store.clear();
                store.addAll(opList);
                store.commitChanges();
            }
        });
    }

    private void setButtonsState() {
        addButton.setEnabled(true);
        editButton.setEnabled(false);
        removeButton.setEnabled(false);
    }

    private IsWidget createContent() {
        final BorderLayoutContainer.BorderLayoutData eastSize = new BorderLayoutContainer.BorderLayoutData();
        eastSize.setSize(0.4);
        eastSize.setSplit(true);

        final BorderLayoutContainer borderContainer = new BorderLayoutContainer();
        borderContainer.setCenterWidget(createExistingPanels(), new MarginData());
        borderContainer.setEastWidget(opDetailsPanel, eastSize);

        VerticalLayoutContainer container = new VerticalLayoutContainer();
        container.add(createToolBar(), new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        container.add(borderContainer, new VerticalLayoutContainer.VerticalLayoutData(1, 1));
        return container;
    }

    private ContentPanel createExistingPanels() {
        final ContentPanel existingPanels = new ContentPanel();
        existingPanels.setHeadingText("Existing OPs");
        existingPanels.setWidget(grid);
        return existingPanels;
    }

    private IsWidget createToolBar() {
        final ToolBar toolBar = new ToolBar();
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(removeButton);
        return toolBar;
    }

    private ColumnModel<OP> createColumnModel() {
        ColumnConfig inum = new ColumnConfig(Properties.OP.inum(), 170, "Id");
        inum.setCell(new TextCell());

        nameColumn.setCell(new TextCell());

        ColumnConfig country = new ColumnConfig(Properties.OP.country(), 170, "Country");
        country.setCell(new TextCell());

        List<ColumnConfig<OP, ?>> list = new ArrayList<ColumnConfig<OP, ?>>();
        list.add(inum);
        list.add(nameColumn);
        list.add(country);

        return new ColumnModel<OP>(list);
    }
}
