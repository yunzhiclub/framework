package club.yunzhi.framework.springboot.service;

import javax.xml.bind.ValidationException;

public interface UserService {

  /**
   * 注销
   *
   */
  void logout();

  /**
   * 修改密码.
   *
   * @param password    密码
   * @param newPassword 新密码
   */
  void updatePassword(String password, String newPassword) throws ValidationException;

}
