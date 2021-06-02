package club.yunzhi.framework.springboot.service;


import club.yunzhi.framework.springboot.Utils;
import club.yunzhi.framework.springboot.properties.AppProperties;
import club.yunzhi.framework.springboot.properties.ShortMessageProperties;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author panjie
 */
@Service
public class AliShortMessageServiceImpl implements ShortMessageService {
    private static final Logger logger = LoggerFactory.getLogger(AliShortMessageServiceImpl.class);
    private ShortMessageProperties shortMessageProperties;
    private final String templateCode = "SMS_180046507";

    public AliShortMessageServiceImpl(AppProperties appProperties) {
        this.shortMessageProperties = appProperties.getShortMessageProperties();
    }

    @Override
    public String getType() {
        return "ali";
    }

    /**
     * 发送验证码
     * 参考：阿里官方 短信服务dome
     * @param phoneNumber 手机号（仅支持大陆手机号）
     * @param code        验证码
     */
    @Override
    public void sendValidateCode(String phoneNumber, String code) {
        Assert.isTrue(Utils.isMobile(phoneNumber), "传入的手机号格式不正确");

        DefaultProfile profile = DefaultProfile.getProfile(
                this.shortMessageProperties.getRegionId(),
                this.shortMessageProperties.getAccessKeyId(),
                this.shortMessageProperties.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(this.shortMessageProperties.getDomain());
        request.setAction("SendSms");
        request.setVersion("2017-05-25");
        request.putQueryParameter("RegionId", this.shortMessageProperties.getRegionId());
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", this.shortMessageProperties.getSignName());
        request.putQueryParameter("TemplateCode", this.templateCode);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", code);
        request.putQueryParameter("TemplateParam", jsonObject.toString());
        try {
            CommonResponse response = client.getCommonResponse(request);
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.getData(), JsonObject.class);
            if (!jsonResponse.get("Code").getAsString().equals("OK")) {
                logger.error(phoneNumber + "发送短信发生错误：" + response.getData());
            }
        } catch (ServerException e) {
            logger.error(String.format("验证码发送发生服务端错误:%s,手机号：%s,内容：%s", e.getMessage(), phoneNumber, jsonObject.toString()));
            e.printStackTrace();
            throw new RuntimeException("验证码发送失败(服务端错误)", e);
        } catch (ClientException e) {
            logger.error(String.format("验证码发送发生客户端错误:%s,手机号：%s,内容：%s", e.getMessage(), phoneNumber, jsonObject.toString()));
            e.printStackTrace();
            throw new RuntimeException("验证码发送失败(客户端错误)", e);
        }
    }
}
