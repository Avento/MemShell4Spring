![MemShell4Spring logo picture](https://laughing-markdown-pics.oss-cn-shenzhen.aliyuncs.com/20231227204819.png)

# Memory Webshell for Spring Web

适用于 Spring Web 的内存马。

## 🐎 Webshell 概览

1. HandlerMethodShell
2. ControllerHandlerShell
3. SimpleUrlHandlerMappingShell
4. HttpRequestHandlerAdapterShell
5. SimpleServletHandlerAdapterShell
6. WelcomePageHandlerMappingShell
7. HandlerMappingShell
8. HandlerAdapterShell
9. MultipartResolverDelegateShell
10. HandlerInterceptorShell
11. ViewResolverShell
12. HandlerExceptionResolverShell

## 🔍 具体说明

使用方法参考 Postman 配置文件。

## 👌 测试版本

| Tested |    JDK    |                         spring-boot                          |                       spring-framework                       |
| :----: | :-------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
|   ✔    |  JDK 17   | [3.1.5](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/3.1.5) | [6.0.13](https://github.com/spring-projects/spring-framework/tree/v6.0.13) |
|   ✔    |  JDK 17   | [3.2.0](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/3.2.0) | [6.1.1](https://github.com/spring-projects/spring-framework/tree/v6.1.1) |
|   ✔    | JDK 8_102 | [2.5.15](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/2.5.15) | [5.3.27](https://github.com/spring-projects/spring-framework/tree/v5.3.27) |

由于 Jakarta 命名空间的问题导致低版本（5.3.x）的 Spring Framework 使用本项目会出现 Jakarta 相关的报错。理论上本项目适配 6.0.x - 6.2.x 之后的版本。

> Java/Jakarta EE Versions and JDK Version Range
>
> - Spring Framework 6.2.x: Jakarta EE 9-11 (jakarta namespace): JDK 17-25 (expected)
> - Spring Framework 6.1.x: Jakarta EE 9-10 (jakarta namesp ace): JDK 17-23
> - Spring Framework 6.0.x: Jakarta EE 9-10 (jakarta namespace): JDK 17-21
> - Spring Framework 5.3.x: Java EE 7-8 (javax namespace): JDK 8-21 (as of 5.3.26)

所以假如需要在低于 5.3.x 的 Spring Framework 使用，修改 import 即可。

```java
< 5.3.x
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

> 5.3.x
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
```

## 📒 Todo

- [ ] 解决 低于 5.3.x 的报错问题；
- [ ] 完成无文件注入测试。