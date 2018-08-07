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
public class RedisLock extends RedisPool implements Lock {
    private ThreadLocal<String> uniqueId;
    private String unlockScript;

    public RedisLock() {
        uniqueId = new ThreadLocal<>();
        unlockScript = ScriptUtil.getScript("redis_unlock_3.lua");
    }

    @Override
    public Boolean lock(String key, long timeout) {
        Jedis redis = null;
        try {
            redis = getJedis();
            uniqueId.set(UUID.randomUUID().toString());
            String result = redis.set(key, uniqueId.get(), "NX", "EX", timeout);
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
                    Arrays.asList(uniqueId.get())
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeJedis(redis);
        }
    }

    @Override
    public Boolean isLocked(String key) {
        Jedis redis = null;
        try {
            redis = getJedis();
            String result = redis.get(key);
            if (result != null) {
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
    public void expire(String key, long timeout) {
        Jedis redis = null;
        try {
            redis = getJedis();
            redis.expire(key, (int)timeout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeJedis(redis);
        }
    }
}
