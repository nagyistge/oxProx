package org.ox.oxprox.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.python.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/03/2014
 */

public class FragmentParser {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentParser.class);

    private final String originalFragment;
    private List<NameValuePair> pairList;

    public FragmentParser(String fragment) {
        this.originalFragment = fragment;
        if (!Strings.isNullOrEmpty(fragment)) {
            if (fragment.startsWith("#")) {
                try {
                    fragment = fragment.substring(1);
                    pairList = URLEncodedUtils.parse(new URI("http://a.com/?" + fragment), "UTF-8");
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }

            }
        }
    }

    public String getOriginalFragment() {
        return originalFragment;
    }

    public List<NameValuePair> getPairList() {
        return pairList;
    }
}
