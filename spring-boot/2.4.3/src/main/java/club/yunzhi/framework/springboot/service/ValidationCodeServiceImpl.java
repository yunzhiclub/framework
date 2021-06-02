package club.yunzhi.framework.springboot.service;

import club.yunzhi.framework.springboot.Utils;
import com.mengyunzhi.core.exception.CallingIntervalIllegalException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.HashMap;

/**
 * 手机验证码
 *
 * @author panjie
 */
@SuppressWarnings("FieldCanBeLocal")
@Service
public class ValidationCodeServiceImpl implements ValidationCodeService {
    private final HashMap<String, CodeCache> cacheData = new HashMap<>();
    /**
     * 清除缓存概率为1/16
     */
    private final int clearFrequency = 15;
    /**
     * 验证码长度
     */
    private final int codeLength = 4;

    /**
     * 过期时间 ms
     */
    private int expiredTimes = 5 * 60 * 1000;

    /**
     * 最大获取次数
     * 对同一手机号获取次数超出该值该无效
     */
    private final int maxGetCount = 3;

    /**
     * 发送的最小间隔为1分钟
     * 在1分钟内只能发送1次
     */
    private final int minSendInterval = 60 * 1000;

    private final ShortMessageService shortMessageService;

    public ValidationCodeServiceImpl(ShortMessageServiceFactory shortMessageServiceFactory) {
        this.shortMessageService = shortMessageServiceFactory.getShortMessageService();
    }

    @Override
    public String sendCode(String phoneNumber) {
        Assert.isTrue(Utils.isMobile(phoneNumber), "电话号码格式不正确");
        if (!this.validateSendInterval(phoneNumber)) {
            throw new CallingIntervalIllegalException(String.format("该手机号%s发送频率过于频繁", phoneNumber));
        }
        String code = Utils.generateRandomNumberCode(this.codeLength);
        this.shortMessageService.sendValidateCode(phoneNumber, code);
        this.cacheData.put(phoneNumber, new CodeCache(code));
        return code;
    }

    private boolean validateSendInterval(String phoneNumber) {
        if (!this.cacheData.containsKey(phoneNumber)) {
            return true;
        }

        return !this.cacheData.get(phoneNumber).isEffective(this.minSendInterval);
    }

    /**
     * 校验验证码是否有效
     *
     * @param key  键
     * @param code 验证码
     */
    @Override
    public boolean validateCode(String key, String code) {
        if (code == null) {
            return false;
        }

        if (!this.cacheData.containsKey(key)) {
            return false;
        }

        CodeCache codeCache = this.cacheData.get(key);
        if (codeCache.isExpired(this.expiredTimes, this.maxGetCount)) {
            this.cacheData.remove(key);
            return false;
        }

        this.clearCacheRandom();

        return code.equals(codeCache.getCode());
    }

    /**
     * 随机清除cache
     * 当前时间的尾数为1010b （10)时，对过期的验证码进行清除
     */
    private void clearCacheRandom() {
        if ((Calendar.getInstance().getTimeInMillis() & this.clearFrequency) == this.clearFrequency) {
            this.cacheData.entrySet().removeIf(e -> e.getValue().isExpired(this.expiredTimes, this.maxGetCount));
        }
    }

    @Override
    public void setExpiredTimes(int expiredTimes) {
        this.expiredTimes = expiredTimes;
    }

    static class CodeCache {
        /**
         * 验证码
         */
        private String code;

        /**
         * 存入的时间
         */
        private Calendar time;

        /**
         * 被获取的次数
         * 验证码每被获取1次，该值加1
         */
        private int getCount = 0;

        CodeCache(String code) {
            this(code, Calendar.getInstance());
        }

        CodeCache(String code, Calendar time) {
            this.code = code;
            this.time = time;
        }

        String getCode() {
            this.getCount++;
            return this.code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Calendar getTime() {
            return this.time;
        }

        public void setTime(Calendar time) {
            this.time = time;
        }

        boolean isEffective(int effectiveTimes) {
            if (this.time == null) {
                return false;
            }

            return Math.abs(this.time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) <= effectiveTimes;
        }

        /**
         * 校验码是否有效
         *
         * @param effectiveTimes 有效时间
         * @param maxGetCount    最大获取次数
         */
        boolean isEffective(int effectiveTimes, int maxGetCount) {
            if (this.getCount >= maxGetCount) {
                return false;
            }
            return this.isEffective(effectiveTimes);
        }

        /**
         * 校验码是否过期
         *
         * @param expiredTimes 过期时间
         */
        public boolean isExpired(int expiredTimes) {
            return !this.isEffective(expiredTimes);
        }

        /**
         * 校验码是否过期
         *
         * @param expiredTimes 过期时间
         * @param maxGetTimes  最大获取次数
         */
        boolean isExpired(int expiredTimes, int maxGetTimes) {
            return !this.isEffective(expiredTimes, maxGetTimes);
        }
    }
}
