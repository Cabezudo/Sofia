package net.cabezudo.ws.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import net.cabezudo.json.JSON;
import net.cabezudo.json.exceptions.JSONParseException;
import net.cabezudo.json.exceptions.PropertyNotExistException;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.json.values.JSONObject;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.WebServer;
import net.cabezudo.sofia.core.passwords.Password;
import net.cabezudo.sofia.core.webusers.WebUserDataManager;
import net.cabezudo.sofia.core.webusers.WebUserDataManager.ClientData;
import net.cabezudo.sofia.hosts.Host;
import net.cabezudo.sofia.emails.EMail;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.08.04
 */
public class WebServicesTest {

  public WebServicesTest() {
  }

  @BeforeClass
  public static void startServer() throws Exception {
    // Start the server
    WebServer.getInstance().start();
  }

  @AfterClass
  public static void stopServer() throws Exception {
    // Stop the server
    WebServer.getInstance().stop();
  }

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testNonExistingGetResource() throws IOException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/this/resource/do/not/exists");
      CloseableHttpResponse response = httpclient.execute(get);
      Assert.assertEquals(404, response.getStatusLine().getStatusCode());
      Assert.assertEquals("Searching /api/v1/this/resource/do/not/exists", response.getStatusLine().getReasonPhrase());
    }
  }

  @Test
  public void testNonExistingPostResource() throws IOException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/this/resource/do/not/exists");
      CloseableHttpResponse response = httpclient.execute(post);
      Assert.assertEquals(404, response.getStatusLine().getStatusCode());
      Assert.assertEquals("Searching /api/v1/this/resource/do/not/exists", response.getStatusLine().getReasonPhrase());
    }
  }

  @Test
  public void testNonExistingPutResource() throws IOException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPut put = new HttpPut("http://localhost:8080/api/v1/this/resource/do/not/exists");
      CloseableHttpResponse response = httpclient.execute(put);
      Assert.assertEquals(404, response.getStatusLine().getStatusCode());
      Assert.assertEquals("Searching /api/v1/this/resource/do/not/exists", response.getStatusLine().getReasonPhrase());
    }
  }

  @Test
  public void testNonExistingDeleteResource() throws IOException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpDelete delete = new HttpDelete("http://localhost:8080/api/v1/this/resource/do/not/exists");
      CloseableHttpResponse response = httpclient.execute(delete);
      Assert.assertEquals(404, response.getStatusLine().getStatusCode());
      Assert.assertEquals("Searching /api/v1/this/resource/do/not/exists", response.getStatusLine().getReasonPhrase());
    }
  }

  @Test
  public void testMailValidation() throws Exception {
    String eMail = "esteban@cabezudo.net";

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"El nombre de dominio es correcto.\", \"type\": \"OK\" }, { \"message\": \"La dirección de correo es correcta.\", \"type\": \"OK\" } ], \"status\": \"VALID\" }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testEmptyMail() throws Exception {
    String eMail = "";

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"Debe especificar una dirección de correo.\", \"type\": \"ERROR\" } ] }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testMailMissingArroba() throws Exception {
    String eMail = "_esteban.cabezudo";

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"Las direcciones de correo deben tener un arroba.\", \"type\": \"ERROR\" } ] }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testMailInvalidLocalPart() throws Exception {
    String eMail = "es$teban";

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"La direccion de correo 'es$teban' tiene un caracter '$' no válido.\", \"type\": \"ERROR\" } ] }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testMailTooLong() throws Exception {
    expectedException.expect(HttpResponseException.class);
    String arrobaAndDomain = "@cabezudo.net";
    expectedException.expectMessage("e-mail too long: " + (EMail.MAX_LENGTH + 1 + arrobaAndDomain.length()));

    // Is part of the Apache HTTP client
    BasicResponseHandler responseHandler = new BasicResponseHandler();

    // The data for the test
    String eMail = Utils.repeat('e', EMail.MAX_LENGTH + 1) + arrobaAndDomain;

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      // Test for too long
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      String response = httpclient.execute(get, responseHandler);
      System.out.println(response);
    }
  }

  @Test
  public void testDomainNameForMailTooLong() throws Exception {
    String longDomainName = Utils.repeat('e', Host.NAME_MAX_LENGTH + 1) + ".net";
    String eMail = "esteban@" + longDomainName;

    expectedException.expect(HttpResponseException.class);
    expectedException.expectMessage("Domain name too long: " + longDomainName.length());

    BasicResponseHandler responseHandler = new BasicResponseHandler();
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      // Test for too long
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      httpclient.execute(get, responseHandler);
    }
  }

  @Test
  public void testEmptyDomain() throws Exception {
    // The data for the test
    String eMail = "esteban@";

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"El nombre de dominio no puede estar vacío.\", \"type\": \"ERROR\" } ] }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testInvalidCharaterInDomain() throws Exception {
    // The data for the test
    String eMail = "esteban@cabe$udo.net";

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"El caracter '$' en el nombre de dominio 'cabe$udo.net' no es válido.\", \"type\": \"ERROR\" } ] }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testMissingDotInDomain() throws Exception {
    // The data for the test
    String eMail = "esteban@cabeudonet";

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"El nombre de dominio 'cabeudonet' debe tener un punto.\", \"type\": \"ERROR\" } ] }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testDomainNoExists() throws Exception {
    // The data for the test
    String eMail = "esteban@cabezudo._ne-t";

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/mail/validate/" + eMail);
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"El nombre de dominio 'cabezudo._ne-t' no existe.\", \"type\": \"ERROR\" } ] }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testPasswordValidation() throws Exception {
    Password password = Password.createFromPlain("thePassword");

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/password/validate");
      String json = "{ \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);
      CloseableHttpResponse response = httpclient.execute(post);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"La contraseña tiene la forma correcta.\", \"type\": \"OK\" } ], \"status\": \"VALID\" }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testEmptyPassword() throws Exception {
    Password password = Password.createFromPlain("");

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/password/validate");
      String json = "{ \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);
      post.setHeader("Accept", "application/json");
      post.setHeader("Content-type", "application/json");
      CloseableHttpResponse response = httpclient.execute(post);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"Debe especificar una contraseña.\", \"type\": \"ERROR\" } ] }";
      Assert.assertEquals(expected, body);
      httpclient.close();
    }
  }

  @Test
  public void testPasswordTooLong() throws Exception {
    Password password = Password.createFromPlain(Utils.repeat('e', Password.MAX_LENGTH + 1));

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/password/validate");
      String json = "{ \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);
      post.setHeader("Accept", "application/json");
      post.setHeader("Content-type", "application/json");
      CloseableHttpResponse response = httpclient.execute(post);

      int statusCode = response.getStatusLine().getStatusCode();
      String statusLine = response.getStatusLine().getReasonPhrase();

      Assert.assertEquals(414, statusCode);
      Assert.assertEquals("Password too long: 51", statusLine);
    }
  }

  @Test
  public void testLoginOK() throws Exception {
    String eMail = "esteban@cabezudo.net";
    Password password = Password.createFromPlain("popo");

    String response = testLoginWithResponse(eMail, password);
    String expected = "{ \"messages\": [ { \"message\": \"El nombre de dominio es correcto.\", \"type\": \"OK\" }, { \"message\": \"La dirección de correo es correcta.\", \"type\": \"OK\" }, { \"message\": \"La contraseña tiene la forma correcta.\", \"type\": \"OK\" } ], \"authorized\": \"YES\" }";
    Assert.assertEquals(expected, response);
  }

  @Test
  public void testFailedLogin() throws Exception {
    String eMail = "esteban@cabezudo.net";
    Password password = Password.createFromPlain("badPassword");

    String body;

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpClientContext context = HttpClientContext.create();
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/users/login");
      String json = "{ \"email\": \"" + eMail + "\", \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);
      post.setHeader("Accept", "application/json");
      post.setHeader("Content-type", "application/json");
      CloseableHttpResponse response = httpclient.execute(post, context);
      CookieStore cookieStore = context.getCookieStore();
      List<Cookie> cookies = cookieStore.getCookies();
      Cookie sessionCookie = null;
      for (Cookie cookie : cookies) {
        if ("JSESSIONID".equals(cookie.getName())) {
          sessionCookie = cookie;
        }
      }
      if (sessionCookie == null) {
        throw new RuntimeException("Session cookie NOT exist.");
      }
      ClientData clientData = WebUserDataManager.getInstance().get(sessionCookie.getValue());
      Assert.assertEquals(2000, clientData.getFailLoginResponseTime());

      HttpEntity httpEntity = response.getEntity();
      body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);

      String expected = "{ \"messages\": [ { \"message\": \"El nombre de dominio es correcto.\", \"type\": \"OK\" }, { \"message\": \"La dirección de correo es correcta.\", \"type\": \"OK\" }, { \"message\": \"La contraseña tiene la forma correcta.\", \"type\": \"OK\" } ], \"authorized\": \"NO\" }";
      Assert.assertEquals(expected, body);

      password = Password.createFromPlain("pope");

      post = new HttpPost("http://localhost:8080/api/v1/users/login");
      json = "{ \"email\": \"" + eMail + "\", \"password\": \"" + password.toBase64() + "\" }";
      entity = new StringEntity(json);
      post.setEntity(entity);
      response = httpclient.execute(post, context);
      cookieStore = context.getCookieStore();
      cookies = cookieStore.getCookies();
      sessionCookie = null;
      for (Cookie cookie : cookies) {
        if ("JSESSIONID".equals(cookie.getName())) {
          sessionCookie = cookie;
        }
      }
      if (sessionCookie == null) {
        throw new RuntimeException("Session cookie NOT exist.");
      }
      clientData = WebUserDataManager.getInstance().get(sessionCookie.getValue());
      Assert.assertEquals(4000, clientData.getFailLoginResponseTime());

      httpEntity = response.getEntity();
      body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
    }
    String expected = "{ \"messages\": [ { \"message\": \"El nombre de dominio es correcto.\", \"type\": \"OK\" }, { \"message\": \"La dirección de correo es correcta.\", \"type\": \"OK\" }, { \"message\": \"La contraseña tiene la forma correcta.\", \"type\": \"OK\" } ], \"authorized\": \"NO\" }";
    Assert.assertEquals(expected, body);
  }

  private String testLoginWithResponse(String eMail, Password password) throws SQLException, IOException {

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpClientContext context = HttpClientContext.create();
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/users/login");
      String json = "{ \"email\": \"" + eMail + "\", \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);
      post.setHeader("Accept", "application/json");
      post.setHeader("Content-type", "application/json");
      CloseableHttpResponse response = httpclient.execute(post, context);
      HttpEntity httpEntity = response.getEntity();
      return EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
    }
  }

  @Test
  public void testEMailToLongInLogin() throws Exception {
    String eMail = Utils.repeat('e', EMail.MAX_LENGTH + 1) + "@cabezudo.net";
    Password password = Password.createFromPlain("anyPassword");

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/users/login");
      String json = "{ \"email\": \"" + eMail + "\", \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);
      CloseableHttpResponse response = httpclient.execute(post);
      Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
      Assert.assertEquals("Invalid request.", response.getStatusLine().getReasonPhrase());
    }
  }

  @Test
  public void testUserLogged() throws Exception {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/users/logged");
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"El usuario no está registrado.\", \"type\": \"OK\" } ], \"status\": \"NOT_LOGGED\" }";
      Assert.assertEquals(expected, body);

      String eMail = "esteban@cabezudo.net";
      Password password = Password.createFromPlain("popo");

      HttpPost post = new HttpPost("http://localhost:8080/api/v1/users/login");
      String json = "{ \"email\": \"" + eMail + "\", \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);

      response = httpclient.execute(post);
      int statusCode = response.getStatusLine().getStatusCode();
      Assert.assertEquals(200, statusCode);

      get = new HttpGet("http://localhost:8080/api/v1/users/logged");
      response = httpclient.execute(get);
      httpEntity = response.getEntity();
      body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      expected = "{ \"messages\": [ { \"message\": \"El usuario está registrado.\", \"type\": \"OK\" } ], \"status\": \"LOGGED\" }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testPasswordRecovery() throws Exception {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/users/password/esteban@cabezudo.net/password/recover");
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      String expected = "{ \"messages\": [ { \"message\": \"El nombre de dominio es correcto.\", \"type\": \"OK\" }, { \"message\": \"La dirección de correo es correcta.\", \"type\": \"OK\" } ], \"status\": \"SENT\" }";
      Assert.assertEquals(expected, body);
    }
  }

  @Test
  public void testUserList() throws IOException, PropertyNotExistException, JSONParseException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/users");
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      JSONObject jsonBody = JSON.parse(body).toJSONObject();
      JSONArray userList = jsonBody.getJSONArray("records");
      Assert.assertEquals(2, userList.size());
    }
  }

  @Test
  public void testPeopleList() throws IOException, PropertyNotExistException, JSONParseException {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/persons");
      CloseableHttpResponse response = httpclient.execute(get);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      JSONObject jsonBody = JSON.parse(body).toJSONObject();
      JSONArray userList = jsonBody.getJSONArray("records");
      Assert.assertEquals(4, userList.size());
    }
  }

  @Test
  public void testUptadePasswordByHash() throws Exception {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet get = new HttpGet("http://localhost:8080/api/v1/users/password/esteban@cabezudo.net/password/recover");
      CloseableHttpResponse response = httpclient.execute(get);
      response.getEntity();
    }

