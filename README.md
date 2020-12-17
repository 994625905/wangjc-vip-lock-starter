## 一、简介

- wangjc-vip-lock-starter是一个声明式锁的组件，提供了单节点锁LockSingle（JUC的lock接口）和分布式锁LockCloud（redis）的快速解决方案，将锁控业务逻辑统一管理，避免代码到处写的都是加锁解锁操作，并提供支持自定义的加锁失败策略和释放锁失败策略。

- PS：wangjc-vip-lock-starter相对于传统的写法，在性能上并没有什么大的优势，主要着手于提供简单的操作和规范统一的管理。为了精细化的控制，将需要上锁的业务抽成单独的方法，在该方法上添加对应注解。单节点锁：@LockSingle，分布式锁：@LockCloud。参考案例：https://gitee.com/baomidou/lock4j-spring-boot-starter.git

- ### 1、单节点锁

  主要是对ReentrantLock操作的封装，普遍采用tryLock来设置获取锁的超时时间，避免lock的长期阻塞（拖垮性能不可控）。但前者可能会加锁超时，进入定义好的加锁失败策略。所以假如并发量很大，又强制保证每一条线程都要成功执行业务，推荐设置acquireTimeout参数稍大一点，给每一条线程足够的重试时间，不然则有的加锁成功，有的失败（类似抢购，不一定都能抢到）

  ```java
  @Target({ElementType.METHOD}) // 只作用于方法
  @Retention(RetentionPolicy.RUNTIME)
  public @interface LockSingle {
  
      /**
       * 锁类型：可重入锁，读锁，写锁，公平锁
       * @return 默认为可重入锁
       */
      LockType type() default LockType.REENTRANT;
  
      /**
       * 锁名称
       * @return 默认为包名+方法名
       */
      String[] keys() default "";
  
      /**
       * 锁名称的构建器
       * @return
       */
      Class<? extends ILockKeyBuilderService> keyBuilder() default DefaultLockKeyBuilderServiceImpl.class;
  
      /**
       * 尝试获取锁的超时时间 单位：毫秒
       * @return 默认为3，
       */
      long acquireTimeout() default 3000;
  
      /**
       * 失败策略，默认是抛出异常
       * 获取锁：{@link DefaultLockFailureStrategyServiceImpl#lockFailure(Long, Integer)}
       * 释放锁：{@link DefaultLockFailureStrategyServiceImpl#releaseFailure(LockEntity)}
       * @return
       */
      Class<? extends ILockFailureStrategyService> lockFailureStrategy() default DefaultLockFailureStrategyServiceImpl.class;
  }
  
  ```

- #### 使用场景：

  单节点的项目部署，因为在同一台JVM里面，自身的lock接口可以满足一切的同步操作需求。

  ```java
      private int counter = 1;
  
      @LockSingle(type = LockType.READ)
      public void read(){
          System.out.println("普通锁：读锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      @LockSingle(type = LockType.WRITE,keys = "#key")
      public void write(String key){
          System.out.println("普通锁：写锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      @LockSingle(type = LockType.FAIR,keys = "#user.name")
      public void fair(User user){
          System.out.println("普通锁：公平锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      @LockSingle(keys = {"#user.name","#user.id"},acquireTimeout = 8000)
      public void reentrant(User user){
          System.out.println("普通锁：可重入锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
          try {
              /** 模拟占用时间 */
              Thread.sleep(2000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
  ```

- ### 2、分布式锁

  摒弃了zookeeper，只使用redis来做分布式锁，客户端类型为Spring提供RedisTemplate，或者官方提供的Redisson（默认此项）。相对于单节点锁，多了一个过期自动释放时间expire（默认30秒，过期时间要长于业务执行时间）
  PS：虽然都说的一种情况是，过期时间到了自动释放，但业务还未执行完，那么最容易发生的就是等业务执行完后，在finally代码块中手动把其他线程的锁给释放了，数据不安全。但这里不会，因为在分布式锁的释放时做了该锁是否属于当前线程的判断。

  ```java
  	@Target({ElementType.METHOD}) // 只作用于方法
  @Retention(RetentionPolicy.RUNTIME)
  public @interface LockCloud {
  
      /**
       * 分布式锁的客户端支持，
       * @return 默认是redisson
       */
      LockCloudClientType client() default LockCloudClientType.REDISSON;
  
      /**
       * 锁类型：可重入锁，读锁，写锁，公平锁
       * @return 默认为可重入锁，目前就redisson支持
       */
      LockType type() default LockType.REENTRANT;
  
      /**
       * 锁名称
       * @return 默认为包名+方法名
       */
      String[] keys() default "";
  
      /**
       * 锁名称的构建器
       * @return
       */
      Class<? extends ILockKeyBuilderService> keyBuilder() default DefaultLockKeyBuilderServiceImpl.class;
  
      /**
       * 锁的过期自动释放时间 单位：毫秒 （分布式锁必须设置自动释放时间，为0无效）
       * @return 默认为30，过期时间一定是要长于业务的执行时间，
       */
      long expire() default 30000;
  
      /**
       * 尝试获取锁的超时时间 单位：毫秒 （分布式锁必须设置自旋获取锁的超时时间，为0无效）
       * @return 默认为3，结合业务，建议该时间不宜设置过长，特别在并发高的情况下.
       */
      long acquireTimeout() default 3000;
  
      /**
       * 失败策略，默认是抛出异常
       * 获取锁：{@link DefaultLockFailureStrategyServiceImpl#lockFailure(Long, Integer)}
       * 释放锁：{@link DefaultLockFailureStrategyServiceImpl#releaseFailure(LockEntity)}
       * @return
       */
      Class<? extends ILockFailureStrategyService> lockFailureStrategy() default DefaultLockFailureStrategyServiceImpl.class;
  }
  
  ```

