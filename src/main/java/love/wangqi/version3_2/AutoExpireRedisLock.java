package love.wangqi.version3_2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/7 下午3:36
 */
public class AutoExpireRedisLock extends RedisLock {
    final static Logger logger = LoggerFactory.getLogger(AutoExpireRedisLock.class);

    private ThreadLocal<Thread> expireThread;

    public AutoExpireRedisLock() {
        expireThread = new ThreadLocal<>();
    }

    @Override
    public Boolean lock(String key, long timeout) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    if (isLocked(key)) {
                        expire(key, timeout);
                    }
                    long sleeptime = timeout / 2 > 0 ? 1000 * (timeout / 2) : 600;
                    try {
                        Thread.sleep(sleeptime);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });

        Boolean result = super.lock(key, timeout);
        if (result) {
            if (expireThread.get() == null) {
                thread.setDaemon(true);
                thread.start();
                expireThread.set(thread);
            }
        }
        return result;
    }

    @Override
    public void unlock(String key) {
        if (expireThread.get() != null) {
            expireThread.get().interrupt();
            expireThread.remove();
        }
        super.unlock(key);
    }
}
