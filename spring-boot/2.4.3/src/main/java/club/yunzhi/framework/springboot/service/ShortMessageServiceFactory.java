package club.yunzhi.framework.springboot.service;

import club.yunzhi.framework.springboot.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 验证码工厂类
 */
@Component
public class ShortMessageServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(ShortMessageServiceFactory.class);

    private String smsType;
    private final List<ShortMessageService> shortMessageServices;

    public ShortMessageServiceFactory(AppProperties examProperties,
                                      List<ShortMessageService> shortMessageServices) {
        this.smsType = examProperties.getSmsType();
        this.shortMessageServices = shortMessageServices;
    }

    /**
     * 获取短消息服务
     */
    public ShortMessageService getShortMessageService() {
        logger.debug("遍历列表，寻找匹配的实现");
        for (ShortMessageService shortMessageService : this.shortMessageServices) {
            if (this.smsType.equals(shortMessageService.getType())) {
                return shortMessageService;
            }
        }

        throw new RuntimeException("未找到符合条件的 ShortMessageService, 请检查 yml 配置");
    }
}
