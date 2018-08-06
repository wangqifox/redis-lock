package love.wangqi.version1_1;

import love.wangqi.Lock;
import love.wangqi.RedisPool;
import redis.clients.jedis.Jedis;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/6 18:39
 */
public class RedisLock extends RedisPool implements Lock {
    @Override
    public Boolean lock(String key, long timeout) {
        Jedis redis = null;
        try {
            redis = getJedis();
            long currentTimestamp = System.currentTimeMillis();
            long newExpireTime = currentTimestamp + timeout;
            if (redis.setnx(key, String.valueOf(newExpireTime)) == 0) {
                String value = redis.get(key);
                long oldExpireTime = value == null ? 0 : Long.valueOf(value);
                if (oldExpireTime < currentTimestamp) {
                    String oldvalue = redis.getSet(key, String.valueOf(newExpireTime));
                    currentTimestamp = oldvalue == null ? 0 : Long.valueOf(oldvalue);
                    if (currentTimestamp == oldExpireTime) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return true;
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
