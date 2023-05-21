![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.light0x00/mybatis-ext/badge.svg) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[中文](./README.md) | English

## What

It provies a set of built-in CRUD operations,to simplify redundant boilerplate code development.

The api it exports for building sql is fluent, graceful. Under the hood,it uses AST(abstract syntax tree) and Visitor
pattern to build sql source,which is also graceful.

## Getting Started

> ⚠️ It's required that mybatis version >= 3.5.1 https://github.com/mybatis/mybatis-3/releases/tag/mybatis-3.5.1

```xml

<dependency>
    <groupId>io.github.light0x00</groupId>
  <artifactId>mybatis-ext</artifactId>
  <version>0.0.7</version>
</dependency>
```

Make your `mapper` extend `io.github.light0x00.mybatisext.BaseMapper`，and specify your `entity` as the generic
parameterized type.

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

So far,the preparation is done. You are ready to use the CRUD operations provided by `BaseMapper`.

### Example 1,general query

```java
List<User> lst=userMapper.select(new SelectCondition()
				.where()
				.like("email","%gmail.com")
				.and()
				.nested(cond->cond.eq("name","light").or().gt("age","18")));
```

Equivalent to:

```sql
select * from test.user where email like '%gmail.com' and (name = 'light' or age > 18)
```

### Example 2,grouping and aggregation

```java
List<Map<String, Object>> maps = userMapper.selectMaps(new SelectCondition()
				.select("age", "count(1) as number")
				.groupBy("age")
				.having(cond -> cond.gt("age", 20))
				.orderByClause("number desc")
);
```

Equivalent to:

```sql
select age,count(1) as number from test.user group by age having age>20 order by number desc
```

### Example 3,cursor

```java
try (Cursor<User> cursor = userMapper.selectCursor(new SelectCondition().gt("age", 1))) {
	cursor.forEach(System.out::println);
} catch (IOException e) {
	e.printStackTrace();
}
```

### Example 4,increment and decrement

```java
userMapper.updateByCondition(new UpdateCondition()
        .incr("age", 3)
        .where()
        .eq("pk_id", 1)
);
```

TEquivalent to:

```sql
update test.user set age=age+3 where pk_id=1
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

## Condition to build sql

Some of the api are easy to understand. they receive an `id` or `entity` as parameter.

The others receive a `XXCondition` as parameter, to build complex sql.

- SelectCondition，being used to build `where condition`,and the `columns` part of select sql.
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
  Equivalent to:
  ```sql
	select name,email from test.user where age>20 or email like '%gmail.com' order by age desc
  ```

- UpdateCondition，being used to build `where condition`, and the `set` part of update sql.
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

- DeleteCondition，being used to build `where condition`
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

In the real world, The "sql where condition" could be very complex. The conjuction `and` and `or` may combine lots of
nested conditions. The following demonstrate how to build a complex condition using `SelectCondition` . (It's same for
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
