package org.ox.oxprox.admin.client.ui;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import org.ox.oxprox.admin.client.Admin;
import org.ox.oxprox.admin.client.event.LoggedinEvent;
import org.ox.oxprox.admin.shared.User;

import static org.ox.oxprox.admin.client.gin.AppGinjector.injector;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 17/06/2014
 */

public class MainPanel extends ContentPanel  {

    private TabPanel tabPanel = new TabPanel();
    private TextButton logoutButton = new TextButton("Login");

    public MainPanel() {
        setHeadingHtml("oxProx Administration");
        setWidget(createContent());
        injector.getEventBus().addHandler(LoggedinEvent.TYPE, new LoggedinEvent.Handler() {
            @Override
            public void update(LoggedinEvent p_event) {
                setUsername();
            }
        });
    }

    private IsWidget createContent() {
        tabPanel = new TabPanel();
        tabPanel.add(injector.getOpTab(), new TabItemConfig("OpenID Providers", false));
        tabPanel.add(injector.getClientTab(), new TabItemConfig("Clients", false));
        tabPanel.add(injector.getScriptTab(), new TabItemConfig("Scripts", false));

        VerticalLayoutContainer container = new VerticalLayoutContainer();
        container.add(createToolbar(), new VerticalLayoutContainer.VerticalLayoutData(1, -1, new Margins(0)));
        container.add(tabPanel, new VerticalLayoutContainer.VerticalLayoutData(1, 1, Admin.DEFAULT_MARGINS));
        return container;
    }

    private void setUsername() {
        final User user = injector.getLoginService().getLoggedinUser();
        final String userName = user != null ? user.getFullname() : "";
        logoutButton.setText(userName);
    }

    private IsWidget createToolbar() {
        final MenuItem logoutMenu = new MenuItem("Logout");
        logoutMenu.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                injector.getEventBus().fireEvent(new LoggedinEvent(null));
            }
        });

        final Menu menu = new Menu();
        menu.add(logoutMenu);
        logoutButton.setMenu(menu);

        final ToolBar toolbar = new ToolBar();
        toolbar.setEnableOverflow(false);

        toolbar.add(new FillToolItem());
        toolbar.add(logoutButton);
        return toolbar;
    }
}
