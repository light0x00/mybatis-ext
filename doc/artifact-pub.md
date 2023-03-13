官方文档： https://central.sonatype.org/publish/publish-guide/

---

1. 注册账号,https://issues.sonatype.org/secure/Signup!default.jspa
2. 申请 ticket ,https://issues.sonatype.org/secure/CreateIssue.jspa?pid=10134&issuetype=21
   > 本质是申请 groupId,只有groupId的所有者才能上传制品库到central。groupId 需要用域名来验证，如果没有域名,则可用 io.github.用户名。
3. pom.xml 规范化，如 Name, Description and URL，License 等信息
   > 参考 https://central.sonatype.org/publish/requirements/#supply-javadoc-and-sources
4. 配置 pom.xml，setting.xml 等
   > 大概是配置发布插件，nexus 服务器认证，制品库签名相关的信息， 参考 https://central.sonatype.org/publish/requirements/#license-information
5. 执行发布指令（如 mvn deploy -P ossrh），可登录 https://s01.oss.sonatype.org/ 验证。
   > artifact 可能被发布在一个临时的 nexus 仓库（而不是 Central）,需要手动做一些操作,具体可参考 https://central.sonatype.org/publish/release/

以上步骤完成后,半小时内同步到 central 仓库, 4小时后能在 https://search.maven.org 搜索

> 验证是否同步到 central，可访问： https://repo1.maven.org/maven2/io/github/light0x00/mybatis-ext-parent/0.0.4/mybatis-ext-parent-0.0.4.pom

发布指令

- mvn versions:set -DnewVersion=0.0.3
- mvn versions:revert 恢复之前版本
- mvn versions:commit 提交当前版本（删除备份文件）
- export GPG_TTY=$(tty) && mvn clean deploy -P ossrh,release 发布到临时仓库
- mvn nexus-staging:release -P release 发布到 central 仓库

> 9 Activate Central Sync
> The first time you promote a release, you need to comment on the OSSRH JIRA ticket you created in Section 3 so we can know you are ready to be synced. We will review your promoted artifacts. If no problem found, we will activate Central Sync for you and close your JIRA ticket.
> After Central Sync is activated, your future promotion will be synced automatically. The sync process runs roughly every 2 hours.

最佳工程化实践

1. Develop, develop, develop
2. Commit any outstanding changes
3. Verify build passes
4. Update versions to release version
5. Commit release version
6. Run deployment
7. Update versions to next snapshot version
8. Commit new snapshot version
9. Develop, develop, develop and rinse and repeat

setting.xml

```xml
<!-- 上传jar包配置 -->
<profiles>
   <profile>
      <id>ossrh</id>
      <activation>
         <activeByDefault>true</activeByDefault>
      </activation>
      <!--事先用 gpg 生成密钥，用于对制品库数字签名，确保只有拥有私钥的人才能上传包 -->
      <properties>
         <gpg.keyname>0x9A3EDB42</gpg.keyname>
         <gpg.executable>gpg</gpg.executable>
         <gpg.passphrase><![CDATA[密钥]]></gpg.passphrase>
      </properties>
   </profile>
</profiles>
<servers>
<!--事先在中央仓库注册账号-->
<server>
   <id>ossrh</id>
   <username>light0x00</username>
   <password><![CDATA[中央仓库账号密码]]></password>
</server>
</servers>
```

```xml

<project>
   <name>${project.groupId}:${project.artifactId}</name>
   <!-- 项目介绍  -->
   <description>A tool to visualize binary in ascii format.</description>
   <!--  项目地址 -->
   <url>https://github.com/light0x00/binary-tree-printer</url>

   <scm>
      <!--项目地址-->
      <connection>scm:git:git://github.com/light0x00/to-be-graceful.git</connection>
      <developerConnection>scm:git:https://github.com/light0x00/to-be-graceful.git</developerConnection>
      <url>https://github.com/light0x00/to-be-graceful/tree/master</url>
   </scm>

   <licenses>
      <license>
         <name>MIT License</name>
         <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
   </licenses>

   <developers>
      <developer>
         <name>light0x00</name>
         <email>light0x00@163.com</email>
         <organization>individual</organization>
         <organizationUrl>https://github.com/light0x00</organizationUrl>
      </developer>
   </developers>

   <properties>
      <maven.compiler.source>8</maven.compiler.source>
      <maven.compiler.target>8</maven.compiler.target>
      <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
   </properties>

   <distributionManagement>
      <snapshotRepository>
         <id>ossrh</id>
         <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
      </snapshotRepository>
   </distributionManagement>

   <profiles>
      <profile>
         <id>release</id>
         <build>
            <plugins>
               <plugin>
                  <groupId>org.sonatype.plugins</groupId>
                  <artifactId>nexus-staging-maven-plugin</artifactId>
                  <version>1.6.7</version>
                  <extensions>true</extensions>
                  <configuration>
                     <serverId>ossrh</serverId>
                     <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
                     <autoReleaseAfterClose>true</autoReleaseAfterClose>
                  </configuration>
               </plugin>

               <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-gpg-plugin</artifactId>
                  <version>1.5</version>
                  <executions>
                     <execution>
                        <id>sign-artifacts</id>
                        <phase>verify</phase>
                        <goals>
                           <goal>sign</goal>
                        </goals>
                        <configuration>
                           <keyname>${gpg.keyname}</keyname>
                           <passphrase>${gpg.passphrase}</passphrase>
                           <executable>${gpg.executable}</executable>
                        </configuration>
                     </execution>
                  </executions>
               </plugin>

               <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-source-plugin</artifactId>
                  <version>2.2.1</version>
                  <executions>
                     <execution>
                        <id>attach-sources</id>
                        <goals>
                           <goal>jar-no-fork</goal>
                        </goals>
                     </execution>
                  </executions>
               </plugin>
               <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-javadoc-plugin</artifactId>
                  <version>2.9.1</version>
                  <!--关掉 warning-->
                  <configuration>
                     <additionalJOption>-Xdoclint:none</additionalJOption>
                  </configuration>
                  <executions>
                     <execution>
                        <id>attach-javadocs</id>
                        <goals>
                           <goal>jar</goal>
                        </goals>
                     </execution>
                  </executions>
               </plugin>
            </plugins>
         </build>
      </profile>
   </profiles>
</project>
```