- #### 使用场景：

  分布式项目的部署，因为多台JVM之间存在隔离，自身API无法实现这种机器隔离的同步操作，只能借用第三方组件来控制，常用的就是redis。当然，还有zookeeper。

  ```java
      private int counter = 1;
  
      @LockCloud(type = LockType.READ)
      public void read(){
          System.out.println("分布式锁：读锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      @LockCloud(type = LockType.WRITE,keys = "#key")
      public void write(String key){
          System.out.println("分布式锁：写锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      @LockCloud(type = LockType.FAIR,keys = "#user.name")
      public void fair(User user){
          System.out.println("分布式锁：公平锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      /**
       * 设置获取锁超时时间 和过期失效时间
       * @param user
       */
      @LockCloud(keys = {"#user.name","#user.id"},acquireTimeout = 4000,expire = 4000)
      public void reentrant(User user){
          System.out.println("分布式锁：可重入锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
          try {
              /** 模拟占用 */
              Thread.sleep(2000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
  ```

------------

## 二、代码结构说明

- 包结构前缀统一为：vip.wangjc.lock。然后下面根据职责细分：注解，aop扫描，自动配置，key构建器，实体，异常，执行器，失败策略，工具……其中，key构建器，执行器，失败策略，都可支持自定义。自定义方式为实现对应的接口，然后在注解中指定具体的实现类。

- ### 结构如下：

  ```java
  ├─src
  	│  └─main
  	│      ├─java
  	│      │  └─vip
  	│      │      └─wangjc
  	│      │          └─lock
  	│      │              │  LockTemplate.java （锁的模板方法提供）
  	│      │              │
  	│      │              ├─annotation
  	│      │              │      LockCloud.java （分布式锁注解）
  	│      │              │      LockSingle.java （单节点锁注解）
  	│      │              │
  	│      │              ├─aop
  	│      │              │      LockCloudAnnotationAdvisor.java （分布式锁的aop通知）
  	│      │              │      LockCloudInterceptor.java （分布式锁的aop切面处理器）
  	│      │              │      LockSingleAnnotationAdvisor.java （普通锁的aop通知）
  	│      │              │      LockSingleInterceptor.java （普通锁的aop切面处理器）
  	│      │              │
  	│      │              ├─auto
  	│      │              │  └─configure
  	│      │              │          LockAutoConfiguration.java （框架的自动配置器）
  	│      │              │
  	│      │              ├─builder
  	│      │              │  └─service
  	│      │              │      │  ILockKeyBuilderService.java （锁的名称key生成器接口）
  	│      │              │      │
  	│      │              │      └─impl
  	│      │              │              DefaultLockKeyBuilderServiceImpl.java （默认的key生成器）
  	│      │              │
  	│      │              ├─entity
  	│      │              │      LockCloudClientType.java （枚举：分布式锁的客户端类型，这里放弃了zookeeper）
  	│      │              │      LockCloudEntity.java （分布式锁的实体类，加强版，提供过期时间和尝试获取次数）
  	│      │              │      LockEntity.java （锁的实体类）
  	│      │              │      LockType.java （枚举：锁的类型，提供可重入锁，读锁，写锁，公平锁）
  	│      │              │
  	│      │              ├─exception
  	│      │              │      LockException.java （自定义锁的异常）
  	│      │              │      LockFailureException.java （失败策略的异常类）
  	│      │              │
  	│      │              ├─executor
  	│      │              │  │  LockExecutorFactory.java （锁执行器的构建工厂）
  	│      │              │  │
  	│      │              │  ├─pool
  	│      │              │  │      LockSinglePool.java （单节点锁的锁存放池）
  	│      │              │  │
  	│      │              │  └─service
  	│      │              │      │  ILockExecutorService.java （锁的核心处理器接口）
  	│      │              │      │
  	│      │              │      └─impl
  	│      │              │              RedissonFairLockExecutorServiceImpl.java （分布式锁的公平锁（Redisson支持））
  	│      │              │              RedissonReadLockExecutorServiceImpl.java （分布式锁的读锁（Redisson支持））
  	│      │              │              RedissonReentrantLockExecutorServiceImpl.java （分布式锁的可重入锁（Redisson支持））
  	│      │              │              RedissonWriteLockExecutorServiceImpl.java （分布式锁的写锁（Redisson支持））
  	│      │              │              RedisTemplateLockServiceImpl.java （分布式锁原生RedisTemplate处理器）
  	│      │              │              SingleFairLockExecutorServiceImpl.java （单节点的公平锁执行器）
  	│      │              │              SingleReadLockExecutorServiceImpl.java （单节点的读锁执行器）
  	│      │              │              SingleReentrantLockExecutorServiceImpl.java （单节点的可重入锁执行器）
  	│      │              │              SingleWriteLockExecutorServiceImpl.java （单节点的写锁执行器）
  	│      │              │
  	│      │              ├─failureStrategy
  	│      │              │  └─service
  	│      │              │      │  ILockFailureStrategyService.java （获取锁/释放锁的失败策略接口）
  	│      │              │      │
  	│      │              │      └─impl
  	│      │              │              DefaultLockFailureStrategyServiceImpl.java （默认的失败策略）
  	│      │              │
  	│      │              └─util
  	│      │                      LockUtil.java （锁的工具类，获取当前JVM的进程ID，获取本机网卡地址）
  	│      │
  	│      └─resources
  	│          └─META-INF
  	│                  spring.factories （自动化配置）
  	│
  
  ```
