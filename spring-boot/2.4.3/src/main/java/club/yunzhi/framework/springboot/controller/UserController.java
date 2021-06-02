package club.yunzhi.framework.springboot.controller;

import club.yunzhi.framework.springboot.entity.User;
import club.yunzhi.framework.springboot.repository.UserRepository;
import club.yunzhi.framework.springboot.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpSession;
import javax.xml.bind.ValidationException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
  private final UserRepository userRepository;
  private final UserService userService;
  private final AuditorAware<User> auditorAware;

  public UserController(UserRepository userRepository, UserService userService, AuditorAware<User> auditorAware) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.auditorAware = auditorAware;
  }


  @RequestMapping("login")
  @JsonView(LoginJsonView.class)
  public User login(Principal user) {
    return this.userRepository.findByUsername(user.getName())
        .orElseThrow(() -> new EntityNotFoundException("未在数据库中找到用户，这可能是当前用户被删除导致的"));
  }

  @GetMapping("currentLoginUser")
  @JsonView(GetCurrentLoginUserJsonView.class)
  public User getCurrentLoginUser() {
    return this.auditorAware.getCurrentAuditor().get();
  }

  @RequestMapping("logout")
  public void logout(HttpSession httpSession) {
    httpSession.invalidate();
    this.userService.logout();
  }

  private interface LoginJsonView extends User.RolesJsonView {
  }

  private interface GetCurrentLoginUserJsonView extends User.RolesJsonView {
  }

  public interface UserBindingJsonView extends User.PasswordJsonView {
  }

}