package com.lyw.distribute;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author liyawei
 * @Date 2021/8/24-上午8:35
 */
public class DistributeLock {

  private ZooKeeper zooKeeper;
  private final String connectString = "192.168.169.7:2181,192.168.169.8:2181,192.168.169.9:2181";
  private final int sessionTimeout = 2000;

  private String currentNode;

  // 监听前一个节点的变化
  private static String waitPath;

  private CountDownLatch connectLatch = new CountDownLatch(1);
  private CountDownLatch awaitLatch = new CountDownLatch(1);

  // 连接到 zookeeper 集群中
  public DistributeLock() throws KeeperException, InterruptedException, IOException {
    zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
      @Override
      public void process(WatchedEvent watchedEvent) {
        // connectLatch 如果连接上了需要释放
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
          connectLatch.countDown();
        }

        // awaitLatch 需要释放
        // 当前节点的类型 - 删除过了，并且当前的前一个节点路径等于原来节点的前一节点路径
        if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)) {
          awaitLatch.countDown();
        }
      }
    });

    // 等待 zk 集群正常连接后才会往下面走
    connectLatch.await();

    Stat status = zooKeeper.exists("/locks", false);

    // 如果不存在创建
    if (status == null) {

      zooKeeper.create("/locks", "locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
  }

  // 上锁
  // 上锁就是在 zookeeper 根目录下的 /locks路径下创建有序的临时节点
  public void zkLock() throws KeeperException, InterruptedException {
    // 创建对应的节点
    currentNode = zooKeeper.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

    Thread.sleep(10);

    // 判断当前节点是不是最小节点
    List<String> children = zooKeeper.getChildren("/locks", false);


    if (children.size() == 1) {

//      System.out.println("当前没有任何节点需要上锁");
      return;
    } else {
      Collections.sort(children);

      String currentNodeName = currentNode.substring("/locks/".length());
      int currentIndexOf = children.indexOf(currentNodeName);

      if (currentIndexOf == -1) {
        System.out.println("xxxxx");
      } else if (currentIndexOf == 0) {
        return;
      } else {
        // 获取当前节点的上一个节点的名称
        waitPath = "/locks/" + children.get(currentIndexOf - 1);

        // 监听当前上一个节点的变化
        zooKeeper.getData(waitPath, true, new Stat());

        // 等待一下
        awaitLatch.await();
        return;
      }
    }
  }

  // 释放锁
  public void unLock() throws KeeperException, InterruptedException {
    // 删除节点
    zooKeeper.delete(currentNode, -1);
  }
}
