package club.yunzhi.framework.springboot.service;

/**
 * 短信服务
 *
 * @author panjie
 */
public interface ShortMessageService {

    /**
     * 获取当前验证码的实现类型
     */
    String getType();

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号（仅支持大陆手机号）
     * @param code        验证码
     */
    void sendValidateCode(String phoneNumber, String code);
}
