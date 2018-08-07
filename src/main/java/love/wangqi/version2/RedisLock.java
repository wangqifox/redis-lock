package love.wangqi.version2;

import love.wangqi.Lock;
import love.wangqi.LockTemplate;
import redis.clients.jedis.Jedis;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/7 上午8:20
 */
public class RedisLock extends LockTemplate implements Lock {

    @Override
    protected Boolean lock(Jedis redis, String key, long timeout) {
        String result = redis.set(key, "1", "NX", "EX", timeout);
        if (result != null && result.equals("OK")) {
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
