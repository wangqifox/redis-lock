package love.wangqi.version3_1;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/8/2 下午5:38
 */
public class RedisLockTest {
    final static Logger logger = LoggerFactory.getLogger(RedisLockTest.class);

    @Test
    public void test01() throws InterruptedException {
        RedisLock redisLock = new RedisLock();
        class MyRun implements Runnable {
            Random random = new Random();
            @Override
            public void run() {
                boolean result;
                while (true) {
                    result = redisLock.lock("lock_test", 10);
                    if (result) {
                        logger.info("result {}", result);
                        try {
                            Thread.sleep(random.nextInt(1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        redisLock.unlock("lock_test");
                        break;
                    }
                }
            }
        }

        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            threadList.add(new Thread(new MyRun()));
        }
        logger.info("start");
        for (Thread thread : threadList) {
            thread.start();
        }
        for (Thread thread : threadList) {
            thread.join();
        }
    }
}