- UML图如下：
  ![类图](http://www.wangjc.vip/group1/M00/00/01/rBAAD1_YGq2AAbTHAAMB0G6sCAI556.png "类图")

------------

## 三、使用方式

- ### 1、添加依赖

  把项目以maven的方式打成jar包上传提交到GitHub的中央仓库，步骤繁琐，暂时没做这一步（后期考虑），所以使用的话直接git clone下来，然后在具体的项目中引入即可
    	

  ```xml
  		<!--spring redis template的提供-->
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-data-redis</artifactId>
    			<optional>true</optional>
    		</dependency>
  
    		<!--redis官方提供的分布式锁-->
    		<dependency>
    			<groupId>org.redisson</groupId>
    			<artifactId>redisson-spring-boot-starter</artifactId>
    			<version>${redisson.version}</version>
    			<optional>true</optional>
    		</dependency>
  
    		<dependency>
    			<groupId>vip.wangjc</groupId>
    			<artifactId>wangjc-vip-lock-starter</artifactId>
    			<version>1.0-SNAPSHOT</version>
    		</dependency>
  ```

  ​	

- ### 2、配置文件

  ```properties
  spring.redis.host=127.0.0.1
  #需要密码的话，打开下方注解
  #spring.redis.password=697295
  spring.redis.timeout=5000
  spring.redis.port=6379
  spring.redis.database=3
  ```

  

- ### 3、使用注解

  为了精细化的控制锁的粒度，将核心业务代码抽成单独的方法，然后在对应方法上，添加锁注解

  ```java
      private int counter = 1;
  
      @LockCloud(type = LockType.READ)
      public void read(){
          System.out.println("分布式锁：读锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      @LockCloud(type = LockType.WRITE,keys = "#key")
      public void write(String key){
          System.out.println("分布式锁：写锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      @LockCloud(type = LockType.FAIR,keys = "#user.name")
      public void fair(User user){
          System.out.println("分布式锁：公平锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
      }
  
      /**
       * 设置超时机制和过期失效时间
       * @param user
       */
      @LockCloud(keys = {"#user.name","#user.id"},acquireTimeout = 4000,expire = 4000)
      public void reentrant(User user){
          System.out.println("分布式锁：可重入锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
          try {
              /** 模拟占用 */
              Thread.sleep(2000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
      /**
       * 自定义key构建器和失败策略
       * @param user
       */
      @LockCloud(keyBuilder = TestLockKeyBuilder.class,lockFailureStrategy = FailStrategyTest.class)
      public void reentrant2(User user){
          System.out.println("分布式锁：可重入锁，当前线程："+Thread.currentThread().getName()+"，counter："+ counter++);
          try {
              /** 模拟占用 */
              Thread.sleep(2000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  
  ```

  
