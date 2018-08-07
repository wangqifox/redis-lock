package love.wangqi.version3;

import love.wangqi.Lock;
import love.wangqi.LockTemplate;
import love.wangqi.ScriptUtil;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/7 上午11:10
 */
public class RedisLock extends LockTemplate implements Lock {
    private ThreadLocal<Long> timestamp;
    private String unlockScript;

    public RedisLock() {
        timestamp = new ThreadLocal<>();
        unlockScript = ScriptUtil.getScript("redis_unlock_3.lua");
    }

    @Override
    protected Boolean lock(Jedis redis, String key, long timeout) {
        timestamp.set(System.currentTimeMillis());
        String result = redis.set(key, String.valueOf(timestamp.get()), "NX", "EX", timeout);
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
                Arrays.asList(String.valueOf(timestamp.get()))
        );
    }
}
