package club.yunzhi.framework.springboot.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"username", "deleteAt"}))
@SQLDelete(sql = "update `user` set deleted = 1, delete_at = UNIX_TIMESTAMP() where id = ?")
@Where(clause = "deleted = false")
public class User extends BaseEntity {

  /**
   * 默认密码
   */
  public static final String DEFAULT_PASSWORD = "hebut";

  private static PasswordEncoder passwordEncoder;
  /**
   * 用户状态.
   */
  public static Integer STATUS_FROZEN = 0;
  public static Integer STATUS_NORMAL = 1;


  @JsonView(PasswordJsonView.class)
  private String password;

  @Column(nullable = false)
  private String name = "";

  @Column(nullable = false)
  private String num = "";

  /**
   * 角色.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JsonView(RolesJsonView.class)
  private List<Role> roles = new ArrayList<>();

  /**
   * 状态：
   * 0 冻结中
   * 1 正常.
   */
  private Integer status = User.STATUS_FROZEN;

  @Column(nullable = false)
  private String username;

  private Long deleteAt;

  public static void setPasswordEncoder(PasswordEncoder passwordEncoder) {
    User.passwordEncoder = passwordEncoder;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if (User.passwordEncoder == null) {
      throw new RuntimeException("未设置User实体的passwordEncoder，请调用set方法设置");
    }
    this.password = User.passwordEncoder.encode(password);
  }

  /**
   * 校验密码.
   *
   * @param password 密码.
   * @return 校验结果.
   */
  public boolean verifyPassword(String password) {
    return User.passwordEncoder.matches(password, this.password);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNum() {
    return num;
  }

  public void setNum(String num) {
    this.num = num;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * 判断用户是否被锁定.
   *
   * @return 锁定   false ;未锁定  true.
   */
  public boolean isNonLocked() {
    return !User.STATUS_FROZEN.equals(this.getStatus());
  }

  public Long getDeleteAt() {
    return deleteAt;
  }

  public void setDeleteAt(Long deleteAt) {
    this.deleteAt = deleteAt;
  }

  public interface PasswordJsonView {
  }


  public interface RolesJsonView {
  }
}
