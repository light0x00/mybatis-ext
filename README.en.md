![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.light0x00/mybatis-ext/badge.svg) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## What

It provies a set of built-in CRUD operations,to simplify redundant template code development.

The api it exports for building sql is fluent, graceful. Under the hood,it uses AST(abstract syntax tree) and Visitor
pattern to build sql source,which is also graceful.

## Getting Started

```xml

<dependency>
    <groupId>io.github.light0x00</groupId>
    <artifactId>mybatis-ext</artifactId>
    <version>0.0.4</version>
</dependency>
```

Make your `mapper` extends `io.github.light0x00.mybatisext.BaseMapper`，and specify your `enitty` as the parameterized
type.

```java
public interface UserMapper extends BaseMapper<User> {

}
```

So far,the parts of configuration is done. You are ready to use the CRUD operations provided by `BaseMapper`.

```java
List<User> lst=userMapper.select(new SelectCondition()
        .select("name","email")
        .where()
        .like("email","%gmail.com")
        .and()
        .nested(cond->{
        cond.eq("name","light").or().gt("age","18");
        })
        );

```

The aboving code is equivalent to:

```sql
select name, email
from test.user
where email like '%gmail.com'
  and (name = 'light' or age > 18)
```

## API

`BaseMapper` provides the following CRUD operations:

### C

- `int insert(T entity)`；
- `int insertIgnore(T entity)`
- `int insertOnDupKey(T entity, InsertCondition condition)`

### R

- `T getById(Serializable id)`
- `List<T> select(SelectCondition condition)`
- `T selectOne(SelectCondition condition)`
- `List<T> selectCount(SelectCondition condition)`
- `Cursor<T> selectCursor(SelectCondition condition)`

### U

- `int updateById(T entity)`
- `int update(T entity, UpdateCondition condition)`
- `int updateByCondition(UpdateCondition condition)`

### D

- `int deleteById(Serializable id)`
- `int delete(DeleteCondition condition)`

## Condition to build sql

Some of the api are easy to understand. they receive an `id` or `entity` as parameter.

The others receive a `XXCondition` as parameter, to build complex sql.

- SelectCondition，used to build `where condition`,and the columns to select.
  ```java
  userMapper.select(new SelectCondition()
              .select("name", "email")
              .where()
              .eq("name", "light")
              .or()
              .like("email", "%gmail.com"));
  ```
  Equivalent to:
  ```
  select name,email from test.user where name='light' or email like '%gmail.com'
  ```

- UpdateCondition，used to build `where condition`, and the `set` part of update sql.
  ```java
  userMapper.updateByCondition(new UpdateCondition()
                  .set("name", "Jack")
                  .incr("age", 3)
                  .where()
                  .eq("pk_id", 1)
  );
  ```
  Equivalent to:

  ```sql
  update test.user set name='Jack',age=age+3 where pk_id=1
  ```

  Also,you can use a entity to specify which column to be updated. The non-null property of an entity will effect
  the `set` part of update sql.

  ```java
  User user = new User();
      user.setEmail("light@163.com");
      user.setAge(20);
  userMapper.update(user, new UpdateCondition().eq("pk_id", 2));
  ```

- DeleteCondition，used to build `where condition`
  ```java
  userMapper.delete(new DeleteCondition().in("name", "alice","bob"));
  ```
  Equivalent to:
  ```sql
  delete from user where name in ("alice","bob")
  ```

- InsertCondition，at present, it only used to build sql synax `insert on duplicates` （Only supported by mysql ）
  ```java
  User user = new User();
  user.setPkId(1L);
  user.setName("light");
  user.setEmail("light@foo.com");
  user.setAge(2);

  userMapper.insertOnDupKey(user, new InsertCondition()
              .updateValueOnDupKey("name", "light2")
  ```

  Equivalent to:

  ```sql
  insert into test.user ( pk_id, name, age, email ) values( 1L, "light", 2, "light@foo.com" ) ON DUPLICATE KEY UPDATE name="light2"
  ```

  ### Build Complex Sql Where Condition

In the real world, The sql where condition could be very complex. The conjuction `and` and `or` may combine lots of
nested conditions. The following demonstrate how to build an complex condition using `SelectCondition` . (It's same for
both of `UpdateCondition` and `DeleteCondition`)

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