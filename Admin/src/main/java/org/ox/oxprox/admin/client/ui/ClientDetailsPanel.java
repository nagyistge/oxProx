package org.ox.oxprox.admin.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import org.ox.oxprox.admin.client.Admin;
import org.ox.oxprox.admin.shared.Client;
import org.ox.oxprox.model.gwt.ClientMapping;

import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class ClientDetailsPanel extends ContentPanel {

    private Label id = new Label();
    private Label displayName = new Label();
    private HTML scopeMapping = new HTML();
    private HTML claimMapping = new HTML();
    private HTML clientMapping = new HTML();

    public ClientDetailsPanel() {
        setHeadingText("Details");

        setWidget(createContent());
    }

    private IsWidget createContent() {
        VerticalLayoutContainer c = new VerticalLayoutContainer();
        c.add(new FieldLabel(id, "Id"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(displayName, "Display name"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(scopeMapping, "Scope mapping"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(claimMapping, "Claim mapping"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(clientMapping, "Client mapping"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        return c;
    }

    public void show(Client client) {
        if (client != null) {
            id.setText(client.getInum());
            displayName.setText(client.getDisplayName());
            scopeMapping.setHTML(createMappingHtml(client.getScopeMappingMap()));
            claimMapping.setHTML(createMappingHtml(client.getClaimMappingMap()));
            clientMapping.setHTML(createClientMappingHtml(client.getClientMapping()));
        } else {
            id.setText("");
            displayName.setText("");
            scopeMapping.setHTML("");
            claimMapping.setHTML("'");
            clientMapping.setHTML("");
        }

    }

    private String createClientMappingHtml(ClientMapping clientMapping) {
        String html = "";
        if (clientMapping != null && clientMapping.getMap() != null) {
            for (Map.Entry<String, ClientMapping.Client> entry : clientMapping.getMap().entrySet()) {
                final ClientMapping.Client value = entry.getValue();
                html = html + "op - "+entry.getKey() + "<br/>client id - " + entry.getValue().getId() + "<br/>client secret - " + value.getSecret() + "<br/><br/>";
            }
        }
        return html;
    }

    private String createMappingHtml(Map<String, String> scopeMappingMap) {
        String html = "";
        if (scopeMappingMap != null) {
            for (Map.Entry<String, String> entry : scopeMappingMap.entrySet()) {
                html = html + entry.getKey() + " = " + entry.getValue() + "<br/>";
            }
        }
        return html;
    }
}
