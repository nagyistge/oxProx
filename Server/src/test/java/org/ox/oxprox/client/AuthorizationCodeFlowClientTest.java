package org.ox.oxprox.client;

import com.google.inject.Inject;
import org.ox.oxprox.TestAppModule;
import org.ox.oxprox.ws.DiscoveryWS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.BaseTest;
import org.xdi.oxauth.client.AuthorizationRequest;
import org.xdi.oxauth.client.AuthorizationResponse;
import org.xdi.oxauth.model.common.ResponseType;

import java.util.Arrays;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 28/04/2014
 */
@Guice(modules = TestAppModule.class)
public class AuthorizationCodeFlowClientTest extends BaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationCodeFlowClientTest.class);

    @Inject
    DiscoveryWS discoveryWS;

    /**
     * Test for the complete Authorization Code Flow.
     */
    @Parameters({"realUserId", "realUserSecret", "realRedirectUris", "realRedirectUri", "realClientId", "realClientSecret"})
    @Test
    public void authorizationCodeFlow(final String userId, final String userSecret, final String redirectUris,
                                      final String redirectUri, final String clientId, final String clientSecret) throws Exception {

        List<ResponseType> responseTypes = Arrays.asList(
                ResponseType.CODE,
                ResponseType.ID_TOKEN);

        // 2. Request authorization and receive the authorization code.
        List<String> scopes = Arrays.asList("openid", "profile", "address", "email");
        String state = "af0ifjsldkj";

        AuthorizationRequest authorizationRequest = new AuthorizationRequest(responseTypes, clientId, scopes, redirectUri, null);
        authorizationRequest.setState(state);

        AuthorizationResponse authorizationResponse = authenticateResourceOwnerAndGrantAccess(
                authorizationEndpoint, authorizationRequest, userId, userSecret);
//
//        assertNotNull(authorizationResponse.getLocation(), "The location is null");
//        assertNotNull(authorizationResponse.getCode(), "The authorization code is null");
//        assertNotNull(authorizationResponse.getState(), "The state is null");
//        assertNotNull(authorizationResponse.getScope(), "The scope is null");
//
//        String scope = authorizationResponse.getScope();
//        String authorizationCode = authorizationResponse.getCode();
//        String idToken = authorizationResponse.getIdToken();
//
//        // 3. Request access token using the authorization code.
//        TokenRequest tokenRequest = new TokenRequest(GrantType.AUTHORIZATION_CODE);
//        tokenRequest.setCode(authorizationCode);
//        tokenRequest.setRedirectUri(redirectUri);
//        tokenRequest.setAuthUsername(clientId);
//        tokenRequest.setAuthPassword(clientSecret);
//        tokenRequest.setAuthenticationMethod(AuthenticationMethod.CLIENT_SECRET_BASIC);
//
//        TokenClient tokenClient1 = new TokenClient(tokenEndpoint);
//        tokenClient1.setRequest(tokenRequest);
//        TokenResponse tokenResponse1 = tokenClient1.exec();
//
//        showClient(tokenClient1);
//        assertEquals(tokenResponse1.getStatus(), 200, "Unexpected response code: " + tokenResponse1.getStatus());
//        assertNotNull(tokenResponse1.getEntity(), "The entity is null");
//        assertNotNull(tokenResponse1.getAccessToken(), "The access token is null");
//        assertNotNull(tokenResponse1.getExpiresIn(), "The expires in value is null");
//        assertNotNull(tokenResponse1.getTokenType(), "The token type is null");
//        assertNotNull(tokenResponse1.getRefreshToken(), "The refresh token is null");
//
//        String refreshToken = tokenResponse1.getRefreshToken();
//
//        // 4. Validate id_token
//        Jwt jwt = Jwt.parse(idToken);
//        assertNotNull(jwt.getHeader().getClaimAsString(JwtHeaderName.TYPE));
//        assertNotNull(jwt.getHeader().getClaimAsString(JwtHeaderName.ALGORITHM));
//        assertNotNull(jwt.getClaims().getClaimAsString(JwtClaimName.ISSUER));
//        assertNotNull(jwt.getClaims().getClaimAsString(JwtClaimName.AUDIENCE));
//        assertNotNull(jwt.getClaims().getClaimAsString(JwtClaimName.EXPIRATION_TIME));
//        assertNotNull(jwt.getClaims().getClaimAsString(JwtClaimName.ISSUED_AT));
//        assertNotNull(jwt.getClaims().getClaimAsString(JwtClaimName.SUBJECT_IDENTIFIER));
//        assertNotNull(jwt.getClaims().getClaimAsString(JwtClaimName.CODE_HASH));
//        assertNotNull(jwt.getClaims().getClaimAsString(JwtClaimName.AUTHENTICATION_TIME));
//        assertNotNull(jwt.getClaims().getClaimAsString("oxInum"));
//        assertNotNull(jwt.getClaims().getClaimAsString("oxValidationURI"));
//        assertNotNull(jwt.getClaims().getClaimAsString("oxOpenIDConnectVersion"));
//
//        RSAPublicKey publicKey = JwkClient.getRSAPublicKey(
//                jwksUri,
//                jwt.getHeader().getClaimAsString(JwtHeaderName.KEY_ID));
//        RSASigner rsaSigner = new RSASigner(SignatureAlgorithm.RS256, publicKey);
//
//        assertTrue(rsaSigner.validate(jwt));
//
//        // 5. Request new access token using the refresh token.
//        TokenClient tokenClient2 = new TokenClient(tokenEndpoint);
//        TokenResponse tokenResponse2 = tokenClient2.execRefreshToken(scope, refreshToken, clientId, clientSecret);
//
//        showClient(tokenClient2);
//        assertEquals(tokenResponse2.getStatus(), 200, "Unexpected response code: " + tokenResponse2.getStatus());
//        assertNotNull(tokenResponse2.getEntity(), "The entity is null");
//        assertNotNull(tokenResponse2.getAccessToken(), "The access token is null");
//        assertNotNull(tokenResponse2.getTokenType(), "The token type is null");
//        assertNotNull(tokenResponse2.getRefreshToken(), "The refresh token is null");
//        assertNotNull(tokenResponse2.getScope(), "The scope is null");
    }

