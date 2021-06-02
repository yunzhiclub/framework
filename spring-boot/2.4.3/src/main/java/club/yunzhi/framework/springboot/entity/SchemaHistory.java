package club.yunzhi.framework.springboot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * 数据更新历史.
 */
@Entity
public class SchemaHistory {

  @Id
  String version;

  Timestamp timestamp = new Timestamp(System.currentTimeMillis());

  public SchemaHistory() {
  }

  public SchemaHistory(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }
}
