package org.ox.oxprox;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/12/2013
 */
@Guice(modules = TestAppModule.class)
public class DevTest {

    @Test
    public void test() throws UnsupportedEncodingException {
        String url = "http://localhost:8090/math/";
        System.out.println(URLEncoder.encode(url, "UTF-8"));
    }

}
