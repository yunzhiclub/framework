package club.yunzhi.framework.springboot.controller;

import club.yunzhi.framework.springboot.entity.User;
import club.yunzhi.framework.springboot.properties.AppProperties;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
@AutoConfigureMockMvc
@WithMockUser(username = "13920618851", password = "admin",
    roles = {"ADMIN"})
@Transactional
public class UserControllerAuthTest {

  @LocalServerPort
  private int port;

  @Autowired
  RestTemplateBuilder restTemplateBuilder;

  @Autowired
  AppProperties appProperties;

  @Test
  void login() throws UnsupportedEncodingException {
    String url = "http://localhost:" + port + "/user/login";
    RestTemplate restTemplate = this.restTemplateBuilder.build();
    HttpHeaders headers = this.getChromeHeaders();

    // 没有认证信息时401
    Assertions.assertThrows(HttpClientErrorException.class, () -> restTemplate.getForObject(url, JSONObject.class));
    try {
      HttpEntity entity = new HttpEntity(headers);
      restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
    } catch (HttpClientErrorException e) {
      Assertions.assertEquals(e.getStatusCode().value(), HttpStatus.UNAUTHORIZED.value());
    }

    // basic认证模式
    headers = this.getChromeHeaders();
    String auth = Base64.getEncoder().encodeToString(
        (appProperties.getUsername() + ":" + appProperties.getPassword()).getBytes("utf-8"));
    headers.add("Authorization", "Basic " + auth);
    HttpEntity entity = new HttpEntity(headers);
    ResponseEntity<User> result = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
    String xAuthToken = result.getHeaders().get("x-auth-token").get(0);
    Assertions.assertNotNull(xAuthToken);
    User body = result.getBody();
    Assertions.assertEquals(appProperties.getUsername(), body.getUsername());

    // x-auth-token认证
    headers = this.getChromeHeaders();
    headers.add("x-auth-token", xAuthToken);
    User user = restTemplate.exchange(url, HttpMethod.GET, entity, User.class).getBody();
    Assertions.assertEquals(appProperties.getUsername(), user.getUsername());
  }

  @Test
  void xAuthToken() throws UnsupportedEncodingException {
    String loginUrl = "http://localhost:" + port + "/user/login";
    String logoutUrl = "http://localhost:" + port + "/user/logout";

    // 第一次请求，无登录信息，断言未获取到token
    RestTemplate restTemplate = this.restTemplateBuilder.build();
    HttpHeaders headers = this.getChromeHeaders();
    HttpEntity entity = new HttpEntity(headers);
    String xAuthToken = null;
    try {
      restTemplate.exchange(loginUrl, HttpMethod.GET, entity, User.class);
    } catch (HttpStatusCodeException exception) {
      HttpHeaders headers1 = exception.getResponseHeaders();
      xAuthToken = headers1.getFirst("x-auth-token");
    }
    Assertions.assertNull(xAuthToken);

    // 第三次请求，加入登录信息
    String auth = Base64.getEncoder().encodeToString((appProperties.getUsername() + ":" + appProperties.getPassword())
        .getBytes("utf-8"));
    headers.add("Authorization", "Basic " + auth);
    ResponseEntity<User> result =  restTemplate.exchange(loginUrl, HttpMethod.GET, entity, User.class);
    xAuthToken = result.getHeaders().getFirst("x-auth-token");
    Assertions.assertNotNull(xAuthToken);

    // 第四次请求，去除登录信息，直接使用token，请求成功
    headers.add("x-auth-token", xAuthToken);
    headers.remove("Authorization");
    restTemplate.exchange(loginUrl, HttpMethod.GET, entity, User.class);

    // 先注销，接着注销后再次请求，失败
    restTemplate.exchange(logoutUrl, HttpMethod.GET, entity, Object.class);
    Assertions.assertThrows(HttpStatusCodeException.class, () ->
        restTemplate.exchange(logoutUrl, HttpMethod.GET, entity, User.class));

    // 第五次，随便写个token
    headers.remove("x-auth-token");
    headers.add("x-auth-token", UUID.randomUUID().toString());
    Assertions.assertThrows(HttpStatusCodeException.class, () ->
        restTemplate.exchange(loginUrl, HttpMethod.GET, entity, User.class));
  }

  private HttpHeaders getChromeHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Proxy-Connection", "keep-alive");
    headers.add("Pragma", "no-cache");
    headers.add("Cache-Control", "no-cache");
    headers.add("Accept", "application/json, text/plain, */*");
    headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.192 Safari/537.36");
    headers.add("Origin", "http://localhost:4200");
    headers.add("Referer", "http://localhost:4200/");
    headers.add("Accept-Encoding", "gzip, deflate");
    headers.add("Accept-Language", "en-GB,en;q=0.9,zh-CN;q=0.8,zh;q=0.7");
    return headers;
  }
}
