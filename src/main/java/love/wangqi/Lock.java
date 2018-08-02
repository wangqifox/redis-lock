package love.wangqi;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/2 下午5:25
 */
public interface Lock {
    Boolean lock(String key, long timeout);

    void unlock(String key);
}
