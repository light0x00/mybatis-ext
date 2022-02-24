package com.light0x00.mybatisext.sql;

import com.light0x00.mybatisext.toolkit.StringUtils;

/**
 * @author light
 * @since 2022/2/20
 */
public class WhereClause extends ConditionBuilder<WhereClause> {

    /*
    where -> 'where' condition | ε
    condition -> expr |  expr ('and'|'or')  condition | nested_condition
    nested_condition -> '(' condition  ')'
    expr -> eq | neq | in | not in | gt | gte | lt | lte | between | not between | like | not like | is null | is not null
    */

    public String getSqlWhere(String paramSymbol) {
        String sqlCondition = getSqlCondition(paramSymbol);
        return StringUtils.isBlank(sqlCondition) ? "" : "where " + sqlCondition;
    }

    public String getSqlWhere() {
        return getSqlWhere("");
    }

    @Override
    protected WhereClause newNestedBuilderInstance() {
        return new WhereClause();
    }
}
