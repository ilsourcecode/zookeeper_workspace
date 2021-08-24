package com.lyw.cluster;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liyawei
 * @Date 2021/8/20-下午4:17
 */
public class ZKCLusterClient {

  private String connectString = "192.168.169.7:2181,192.168.169.8:2181,192.168.169.9:2181";
  private int sessionTimeout = 2000;
  private ZooKeeper zooKeeper = null;

  public static void main(String[] args) throws IOException, KeeperException, InterruptedException {

    ZKCLusterClient client = new ZKCLusterClient();
    // 获取 zk 连接
    client.getConnect();

    // 监听 /servers 下面节点的增加/删除
    client.listener();

    // 其他业务逻辑
    client.business();

  }

  private void business() {
    try {
      Thread.sleep(Long.MAX_VALUE);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void listener() throws KeeperException, InterruptedException {
    /***
     *  第二个参数为 true 时，使用的是连接 Zookeeper 服务器提供的监听类，也可自己提供一个
     */
    /*zooKeeper.getChildren("/servers", new Watcher() {
      @Override
      public void process(WatchedEvent watchedEvent) {

      }
    });*/
    List<String> servers = new ArrayList<>();
    List<String> children = zooKeeper.getChildren("/servers", true);
    for (String child : children) {
      // 获取 /servsers/+child 路径， false 不监听当前节点， null 状态不查看
      byte[] data = zooKeeper.getData("/servers/" + child, false, null);
      servers.add(new String(data));
    }
    System.out.println(servers);
  }

  private void getConnect() throws IOException {
    zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
      @Override
      public void process(WatchedEvent watchedEvent) {
        try {
          listener();
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
