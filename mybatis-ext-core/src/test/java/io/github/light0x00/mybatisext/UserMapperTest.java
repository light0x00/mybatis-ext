package io.github.light0x00.mybatisext;

import io.github.light0x00.mybatisext.sql.DeleteCondition;
import io.github.light0x00.mybatisext.sql.InsertCondition;
import io.github.light0x00.mybatisext.sql.SelectCondition;
import io.github.light0x00.mybatisext.sql.UpdateCondition;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author light
 * @since 2022/2/16
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserMapperTest {

    String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;" +
            "INIT=CREATE SCHEMA IF NOT EXISTS TEST" +
            "\\;SET SCHEMA TEST" +
            "\\;runscript from 'classpath:db/schema.sql'" +
            "\\;runscript from 'classpath:db/data.sql'";

    SqlSession sqlSession;
    UserMapper userMapper;

    @BeforeAll
    public void init() {
        Environment environment = new Environment("development", new JdbcTransactionFactory(), new UnpooledDataSource(
                "org.h2.Driver", url, "root", "test"));
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(configuration);
        configuration.addMapper(UserMapper.class);
        sqlSession = sqlSessionFactory.openSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    @Test
    public void insert() {
        User user = new User();
        user.setPkId(10L);
        user.setName("Light");
        user.setEmail("light@163.com");
        user.setAge(20);
        userMapper.insert(user);
    }

    //    @Test
    public void insertIgnore() {
        User user = new User();
        user.setPkId(10L);
        user.setName("Light");
        user.setEmail("light@163.com");
        user.setAge(20);
        userMapper.insertIgnore(user);
    }

    @Test
    public void insertOnDupKey() {
        User user = new User();
        user.setPkId(1L);
        user.setName("Light");
        user.setEmail("light@163.com");
        user.setAge(20);
        userMapper.insertOnDupKey(user, new InsertCondition()
                .updateValueOnDupKey("name", "Light")
        );
    }

    @Test
    public void deleteById() {
        userMapper.deleteById(2L);
    }

    @Test
    public void delete() {
        userMapper.delete(new DeleteCondition().eq("name", "jack"));
    }

    @Test
    public void updateById() {
        User user = new User();
        user.setPkId(2L);
        user.setEmail("light@163.com");
        user.setAge(20);
        userMapper.updateById(user);
    }

    @Test
    public void update() {
        User user = new User();
        user.setEmail("light@163.com");
        user.setAge(20);
        userMapper.update(user, new UpdateCondition().eq("pk_id", 2));
    }

    @Test
    public void updateByCondition() {
        userMapper.updateByCondition(new UpdateCondition()
                .set("name", "Jack")
                .incr("age", 3)
                .where()
                .eq("pk_id", 1)
        );
    }

    @Test
    public void getById() {
        userMapper.getById(2L);
    }

    @Test
    public void select() {
        userMapper.select(new SelectCondition()
                .select("name", "email")
                .where()
                .eq("name", "light")
                .or()
                .like("email", "%gmail.com")
        );
    }

    @Test
    public void select2() {
        userMapper.select(new SelectCondition()
                .select("name", "email")
                .where()
                .like("email", "%gmail.com")
                .and()
                .nested(cond -> {
                    cond.eq("name", "light").or().gt("age", "18");
                })
        );
    }

    @Test
    public void selectMaps() {
        List<Map<String, Object>> maps = userMapper.selectMaps(new SelectCondition()
                .select("age","count(1) as number")
                .groupBy("age")
                .having(h -> h.gt("age", 20))
                .orderByClause("number desc")
        );
    }

    @Test
    public void selectOne() {
        userMapper.selectOne(new SelectCondition()
                .select("name", "email")
                .where().eq("pk_id", 1));
    }

    @Test
    public void selectCount() {
        userMapper.selectCount(new SelectCondition()
                .select("name", "email")
                .where().in("pk_id", 1, 2, 3));
    }

    @Test
    public void selectCursor() {
        try (Cursor<User> cursor = userMapper.selectCursor(new SelectCondition().ne("pk_id", 1))) {
            cursor.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void selectStreaming() {
        userMapper.selectStreaming(new SelectCondition().ne("pk_id", 1), new ResultHandler<User>() {
            @Override
            public void handleResult(ResultContext<? extends User> resultContext) {
                System.out.println(resultContext.getResultObject());
            }
        });
    }
}
