package club.yunzhi.framework.springboot.security;

import club.yunzhi.framework.springboot.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
public class MvcSecurityConfig extends WebSecurityConfigurerAdapter {

  private final BCryptPasswordEncoder passwordEncoder;

  public MvcSecurityConfig(OneTimePassword oneTimePassword) {
    this.passwordEncoder = new MyBCryptPasswordEncoder(oneTimePassword);
    User.setPasswordEncoder(this.passwordEncoder);
  }

  /**
   * 由于我们启用了@EnableSpringHttpSession后，而非RedisHttpSession.
   * 所以应该为SessionRepository提供一个实现。
   * 而Spring中默认给了一个SessionRepository的实现MapSessionRepository.
   *
   * @return
   */
  @Bean
  public SessionRepository sessionRepository() {
    SessionRepository mapSessionRepository = new MapSessionRepository();
    return mapSessionRepository;
  }

  /**
   * session认证方式为header认证
   *
   * @return Header认证策略
   */
  @Bean
  HttpSessionStrategy sessionStrategy() {
    return new HeaderHttpSessionStrategy();
  }

  /**
   * https://spring.io/guides/gs/securing-web/
   *
   * @param http http安全
   * @throws Exception 异常
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        // 开放端口
        .antMatchers("/user/getRolesByUsername").permitAll()
        .antMatchers("/user/sendVerificationCode").permitAll()
        .antMatchers("/user/userBinding").permitAll()
        .anyRequest().authenticated()
        .and().cors()
        .and().httpBasic()
        .and().csrf().disable();
  }

  /**
   * CORS设置.
   * CORS出现错误时，请日志等级设置为debug查看具体原因
   *
   * @return 配置
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:9876", "http://localhost:4200", "http://work-review.yunzhi.club:81"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("content-type", "x-auth-token", "authorization", "verificationCode"));
    configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return this.passwordEncoder;
  }
}
