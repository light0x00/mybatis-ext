package io.github.light0x00.mybatisext.sql;

import io.github.light0x00.mybatisext.toolkit.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author light
 * @since 2022/2/21
 */
public class SelectCondition extends WhereCondition<SelectCondition> {

    private List<String> columns;

    public SelectCondition select(String... columns) {
        this.columns = Arrays.stream(columns)
                .collect(Collectors.toList());
        return this;
    }

    public String getSqlColumns() {
        if (columns == null || columns.size() == 0) {
            return "*";
        } else {
            return String.join(",", columns);
        }
    }

    public String getSqlWhere(String paramSymbol) {
        String sqlCondition = getSqlCondition(paramSymbol);
        return StringUtils.isBlank(sqlCondition) ? "" : "where " + sqlCondition;
    }

    public String getSqlWhere() {
        return getSqlWhere("");
    }

}
