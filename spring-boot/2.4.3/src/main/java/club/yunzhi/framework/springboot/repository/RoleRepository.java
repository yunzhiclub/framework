package club.yunzhi.framework.springboot.repository;


import club.yunzhi.framework.springboot.entity.Role;
import org.springframework.data.repository.CrudRepository;

/**
 * 角色.
 */
public interface RoleRepository extends CrudRepository<Role, Long> {
}
