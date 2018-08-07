package love.wangqi.version3_2;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/7 下午2:46
 */
public interface Lock {
    Boolean lock(String key, long timeout);

    void unlock(String key);

    Boolean isLocked(String key);

    void expire(String key, long timeout);
}
