package club.yunzhi.framework.springboot.service;

import club.yunzhi.framework.springboot.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class LocalShortMessageServiceImpl implements ShortMessageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalShortMessageServiceImpl.class);

    @Override
    public String getType() {
        return "local";
    }

    @Override
    public void sendValidateCode(String phoneNumber, String code) {
        Assert.isTrue(Utils.isMobile(phoneNumber), "传入的手机号格式不正确");
        logger.info("目标手机号: {}, 验证码: {}", phoneNumber, code);
    }
}
