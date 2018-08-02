package love.wangqi.version1;

import love.wangqi.Lock;
import love.wangqi.RedisPool;
import love.wangqi.ScriptUtil;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/2 下午5:24
 */
public class RedisLock extends RedisPool implements Lock {
    private String script;

    public RedisLock() {
        script = ScriptUtil.getScript("redis_lock_1.lua");
    }

    @Override
    public Boolean lock(String key, long timeout) {
        Jedis redis = null;
        try {
            redis = getJedis();
            Object result = redis.eval(script,
                    Arrays.asList(key),
                    Arrays.asList(String.valueOf(timeout))
            );
            if (result != null && 0 != (Long) result) {
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
            redis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeJedis(redis);
        }
    }
}
