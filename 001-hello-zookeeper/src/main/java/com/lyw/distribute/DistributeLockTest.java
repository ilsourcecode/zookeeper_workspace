package com.lyw.distribute;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * @author liyawei
 * @Date 2021/8/24-上午11:13
 */
public class DistributeLockTest {

  public static void main(String[] args) throws InterruptedException, IOException, KeeperException {

    DistributeLock lock1 = new DistributeLock();
    DistributeLock lock2 = new DistributeLock();

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          lock1.zkLock();
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("lock1 正在上锁中。。。。");

        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        try {
          lock1.unLock();
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("lock1 释放锁成功了！");
      }
    }).start();

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          lock2.zkLock();
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("lock2 正在上锁中。。。。");

        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        try {
          lock2.unLock();
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("lock2 释放锁成功了！");
      }
    }).start();

  }
}