//    User user = UserManager.getInstance().getByEMail("esteban@cabezudo.net", owner);
//    String hash = user.getPasswordRecoveryHash();
//
//    Password password = Password.createFromPlain("newPassword");
//
//    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
//      HttpPost post = new HttpPost("http://localhost:8080/api/v1/users/" + hash + "/password");
//      String json = "{ \"password\": \"" + password.toBase64() + "\" }";
//      StringEntity entity = new StringEntity(json);
//      post.setEntity(entity);
//      CloseableHttpResponse response = httpclient.execute(post);
//      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
//    }
  }

  @Test
  public void testRegisterUser() throws Exception {

    Password password = Password.createFromPlain("popo");

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/users");
      String json = "{ \"name\": \"Federico\", \"lastName\": \"Cabezudo\", \"email\": \"federico@cabezudo.net\", \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);
      CloseableHttpResponse response = httpclient.execute(post);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      JSONObject jsonBody = JSON.parse(body).toJSONObject();
    }

    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost post = new HttpPost("http://localhost:8080/api/v1/users");
      String json = "{ \"name\": \"Federico\", \"lastName\": \"Cabezudo\", \"email\": \"federico@cabezudo.net\", \"password\": \"" + password.toBase64() + "\" }";
      StringEntity entity = new StringEntity(json);
      post.setEntity(entity);
      CloseableHttpResponse response = httpclient.execute(post);
      HttpEntity httpEntity = response.getEntity();
      String body = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
      JSONObject jsonBody = JSON.parse(body).toJSONObject();
    }
  }

}
