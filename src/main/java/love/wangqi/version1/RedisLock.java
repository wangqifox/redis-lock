package love.wangqi.version1;

import love.wangqi.Lock;
import love.wangqi.LockTemplate;
import love.wangqi.ScriptUtil;
import redis.clients.jedis.Jedis;

import java.util.Arrays;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/2 下午5:24
 */
public class RedisLock extends LockTemplate implements Lock {
    private String script;

    public RedisLock() {
        script = ScriptUtil.getScript("redis_lock_1.lua");
    }

    @Override
    protected Boolean lock(Jedis redis, String key, long timeout) {
        Object result = redis.eval(script,
                Arrays.asList(key),
                Arrays.asList(String.valueOf(timeout))
        );
        if (result != null && 0 != (Long) result) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void unlock(Jedis redis, String key) {
        redis.del(key);
    }
}
