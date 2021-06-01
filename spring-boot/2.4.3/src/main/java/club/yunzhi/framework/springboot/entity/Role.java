package club.yunzhi.framework.springboot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

/**
 * 角色.
 */
@Entity
public class Role {
  /**
   * 用户角色.
   */
  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  public static final String ROLE_USER = "ROLE_USER";

  @Column(nullable = false)
  @Id
  private String name;

  @Column(nullable = false)
  private String description;

  private int weight = 0;

  public Role() {
  }


  public Role(Long id, String name, String description, int weight) {
    this.name = name;
    this.description = description;
    this.weight = weight;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Role role = (Role) o;
    return Objects.equals(name, role.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
