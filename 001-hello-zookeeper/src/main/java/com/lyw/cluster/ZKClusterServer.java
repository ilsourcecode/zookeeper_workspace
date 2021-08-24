package com.lyw.cluster;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @author liyawei
 * @Date 2021/8/20-上午11:44
 * zk 服务器注册到 zk 集群中
 */
public class ZKClusterServer {

  private String connectString = "192.168.169.7:2181,192.168.169.8:2181,192.168.169.9:2181";
  private int sessionTimeout = 2000;
  private ZooKeeper zooKeeper = null;

  public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
    ZKClusterServer server = new ZKClusterServer();

    // 获取 ZK 链接
    server.getConnect();

    // 注册服务器到 zk 集群
    server.register(args[0]);

    // 启动业务逻辑 （这里用睡觉模拟）
    server.business();
  }

  private void business() {
    try {
      Thread.sleep(Long.MAX_VALUE);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void register(String hostname) throws KeeperException, InterruptedException {
    String create = zooKeeper.create("/servers/" + hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    System.out.println(hostname + " -已上线");
  }

  private void getConnect() throws IOException {
    zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
      @Override
      public void process(WatchedEvent watchedEvent) {

      }
    });
  }
}
