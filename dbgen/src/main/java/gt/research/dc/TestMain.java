package gt.research.dc;

import java.util.Vector;

/**
 * Created by ayi.zty on 2016/2/22.
 */
public class TestMain {
    public static void main(String[] args) {
        Vector<Thread> threads = new Vector<Thread>();
        for (int i = 0; i < 10; i++) {
            final int j = i;
            Thread iThread = new Thread(new Runnable() {
                public void run() {

                    try {
                        if (0 == j) {
                            Thread.sleep(10000);
                        } else {
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                    }
                    System.out.println("sub thread" + Thread.currentThread() + "finish");

                }
            });

            threads.add(iThread);
            iThread.start();
        }

        int i = 0;
        for (Thread iThread : threads) {
            try {
                final int j = i;
                iThread.join();
                System.out.println("join finish " + j);
                ++i;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("main thread");
    }

}
