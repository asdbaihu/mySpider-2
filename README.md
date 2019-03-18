## mySpider

> 基于webmagic实现的网页爬虫工具

#### 配置文件application.yml

```yml
server:
  port: 9090

spring:
  #DB Configuration:
  datasource:
    driverClassName: com.mysql.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/crawler?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: root
  #JPA Configuration:
  jpa:
    database: mysql
    show-sql: true
    generate-ddl: true
    hibernate:
      ddl-auto: update

spider:
  url: 这里配置需要爬取的种子页面

```

