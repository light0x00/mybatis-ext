package com.light0x00.mybatisext.test;

import com.light0x00.mybatisext.BaseMapper;
import com.light0x00.mybatisext.BaseMapperSqlSourceProvider;
import com.light0x00.mybatisext.sql.WhereCondition;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.ResultHandler;

public interface UserMapper extends BaseMapper<User> {

    //    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectStreaming")
    @ResultType(User.class)
    void selectStreaming(WhereCondition condition, ResultHandler<User> handler);
}