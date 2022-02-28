package io.github.light0x00.mybatisext.sql;

import io.github.light0x00.mybatisext.toolkit.StringUtils;

/**
 * @author light
 * @since 2022/2/24
 */
public class HavingCondition extends ConditionBuilder<HavingCondition> {

    public String getSqlHaving(String paramSymbol) {
        String sqlCondition = getSqlCondition(paramSymbol);
        return StringUtils.isBlank(sqlCondition) ? "" : "having " + sqlCondition;
    }

    @Override
    protected HavingCondition newNestedInstance() {
        return new HavingCondition();
    }
}
