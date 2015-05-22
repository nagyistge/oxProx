package org.ox.oxprox.testframework;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2014
 */

public class TUserInfo {

    private static final Logger LOG = LoggerFactory.getLogger(TUserInfo.class);

    private final Map<String, List<String>> claims;

    public TUserInfo(String entityJson) {
        claims = parseClaims(entityJson);
    }

    public Map<String, List<String>> getClaims() {
        return claims;
    }

    private static Map<String, List<String>> parseClaims(String entityJson) {
        Map<String, List<String>> claims = new HashMap<String, List<String>>();
        try {
            JSONObject jsonObj = new JSONObject(entityJson);
            for (Iterator<String> iterator = jsonObj.keys(); iterator.hasNext(); ) {
                String key = iterator.next();
                List<String> values = new ArrayList<String>();

                JSONArray jsonArray = jsonObj.optJSONArray(key);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String value = jsonArray.optString(i);
                        if (value != null) {
                            values.add(value);
                        }
                    }
                } else {
                    String value = jsonObj.optString(key);
                    if (value != null) {
                        values.add(value);
                    }
                }

                claims.put(key, values);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return claims;
    }
}
