package com.lyw.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author liyawei
 * @Date 2021/8/18-下午3:07
 */
public class ZkClient {

  private ZooKeeper keeper;
  private String connectString = "192.168.169.7:2181";
  private int sessionTimeout = 2000;

  @Before
  public void init() throws IOException {
    keeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
      @Override
      /***
       * 实时监听节点的变化
       */
      public void process(WatchedEvent watchedEvent) {
        System.out.println("-------------------------start");
        List<String> children = null;
        try {
          children = keeper.getChildren("/", true);
          for (String child : children) {
            System.out.println(child);
          }
          System.out.println("-------------------------end");
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Test
  public void create() throws KeeperException, InterruptedException {
    /***
     *  create 方法的参数
     *    - /atguigu 当前路径创建一个 atguigu
     *    - "ss.avi".getBytes() 当前节点的内容
     *    - ZooDefs.Ids.OPEN_ACL_UNSAFE 当前节点的访问权限，任何人都可以看
     *    - CreateMode.PERSISTENT) 节点两大类型中的永久无序
     */
    String nodeCreated = keeper.create("/atguigu", "ss.avi".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

  }

  @Test
  /***
   * 获取当前节点的自节点并打印
   */
  public void getChildren() throws KeeperException, InterruptedException {
    List<String> children = keeper.getChildren("/", true);
    for (String child : children) {
      System.out.println(child);
    }

    Thread.sleep(Long.MAX_VALUE);
  }

  @Test
  public void exist() throws KeeperException, InterruptedException {
    Stat exists = keeper.exists("/atguigu", false);
    System.out.println(exists == null ? "not exist" : "exist");
  }

  @Test
  public void test1() {
    System.out.println(new Date());
  }
}
