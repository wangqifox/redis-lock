package love.wangqi;

import redis.clients.jedis.Jedis;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/7 下午5:15
 */
public abstract class LockTemplate extends RedisPool implements Lock {
    @Override
    public Boolean lock(String key, long timeout) {
        Jedis redis = null;
        try {
            redis = getJedis();
            return lock(redis, key, timeout);
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
            unlock(redis, key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeJedis(redis);
        }
    }

    protected abstract Boolean lock(Jedis redis, String key, long timeout);

    protected abstract void unlock(Jedis redis, String key);

}
