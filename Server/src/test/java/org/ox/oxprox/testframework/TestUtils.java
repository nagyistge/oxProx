package org.ox.oxprox.testframework;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 06/05/2014
 */

public class TestUtils {

    private TestUtils() {
    }

    public static ClientExecutor createClientExecutor(Cookie... cookies) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        httpClient.setCookieStore(cookieStore);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieStore.addCookie(cookie);
            }
        }
        return new ApacheHttpClient4Executor(httpClient);
    }
}
