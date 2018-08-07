package love.wangqi.version3_2;

import love.wangqi.RedisPool;
import love.wangqi.ScriptUtil;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/7 下午2:50
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

    @Override
    protected Boolean isLocked(Jedis redis, String key) {
        String result = redis.get(key);
        if (result != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void expire(Jedis redis, String key, long timeout) {
        redis.expire(key, (int)timeout);
    }
}
