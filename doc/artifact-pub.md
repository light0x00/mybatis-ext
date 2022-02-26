1. 注册账号,https://issues.sonatype.org/secure/Signup!default.jspa
2. 申请 ticket ,https://issues.sonatype.org/secure/CreateIssue.jspa?pid=10134&issuetype=21
   > 本质是申请 groupId,如果没有域名,则可用 io.github.用户名。
3. pom.xml 规范化，如 Name, Description and URL，License 等信息
   > 参考 https://central.sonatype.org/publish/requirements/#supply-javadoc-and-sources
4. 配置 pom.xml，setting.xml 等
   > 大概是配置发布插件，nexus 服务器认证，签名相关的信息， 参考 https://central.sonatype.org/publish/requirements/#license-information
5. 执行发布（如 mvn deploy -P ossrh），可登录 https://s01.oss.sonatype.org/ 验证。
   > 第一次发布时，artifact 可能被发布在一个临时的 nexus 仓库（而不是 Central），需要手动做一些操作,具体可参考 https://central.sonatype.org/publish/release/ 