package org.ox.oxprox.model.server;

import org.codehaus.jackson.type.TypeReference;
import org.ox.oxprox.model.gwt.ClientMapping;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class ClientMappingUtils {
    public static String asJson(ClientMapping mapping) throws IOException {
        return ModelUtils.asJson(mapping.getMap());
    }

    public static ClientMapping parse(String json) throws IOException {
        TypeReference<HashMap<String, ClientMapping.Client>> typeRef = new TypeReference<HashMap<String, ClientMapping.Client>>() {
        };

        HashMap<String, ClientMapping.Client> parsedMap = ModelUtils.createJsonMapper().readValue(json, typeRef);
        final ClientMapping result = new ClientMapping();
        result.getMap().putAll(parsedMap);
        return result;
    }

    public static void main(String[] args) throws IOException {
        final ClientMapping mapping = new ClientMapping();
        mapping.getMap().put("example.com", new ClientMapping.Client("1231233", "secret"));
        mapping.getMap().put("seed.gluu.org", new ClientMapping.Client("@!1111!0008!FF81!2D40", "6213e9b9-c46d-4008-8af1-03f918a8ade4"));
        final String json = ModelUtils.asJson(mapping.getMap());
        System.out.println(json);
        System.out.println(parse(json));
    }
}
