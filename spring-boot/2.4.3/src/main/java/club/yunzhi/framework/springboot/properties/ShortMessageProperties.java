package club.yunzhi.framework.springboot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * 短信验证码属性.
 *
 * @author panjie
 */
@ConfigurationProperties(prefix = "app.sms")
@Component
public class ShortMessageProperties {
  /**
   * 访问KEY ID.
   */
  @NotBlank
  private String accessKeyId;

  /**
   * 访问密钥.
   */
  @NotBlank
  private String accessSecret;

  /**
   * 域名.
   */
  private String domain = "dysmsapi.aliyuncs.com";

  /**
   * 区域ID.
   */
  private String regionId = "cn-hangzhou";

  private String signName = "工大试题系统";

  public ShortMessageProperties() {
  }

  public String getAccessKeyId() {
    return this.accessKeyId;
  }

  public void setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
  }

  public String getAccessSecret() {
    return this.accessSecret;
  }

  public void setAccessSecret(String accessSecret) {
    this.accessSecret = accessSecret;
  }

  public String getDomain() {
    return this.domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getRegionId() {
    return this.regionId;
  }

  public void setRegionId(String regionId) {
    this.regionId = regionId;
  }

  public String getSignName() {
    return this.signName;
  }

  public void setSignName(String signName) {
    this.signName = signName;
  }
}
