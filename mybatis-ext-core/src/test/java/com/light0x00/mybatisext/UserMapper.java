package com.light0x00.mybatisext;

import com.light0x00.mybatisext.sql.SelectCondition;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.ResultHandler;

public interface UserMapper extends BaseMapper<User> {

    //    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
    @SelectProvider(type = BaseMapperSqlSourceProvider.class, method = "selectStreaming")
    @ResultType(User.class)
    void selectStreaming(SelectCondition condition, ResultHandler<User> handler);
}