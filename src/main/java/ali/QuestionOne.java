package ali;

import java.util.concurrent.Semaphore;

/**
 * 题目理解：
 * 三个线程轮流有序打印，即需要三个线程之间有序的等待，不能死锁
 * 实现独占型锁即可以实现逻辑控制
 * ReentrantLock和Synchronized都可以实现，这里采用更为清晰的信号量实现
 */
public class QuestionOne {
    //控制打印多少组，请更改,默认为MAX_VALUE
    private static final Integer count = Integer.MAX_VALUE;
    //初始化三个信号量，初始SemaphoreA给一个通行证，SemaphoreL和SemaphoreI预设值为0
    private static Semaphore A = new Semaphore(1);
    private static Semaphore L = new Semaphore(0);
    private static Semaphore I = new Semaphore(0);

    static class ThreadPrintA extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < count; i++) {
                    //获取SemaphoreA的1个通行证，初始化了1个或者是ThreadPrintI中释放了1个，能拿到就继续执行任务，该通行证被拿走后后续循环acquire时为0，即等待
                    A.acquire();
                    System.out.print("a");
                    //释放SemaphoreL的1个通行证，后续任务可以拿到该通行证
                    L.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class ThreadPrintL extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < count; i++) {
                    //获取SemaphoreL的1个通行证，ThreadPrintA中释放了1个，能拿到就继续执行任务，该通行证被拿走后后续循环acquire时为0，即等待
                    L.acquire();
                    System.out.print("l");
                    //释放SemaphoreI的1个通行证，后续任务可以拿到该通行证
                    I.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class ThreadPrintI extends Thread {
        @Override
        public void run() {
            try {
                for (int i = 0; i < count; i++) {
                    //获取SemaphoreI的1个通行证，ThreadPrintL中释放了1个，能拿到就继续执行任务，该通行证被拿走后后续循环acquire时为0，即等待
                    I.acquire();
                    System.out.print("i");
                    //释放SemaphoreA的1个通行证，后续任务可以拿到该通行证
                    A.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        //start三个线程任务，分别负责打印ali
        new ThreadPrintA().start();
        new ThreadPrintL().start();
        new ThreadPrintI().start();
    }

}
