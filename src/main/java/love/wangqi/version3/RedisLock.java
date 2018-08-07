package love.wangqi.version3;

import love.wangqi.Lock;
import love.wangqi.RedisPool;
import love.wangqi.ScriptUtil;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/7 上午11:10
 */
public class RedisLock extends RedisPool implements Lock {
    private ThreadLocal<Long> timestamp;
    private String unlockScript;

    public RedisLock() {
        timestamp = new ThreadLocal<>();
        unlockScript = ScriptUtil.getScript("redis_unlock_3.lua");
    }

    @Override
    public Boolean lock(String key, long timeout) {
        Jedis redis = null;
        try {
            redis = getJedis();
            timestamp.set(System.currentTimeMillis());
            String result = redis.set(key, String.valueOf(timestamp.get()), "NX", "EX", timeout);
            if (result != null && result.equals("OK")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeJedis(redis);
        }
        return false;
    }

    @Override
    public void unlock(String key) {
        Jedis redis = null;
        try {
            redis = getJedis();
            redis.eval(unlockScript,
                    Arrays.asList(key),
                    Arrays.asList(String.valueOf(timestamp.get()))
                    );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeJedis(redis);
        }
    }
}
