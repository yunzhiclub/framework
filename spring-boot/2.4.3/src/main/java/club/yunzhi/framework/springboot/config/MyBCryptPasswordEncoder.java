package club.yunzhi.framework.springboot.config;

import club.yunzhi.framework.springboot.security.OneTimePassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

/**
 * 自定义密码校验器.
 * 注意：其不能够声明为@Component组件出现，否则将触发DaoAuthenticationProvider的构造函数
 * 从而直接注册DelegatingPasswordEncoder校验器
 */
public class MyBCryptPasswordEncoder extends BCryptPasswordEncoder {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 一次性密码.
   */
  private final OneTimePassword oneTimePassword;

  public MyBCryptPasswordEncoder(OneTimePassword oneTimePassword) {
    super();
    this.oneTimePassword = oneTimePassword;
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    if (rawPassword == null) {
      throw new IllegalArgumentException("rawPassword cannot be null");
    }

    // 当有一次性密码（每个密码仅能用一次）且未使用时，验证用户是否输入了超密
    Optional oneTimePassword = this.oneTimePassword.getPassword();
    if (oneTimePassword.isPresent() && oneTimePassword.get().equals(rawPassword.toString())) {
      logger.warn("当前正在使用超级密码登录");
      return true;
    }

    return super.matches(rawPassword, encodedPassword);
  }
}