//    public AuthorizationResponse authenticateResourceOwnerAndGrantAccess(
//               String authorizeUrl, AuthorizationRequest authorizationRequest, String userId, String userSecret) {
//           String authorizationRequestUrl = authorizeUrl + "?" + authorizationRequest.getQueryString();
//
//           AuthorizeClient authorizeClient = new AuthorizeClient(authorizeUrl);
//           authorizeClient.setRequest(authorizationRequest);              k
//
//           startSelenium();
//           deleteAllCookies();
//           driver.navigate().to(authorizationRequestUrl);
//
//           WebElement usernameElement = driver.findElement(By.name(loginFormUsername));
//           WebElement passwordElement = driver.findElement(By.name(loginFormPassword));
//           WebElement loginButton = driver.findElement(By.name(loginFormLoginButton));
//
//           usernameElement.sendKeys(userId);
//           passwordElement.sendKeys(userSecret);
//           loginButton.click();
//
//           //(new WebDriverWait(driver, 30)).until(ExpectedConditions.not(ExpectedConditions.titleIs("oxAuth - Login")));
//
//           String authorizationResponseStr = driver.getCurrentUrl();
//
//           if (authorizationRequest.getRedirectUri() == null ||
//                   !authorizationResponseStr.startsWith(authorizationRequest.getRedirectUri())) {
//
//               //WebElement allowButton = (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.name(authorizeFormAllowButton)));
//               WebElement allowButton = driver.findElement(By.name(authorizeFormAllowButton));
//
//               WebElement doNotAllowButton = driver.findElement(By.name(authorizeFormDoNotAllowButton));
//
//               final String previousURL = driver.getCurrentUrl();
//               allowButton.click();
//               WebDriverWait wait = new WebDriverWait(driver, 10);
//               wait.until(new ExpectedCondition<Boolean>() {
//                   public Boolean apply(WebDriver d) {
//                       return (d.getCurrentUrl() != previousURL);
//                   }
//               });
//
//               authorizationResponseStr = driver.getCurrentUrl();
//           }
//
//           stopSelenium();
//
//           AuthorizationResponse authorizationResponse = new AuthorizationResponse(authorizationResponseStr);
//           authorizeClient.setResponse(authorizationResponse);
//           showClientUserAgent(authorizeClient);
//
//           return authorizationResponse;
//       }

}
