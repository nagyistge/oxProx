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
import org.ox.oxprox.admin.shared.Client;

import java.util.ArrayList;
import java.util.List;

import static org.ox.oxprox.admin.client.gin.AppGinjector.injector;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class ClientTab extends SimpleContainer {

    private final TextButton addButton = new TextButton("Add");
    private final TextButton editButton = new TextButton("Edit");
    private final TextButton removeButton = new TextButton("Remove");

    private final ColumnConfig nameColumn = new ColumnConfig(Properties.CLIENT.displayName(), 200, "Name");
    private final ClientDetailsPanel clientDetailsPanel = injector.getClientDetailsPanel();
    private ListStore<Client> store = new ListStore<Client>(Properties.CLIENT.dn());
    private Grid<Client> grid = new Grid<Client>(store, createColumnModel());
    private GridSelectionModel<Client> sm= new GridSelectionModel<Client>();

    public ClientTab() {
        init();
        setWidget(createContent());
        setButtonsState();
    }

    private void init() {
        grid.setSelectionModel(sm);
        grid.getView().setAutoExpandColumn(nameColumn);
        grid.setStateful(false);

        sm.setSelectionMode(Style.SelectionMode.SINGLE);
        sm.addSelectionChangedHandler(new SelectionChangedEvent.SelectionChangedHandler<Client>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<Client> event) {
                clientDetailsPanel.show(sm.getSelectedItem());
            }
        });

        injector.getService().getClientList(new AsyncCallback<List<Client>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Client> clients) {
                store.clear();
                store.addAll(clients);
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
        borderContainer.setCenterWidget(createExistingClients(), new MarginData());
        borderContainer.setEastWidget(clientDetailsPanel, eastSize);

        VerticalLayoutContainer container = new VerticalLayoutContainer();
        container.add(createToolBar(), new VerticalLayoutContainer.VerticalLayoutData(1, -1));
        container.add(borderContainer, new VerticalLayoutContainer.VerticalLayoutData(1, 1));
        return container;
    }

    private ContentPanel createExistingClients() {
        final ContentPanel existingClients = new ContentPanel();
        existingClients.setHeadingText("Existing clients");
        existingClients.setWidget(grid);
        return existingClients;
    }

    private IsWidget createToolBar() {
        final ToolBar toolBar = new ToolBar();
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(removeButton);
        return toolBar;
    }

    private ColumnModel<Client> createColumnModel() {
        ColumnConfig inum = new ColumnConfig(Properties.CLIENT.inum(), 170, "Id");
        inum.setCell(new TextCell());

        nameColumn.setCell(new TextCell());

        List<ColumnConfig<Client, ?>> list = new ArrayList<ColumnConfig<Client, ?>>();
        list.add(inum);
        list.add(nameColumn);

        return new ColumnModel<Client>(list);
    }
}
