package org.ox.oxprox.admin.client.ui;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import org.ox.oxprox.admin.client.Admin;
import org.ox.oxprox.admin.shared.OP;
import org.ox.oxprox.model.gwt.ClientMapping;

import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class OpDetailsPanel extends ContentPanel {

    private Label id = new Label();
    private Label displayName = new Label();
    private Label opId = new Label();
    private Label domain = new Label();
    private Label country = new Label();
    private Label city = new Label();

    public OpDetailsPanel() {
        setHeadingText("Details");

        setWidget(createContent());
    }

    private IsWidget createContent() {
        VerticalLayoutContainer c = new VerticalLayoutContainer();
        c.add(new FieldLabel(id, "Id"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(displayName, "Display name"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(opId, "OP Id"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(domain, "Domain"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(country, "Country"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        c.add(new FieldLabel(city, "City"), new VerticalLayoutContainer.VerticalLayoutData(1, -1, Admin.DEFAULT_MARGINS));
        return c;
    }

    public void show(OP op) {
        if (op != null) {
            id.setText(op.getInum());
            displayName.setText(op.getDisplayName());
            opId.setText(op.getOpId());
            domain.setText(op.getDomain());
            country.setText(op.getCountry());
            city.setText(op.getCity());
        } else {
            id.setText("");
            displayName.setText("");
            opId.setText("");
            domain.setText("'");
            country.setText("");
            city.setText("");
        }

    }

    private String createClientMappingHtml(ClientMapping clientMapping) {
        String html = "";
        if (clientMapping != null && clientMapping.getMap() != null) {
            for (Map.Entry<String, ClientMapping.Client> entry : clientMapping.getMap().entrySet()) {
                final ClientMapping.Client value = entry.getValue();
                html = html + "op - " + entry.getKey() + "<br/>client id - " + entry.getValue().getId() + "<br/>client secret - " + value.getSecret() + "<br/><br/>";
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