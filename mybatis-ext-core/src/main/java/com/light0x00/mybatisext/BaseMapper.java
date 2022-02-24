package com.light0x00.mybatisext;

import com.light0x00.mybatisext.sql.InsertCondition;
import com.light0x00.mybatisext.sql.UpdateCondition;
import com.light0x00.mybatisext.sql.WhereClause;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.cursor.Cursor;

import java.io.Serializable;
import java.util.List;

/**
 * @author light
 * @since 2022/2/18
 */
public interface BaseMapper<T> {

    @InsertProvider(type = BaseMapperSqlSourceProvider.class, method = "insert")
    int insert(T entity);

    /**
     * @implNote Only supported by MySql.
     */
    @InsertProvider(type = BaseMapperSqlSourceProvider.class, method = "insertIgnore")
    int insertIgnore(T entity);

    /**
     * @implNote Only supported by MySql.
     */
    @InsertProvider(type = BaseMapperSqlSourceProvider.class, method = "insertOnDupKey")
    int insertOnDupKey(T entity, InsertCondition condition);

    @DeleteProvider(type = BaseMapperSqlSourceProvider.class, method = "deleteById")
    int deleteById(Serializable id);

    @UpdateProvider(type = BaseMapperSqlSourceProvider.class, method = "delete")
    int delete(WhereClause condition);

    @UpdateProvider(type = BaseMapperSqlSourceProvider.class, method = "updateById")
    int updateById(T entity);

    @UpdateProvider(type = BaseMapperSqlSourceProvider.class, method = "update")
    int update(T entity, WhereClause condition);

    /**
     * @param condition The condition to build the sql.It must be specified as type of {@link UpdateCondition}.
     */
    @UpdateProvider(type = BaseMapperSqlSourceProvider.class, method = "updateByCondition")
    int updateByCondition(WhereClause condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "getById")
    T getById(Serializable id);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "select")
    List<T> select(WhereClause condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectOne")
    T selectOne(WhereClause condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectCount")
    List<T> selectCount(WhereClause condition);

    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectCursor")
    Cursor<T> selectCursor(WhereClause condition);

//    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectPage")
//    List<T> selectPage(WhereCondition where);


}
