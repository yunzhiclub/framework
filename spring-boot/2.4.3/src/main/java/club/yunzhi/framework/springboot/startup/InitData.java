package club.yunzhi.framework.springboot.startup;

import club.yunzhi.framework.springboot.entity.Role;
import club.yunzhi.framework.springboot.entity.SchemaHistory;
import club.yunzhi.framework.springboot.entity.User;
import club.yunzhi.framework.springboot.properties.AppProperties;
import club.yunzhi.framework.springboot.repository.RoleRepository;
import club.yunzhi.framework.springboot.repository.SchemaHistoryRepository;
import club.yunzhi.framework.springboot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 系统数据初始化.
 */
@Component
public class InitData implements ApplicationListener<ContextRefreshedEvent>, Ordered {
  public final static int order = 0;

  private static final Logger logger = LoggerFactory.getLogger(InitData.class);

  private final AppProperties appProperties;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final SchemaHistoryRepository schemaHistoryRepository;

  public InitData(AppProperties appProperties,
                  UserRepository userRepository,
                  RoleRepository roleRepository,
                  SchemaHistoryRepository schemaHistoryRepository) {
    this.appProperties = appProperties;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.schemaHistoryRepository = schemaHistoryRepository;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    String version = "1.0.0";
    if (!this.schemaHistoryRepository.findById(version).isPresent()) {
      logger.info("初始化角色及系统管理员");
      this.schemaHistoryRepository.save(new SchemaHistory(version));
      Role adminRole = this.roleRepository.save(new Role(Role.ROLE_ADMIN, "系统管理员", 0));

      User systemAdmin = new User();
      systemAdmin.setStatus(User.STATUS_NORMAL);
      systemAdmin.setUsername(this.appProperties.getUsername());
      systemAdmin.setPassword(this.appProperties.getPassword());
      systemAdmin.getRoles().add(adminRole);
      this.userRepository.save(systemAdmin);
    }
  }

  @Override
  public int getOrder() {
    return order;
  }
}
