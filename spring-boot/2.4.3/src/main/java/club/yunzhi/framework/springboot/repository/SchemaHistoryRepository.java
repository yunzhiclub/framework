package club.yunzhi.framework.springboot.repository;


import club.yunzhi.framework.springboot.entity.SchemaHistory;
import org.springframework.data.repository.CrudRepository;

/**
 * 数据更新历史.
 */
public interface SchemaHistoryRepository extends CrudRepository<SchemaHistory, String> {
}
