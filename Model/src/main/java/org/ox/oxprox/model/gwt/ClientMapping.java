package org.ox.oxprox.model.gwt;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 08/05/2014
 */

public class ClientMapping implements Serializable {

    public static class Client implements Serializable {

//        @JsonProperty(value = "id")
        private String id;
//        @JsonProperty(value = "secret")
        private String secret;

        public Client() {
        }

        public Client(String id, String secret) {
            this.id = id;
            this.secret = secret;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    private Map<String, Client> map = Maps.newHashMap();

    public ClientMapping() {
    }

    public Map<String, Client> getMap() {
        return map;
    }
}
