package club.yunzhi.framework.springboot.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.util.Map;

/**
 * 获取application.yml中对应的配置信息
 *
 * @author panjie
 */
@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
  private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);

  private String crypto;

  private String smsType;

  private String username;

  private String password;

  private Integer timeout;

  private String prefix;

  private String attachment;

  private String token = "yunzhi.club";

  /**
   * 短信.
   */
  private Map<String, String> sms;

  public void setSms(Map<String, String> sms) {
    this.sms = sms;
  }

  /**
   * 短信配置信息 -- 以对象的形式存储短信配置.
   */
  private ShortMessageProperties shortMessageProperties;

  public AppProperties(ShortMessageProperties shortMessageProperties) {
    this.shortMessageProperties = shortMessageProperties;
  }

  public Map<String, String> getSms() {
    return this.sms;
  }

  public String getCrypto() {
    return this.crypto;
  }

  public void setCrypto(String crypto) {
    this.crypto = crypto;
  }

  public String getSmsType() {
    return this.smsType;
  }

  public void setSmsType(String smsType) {
    if ("local".equals(smsType) || "ali".equals(smsType)) {
      this.smsType = smsType;
    } else {
      throw new ValidationException("短信类型仅支持(local,ali),请检查");
    }
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getTimeout() {
    return this.timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getAttachment() {
    return this.attachment;
  }

  public void setAttachment(String attachment) {
    this.attachment = attachment;
  }

  private void setShortMessageProperties(ShortMessageProperties shortMessageProperties) {
    this.shortMessageProperties = shortMessageProperties;
  }

  public ShortMessageProperties getShortMessageProperties() {
    return this.shortMessageProperties;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}


