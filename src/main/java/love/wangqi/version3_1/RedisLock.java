package love.wangqi.version3_1;

import love.wangqi.Lock;
import love.wangqi.LockTemplate;
import love.wangqi.ScriptUtil;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/7 上午11:30
 */
public class RedisLock extends LockTemplate implements Lock {
    private ThreadLocal<String> uniqueId;
    private String unlockScript;

    public RedisLock() {
        uniqueId = new ThreadLocal<>();
        unlockScript = ScriptUtil.getScript("redis_unlock_3.lua");
    }

    @Override
    protected Boolean lock(Jedis redis, String key, long timeout) {
        uniqueId.set(UUID.randomUUID().toString());
        String result = redis.set(key, uniqueId.get(), "NX", "EX", timeout);
        if (result != null && result.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void unlock(Jedis redis, String key) {
        redis.eval(unlockScript,
                Arrays.asList(key),
                Arrays.asList(uniqueId.get())
        );
    }
}
