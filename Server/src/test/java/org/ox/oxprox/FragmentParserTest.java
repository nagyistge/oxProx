package org.ox.oxprox;

import junit.framework.Assert;
import org.apache.http.NameValuePair;
import org.ox.oxprox.service.FragmentParser;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/03/2014
 */

public class FragmentParserTest {

    @Test
    public void test() {
        final String fragment = "#code=cdea3897-d252-4d56-875a-e7d44128d392&id_token=eyJ0eXAiOiJKV1MiLCJhbGciOiJSUzI1NiIsImtpZCI6IjEifQ.eyJpc3MiOiJodHRwczovL3NlZWQuZ2x1dS5vcmciLCJhdWQiOiJAITExMTEhMDAwOCFGRjgxITJEMzkiLCJleHAiOjEzOTU5Mzg5MjMsImlhdCI6MTM5NTkzNTMyMywic3ViIjoieXVyaXkiLCJveEludW0iOiJAITExMTEhMDAwMCFEREREIiwibm9uY2UiOiJub25jZSAiLCJhdXRoX3RpbWUiOjEzOTU5MzUzMjEsImNfaGFzaCI6IkptQ1o2X1diRFU2SDN6NktSek1UckEiLCJveFZhbGlkYXRpb25VUkkiOiJodHRwczovL3NlZWQuZ2x1dS5vcmcvb3hhdXRoL29waWZyYW1lLnNlYW0iLCJveE9wZW5JRENvbm5lY3RWZXJzaW9uIjoib3BlbmlkY29ubmVjdC0xLjAiLCJ6b25laW5mbyI6IkFtZXJpY2EvQ2hpY2FnbyIsImVtYWlsIjoieXVyaXlAZ2x1dS5vcmciLCJsb2NhbGUiOiJlbl9VUyIsIm5hbWUiOiJZdXJpeSBaYWJyb3Zhcm55eSIsImxvY2FsaXR5IjoiTHZpdiIsImZhbWlseV9uYW1lIjoiWmFicm92YXJueXkiLCJnaXZlbl9uYW1lIjoiWXVyaXkiLCJhbXIiOiJiYXNpYyJ9.bIvgy25ZbOXC8_j3SpYNAlgFR_6Amn1P5Skv44zyyvxlcXILEuFsUkYGYjoxlPvJF8g4XAksZiDgqB1ev3FXhT1MUO61pZ7v-qIk9_jJD6nKACs4kJXT50ZNl9etuYZZ6Sz11ZQ0bX12XOe0NWg8R79wyVGygJAKrFKs1zaXMqOZNV7gXXdJko2ealN9fNM_T1R6zhX9KU6vYBA6H3oPMuocvx9Bhve519VknDsxC1550T3C_8N9RqMTMXF-pf3nZzBuJ799goa38DrL744mIZio5LLB8TRpbkfv4saMiYeh-erP46cyoK9WvFtY2VwERhvHqTHx3oCqrK4zjHraxw&auth_level=10&auth_mode=basic&session_id=84fe4881-4f96-4c04-94f2-ea210b806bae&scope=openid+profile+address+email";
        FragmentParser parser = new FragmentParser(fragment);
        final List<NameValuePair> pairList = parser.getPairList();
        Assert.assertNotNull(pairList);
    }
}
