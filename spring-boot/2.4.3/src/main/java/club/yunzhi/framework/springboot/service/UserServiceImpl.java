package club.yunzhi.framework.springboot.service;


import club.yunzhi.framework.springboot.entity.Role;
import club.yunzhi.framework.springboot.entity.User;
import club.yunzhi.framework.springboot.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService, UserDetailsService, AuditorAware<User> {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Optional<User> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (null == authentication) {
      return Optional.empty();
    } else {
      try {
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return Optional.of(userDetail.getUser());
      } catch (Exception e) {
        this.logger.error("接收到了认证用户类型不正确,请在loadUserByUsername中返回UserDetail");
        throw e;
      }
    }
  }

  /**
   * 根据用户名获取用户
   *
   * @param username 用户名
   * @return
   * @throws UsernameNotFoundException
   */
  @Override
  public UserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

    // 设置用户角色
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    for (Role role : user.getRoles()) {
      authorities.add(new SimpleGrantedAuthority(role.getId()));
    }
    return new UserDetail(user, user.isNonLocked(), authorities);
  }

  @Override
  public void logout() {
    SecurityContextHolder.clearContext();
  }

  @Override
  public void updatePassword(String password, String newPassword) throws ValidationException {
    if (!this.checkPasswordIsRight(password)) {
      throw new ValidationException("旧密码不正确");
    }
    User currentUser = this.getCurrentAuditor().get();
    currentUser.setPassword(newPassword);
    this.userRepository.save(currentUser);
  }

  public boolean checkPasswordIsRight(String password) {
    User currentLoginUser = this.getCurrentAuditor().get();
    return currentLoginUser.verifyPassword(password);
  }

  public static class UserDetail extends org.springframework.security.core.userdetails.User {
    private User user;

    public UserDetail(User user, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
      super(user.getUsername(),
          user.getPassword(),
          true,
          true,
          true,
          accountNonLocked,
          authorities);
      this.user = user;
    }

    public User getUser() {
      return user;
    }
  }
}
