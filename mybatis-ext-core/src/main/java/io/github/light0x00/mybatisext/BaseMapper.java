package io.github.light0x00.mybatisext;

import io.github.light0x00.mybatisext.sql.DeleteCondition;
import io.github.light0x00.mybatisext.sql.InsertCondition;
import io.github.light0x00.mybatisext.sql.SelectCondition;
import io.github.light0x00.mybatisext.sql.UpdateCondition;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author light
 * @since 2022/2/18
 */
public interface BaseMapper<T> {

    @InsertProvider(type = BaseMapperSqlSourceProvider.class, method = "insert")
    @Lang(XMLLanguageDriver.class)
    int insert(T entity);

    /**
     * Only supported by MySql.
     */
    @InsertProvider(type = BaseMapperSqlSourceProvider.class, method = "insertIgnore")
    @Lang(XMLLanguageDriver.class)
    int insertIgnore(T entity);

    /**
     * Only supported by MySql.
     */
    @InsertProvider(type = BaseMapperSqlSourceProvider.class, method = "insertOnDupKey")
    @Lang(XMLLanguageDriver.class)
    int insertOnDupKey(T entity, InsertCondition condition);

    @DeleteProvider(type = BaseMapperSqlSourceProvider.class, method = "deleteById")
    @Lang(XMLLanguageDriver.class)
    int deleteById(Serializable id);

    @UpdateProvider(type = BaseMapperSqlSourceProvider.class, method = "delete")
    @Lang(XMLLanguageDriver.class)
    int delete(DeleteCondition condition);

    @UpdateProvider(type = BaseMapperSqlSourceProvider.class, method = "updateById")
    @Lang(XMLLanguageDriver.class)
    int updateById(T entity);

    @UpdateProvider(type = BaseMapperSqlSourceProvider.class, method = "update")
    @Lang(XMLLanguageDriver.class)
    int update(T entity, UpdateCondition condition);

    /**
     * @param condition The condition to build the sql.It must be specified as type of {@link UpdateCondition}.
     */
    @UpdateProvider(type = BaseMapperSqlSourceProvider.class, method = "updateByCondition")
    @Lang(XMLLanguageDriver.class)
    int updateByCondition(UpdateCondition condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "getById")
    @Lang(XMLLanguageDriver.class)
    T getById(Serializable id);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "select")
    @Lang(XMLLanguageDriver.class)
    List<T> select(SelectCondition condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectMaps")
    @Lang(XMLLanguageDriver.class)
    List<Map<String, Object>> selectMaps(SelectCondition condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectOne")
    @Lang(XMLLanguageDriver.class)
    T selectOne(SelectCondition condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectCount")
    @Lang(XMLLanguageDriver.class)
    long selectCount(SelectCondition condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectCursor")
    @Lang(XMLLanguageDriver.class)
    Cursor<T> selectCursor(SelectCondition condition);

}
