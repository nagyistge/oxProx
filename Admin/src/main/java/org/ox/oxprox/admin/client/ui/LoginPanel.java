package org.ox.oxprox.admin.client.ui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.TextField;
import org.ox.oxprox.admin.client.Admin;
import org.ox.oxprox.admin.client.event.LoggedinEvent;
import org.ox.oxprox.admin.client.gin.AppGinjector;
import org.ox.oxprox.admin.shared.User;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 16/06/2014
 */

public class LoginPanel extends CenterLayoutContainer {

    private final TextField userField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextButton loginButton = new TextButton("Login", new SelectEvent.SelectHandler() {
        @Override
        public void onSelect(SelectEvent event) {
            onLogin();
        }
    });

    public LoginPanel() {

        userField.setAllowBlank(false);
        passwordField.setAllowBlank(false);

        final VerticalLayoutContainer verticalContainer = new VerticalLayoutContainer();
        verticalContainer.add(new FieldLabel(userField, "Username"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        verticalContainer.add(new FieldLabel(passwordField, "Password"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        verticalContainer.add(loginButton, new VerticalLayoutContainer.VerticalLayoutData(-1, -1, Admin.DEFAULT_MARGINS));

        final ContentPanel contentPanel = new ContentPanel();
        contentPanel.setHeadingHtml("Welcome to oxProx Administration");
        contentPanel.add(verticalContainer);

        add(contentPanel);
    }

    private void onLogin() {
        AppGinjector.injector.getService().login(userField.getValue(), passwordField.getValue(), new AsyncCallback<User>() {
            @Override
            public void onFailure(Throwable caught) {
                notifyNotLoggedin();
            }

            @Override
            public void onSuccess(User result) {
                AppGinjector.injector.getLoginService().setLoggedinUser(result);
                if (result != null) {
                    AppGinjector.injector.getEventBus().fireEvent(new LoggedinEvent(result));
                } else {
                    notifyNotLoggedin();
                }
            }
        });
    }

    private void notifyNotLoggedin() {
        Admin.showInformation("Failed to login. Please check your username and password.");
        passwordField.setValue("");
    }
}
