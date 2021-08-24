package com.lyw;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author liyawei
 * @Date 2021/8/24-下午2:55
 */
public class CuratorLockDemo {

  public static void main(String[] args) {
    InterProcessLock lock1 = new InterProcessMutex(getCuratorFramework(), "/locks");
    InterProcessLock lock2 = new InterProcessMutex(getCuratorFramework(), "/locks");

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          lock1.acquire(); // 获取到锁
          System.out.println("线程1 获取到了锁");

          lock1.acquire();
          System.out.println("线程1 再次获取到了锁");

          Thread.sleep(5000);

          lock1.release(); // 释放锁
          System.out.println("线程1 释放锁");

          lock1.release();
          System.out.println("线程1 再次释放所");

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          lock2.acquire(); // 获取到锁
          System.out.println("线程2 获取到了锁");

          lock2.acquire();
          System.out.println("线程2 再次获取到了锁");

          Thread.sleep(5000);

          lock2.release(); // 释放锁
          System.out.println("线程2 释放锁");

          lock2.release();
          System.out.println("线程2 再次释放所");

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private static CuratorFramework getCuratorFramework() {

  // 3秒中之后重试3次
    ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 3);

    CuratorFramework client = CuratorFrameworkFactory.builder().connectString("192.168.169.7:2181,192.168.169.8:2181,192.168.169.9:2181")
            .connectionTimeoutMs(2000)
            .sessionTimeoutMs(2000)
            // 间隔多少秒之后进行下一次的重试次数
            .retryPolicy(retry).build();

    // 启动客户端
    client.start();
    System.out.println("zookeeper 启动成功！");

    return client;
  }
}
