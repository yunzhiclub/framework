package club.yunzhi.framework.springboot.security;

import club.yunzhi.framework.springboot.properties.AppProperties;
import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
public class OneTimePasswordImpl implements OneTimePassword {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  /**
   * 密码.
   */
  private String password = "";

  private final String token;

  public OneTimePasswordImpl(AppProperties appProperties) {
    // 将token使用base32进行转码，原理同base64
    Base32 base32 = new Base32();
    this.token = base32.encodeAsString(appProperties.getToken().getBytes());
  }

  /**
   * 仅允许获取1次，获取成功后code值为null
   *
   * @return
   */
  @Override
  public Optional<String> getPassword() {
    try {
      String password = TimeBasedOneTimePasswordUtil.generateCurrentNumberString(this.token);
      // 每个密码只能用一次，如果生成的密码与当前的密码相同，则说明短时间内请求了两次，返回empty
      if (password.equals(this.password)) {
        return Optional.empty();
      } else {
        this.password = password;
      }
    } catch (GeneralSecurityException e) {
      this.logger.error("生成一次性密码时发生错误");
      e.printStackTrace();
    }

    return Optional.of(this.password);
  }
}
