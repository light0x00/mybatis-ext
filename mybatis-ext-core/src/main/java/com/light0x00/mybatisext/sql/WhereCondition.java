
package com.light0x00.mybatisext.sql;

import com.light0x00.mybatisext.toolkit.StringUtils;

import java.util.function.Consumer;

/**
 * @author light
 * @since 2022/2/20
 */
class WhereCondition<R extends ConditionBuilder<R>> extends ConditionBuilder<R> {

    public String getSqlWhere(String paramSymbol) {
        String sqlCondition = getSqlCondition(paramSymbol);
        return StringUtils.isBlank(sqlCondition) ? "" : "where " + sqlCondition;
    }

    public String getSqlWhere() {
        return getSqlWhere("");
    }

    public R where() {
        return thisAsR();
    }

    public R where(Consumer<WhereCondition<R>> whereConditionConsumer) {
        whereConditionConsumer.accept(this);
        return thisAsR();
    }
}
