![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.light0x00/mybatis-ext/badge.svg) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

➡️ [English document](./README.en.md)

## 介绍

提供了一套通用的单表 CRUD 操作，简化冗余的模版代码开发。相对于同类产品的特点是， 不越界，只做最核心的事，因此不制造任何新的概念，无需任何配置，心智负担小。

另外，用户接口层面，其用于生成 sql 的 api 流畅合理且优雅；实现层面，采用了 visitor 模式 + AST 方案，也很优雅。

## 上手指南

> ⚠️ mybatis版本需 >= 3.5.1 https://github.com/mybatis/mybatis-3/releases/tag/mybatis-3.5.1

引入 mybatis-ext

```xml

<dependency>
    <groupId>io.github.light0x00</groupId>
    <artifactId>mybatis-ext</artifactId>
    <version>0.0.7</version>
</dependency>
```

然后让你的 mapper 继承 `io.github.light0x00.mybatisext.BaseMapper`，并把范型参数指定为实体类。

```java
public interface UserMapper extends BaseMapper<User> {

}
```

```java

@Data
@TableName(schema = "test")
public class User {
  @Column(primary = true)
  private Long pkId;
  private String name;
  private Integer age;
  private String email;
}
```

至此,准备工作就完成了,可以写个查询试试~

### 案例1,常规查询

```java
List<User> lst=userMapper.select(new SelectCondition()
        .where()
        .like("email","%gmail.com")
        .and()
        .nested(cond->cond.eq("name","light").or().gt("age","18")));
```

等价于

```sql
select *
from test.user
where email like '%gmail.com'
  and (name = 'light' or age > 18)
```

### 案例2,分组聚合

```java
List<Map<String, Object>>maps=userMapper.selectMaps(new SelectCondition()
        .select("age","count(1) as number")
        .groupBy("age")
        .having(cond->cond.gt("age",20))
        .orderByClause("number desc")
        );
```

等价于:

```sql
select age, count(1) as number
from test.user
group by age
having age > 20
order by number desc
```

### 案例3,游标

```java
try(Cursor<User> cursor=userMapper.selectCursor(new SelectCondition().gt("age",1))){
        cursor.forEach(System.out::println);
        }catch(IOException e){
        e.printStackTrace();
        }
```

### 案例4,自增

```java
userMapper.updateByCondition(new UpdateCondition()
        .incr("age",3)
        .where()
        .eq("pk_id",1)
        );
```

等价于:

```sql
update test.user
set age=age + 3
where pk_id = 1
```

## API

BaseMapper 提供了如下 CRUD 操作:

### C

- `int insert(T entity)`；
- `int insertIgnore(T entity)`
- `int insertOnDupKey(T entity, InsertCondition condition)`

### R

- `T getById(Serializable id)`
- `List<T> select(SelectCondition condition)`
- `T selectOne(SelectCondition condition)`
- `long selectCount(SelectCondition condition)`
- `Cursor<T> selectCursor(SelectCondition condition)`
- `List<Map<String, Object>> selectMaps(SelectCondition condition)`

### U

- `int updateById(T entity)`
- `int update(T entity, UpdateCondition condition)`
- `int updateByCondition(UpdateCondition condition)`

### D

- `int deleteById(Serializable id)`
- `int delete(DeleteCondition condition)`

## 构建条件

这些 CRUD 操作按接收参数的不同可分为两类，一类是接收 ID 或 实体类的,这类使用较为简单,就不多赘述。

另一类则接收 XXCondition 对象，用于构建带有复杂条件的增删改查 sql, 使用方式如下:

- SelectCondition，用于构建 where 条件，要查询的列，分组聚合，排序
  ```java
	userMapper.select(new SelectCondition()
					.select("name", "email")
					.where()
					.gt("age", 20)
					.or()
					.like("email", "%gmail.com")
					.orderByClause("age desc")
	);
  ```
  等价于:
  ```sql
	select name,email from test.user where age>20 or email like '%gmail.com' order by age desc
  ```

- UpdateCondition，用于构建 where 条件，和 update 语句的 set 部分
  ```java
  userMapper.updateByCondition(new UpdateCondition()
                  .set("name", "Jack")
                  .incr("age", 3)
                  .where()
                  .eq("pk_id", 1)
  );
  ```
  等价于:

  ```sql
  update test.user set name='Jack',age=age+3 where pk_id=1
  ```

  你也可以用 `entity` 指定哪些列要被更新,对象的非空属性会被更新.

  ```java
  User user = new User();
      user.setEmail("light@163.com");
      user.setAge(20);
  userMapper.update(user, new UpdateCondition().eq("pk_id", 2));
  ```


- DeleteCondition，用于构建 where 条件
  ```java
  userMapper.delete(new DeleteCondition().in("name", "alice","bob"));
  ```
  等价于:
  ```sql
  delete from user where name in ("alice","bob")
  ```

- InsertCondition，目前用于构建 insert on duplicates 语句（mysql 独有）
  ```java
  User user = new User();
  user.setPkId(1L);
  user.setName("light");
  user.setEmail("light@foo.com");
  user.setAge(2);

  userMapper.insertOnDupKey(user, new InsertCondition()
              .updateValueOnDupKey("name", "light2")
  ```

  等价于:

  ```sql
  insert into test.user ( pk_id, name, age, email ) values( 1L, "light", 2, "light@foo.com" ) ON DUPLICATE KEY UPDATE name="light2"
  ```

### 构建复杂Where条件

真实世界里,我们的 sql 的 where 条件是可能很复杂的，由 and 和 or 连接的条件是可以嵌套的.如下演示了不同条件逻辑组合，是如何使用 `SelectCondition` 对象构建的(其他两个`UpdateCondition`
和 `DeleteCondition`也是相同的用法):

- c1 = v1 and c2 = v2
  ```java
  new SelectCondition().eq("c1", "v1").and().eq("c2", "v2")
  ```
- c1 = v1 and (c2= v2 or c3 = v3)
  ```java
  new SelectCondition()
              .eq("c1", "v1")
              .andNested(
                      (nested) -> nested.eq("c2", "v2").or().eq("c3", "v3")
              )
  ```
- c1 = v1 or c2 = v2
  ```java
  new SelectCondition().eq("c1", "v1").or().eq("c2", "v2");
  ```
- c1 = v1 or (c2 = v2 and c3 = v3)
  ```java
  new SelectCondition()
              .eq("c1", "v1")
              .orNested(
                      (nested) -> nested.eq("c2", "v2").and().eq("c3", "v3")
              )
  ```
- (c1 = v1 and c2 = v2) or (c3 = v3 and c4 = v4)
  ```java
   new SelectCondition()
              .nested(nested -> nested.eq("c1", "v1").and().eq("c2", "v2"))
              .or()
              .nested(nested -> nested.eq("c3", "v3").and().eq("c4", "v4"))
  ```
- (c1 = v1 and (c2 = v2 or c3 = v3 )) or (c4 = v4 and (c5 = v5 or c6 = v6))
  ```java
  new SelectCondition()
              .nested(cond -> cond
                      .eq("c1", "v1")
                      .andNested(
                              subCond -> subCond.eq("c2", "v2").or().eq("c3", "v3")
                      ))
              .or()
              .nested(cond -> cond
                      .eq("c4", "v4")
                      .andNested(
                              subCond -> subCond.eq("c5", "v5").or().eq("c6", "v6")
                      )
              )
  ```