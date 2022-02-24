package com.light0x00.mybatisext.sql;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author light
 * @since 2022/2/21
 */
public class SelectCondition extends WhereClause {

    private List<String> columns;

    public SelectCondition select(String... columns) {
        this.columns = Arrays.stream(columns)
                .collect(Collectors.toList());
        return this;
    }

    public WhereClause where() {
        return this;
    }

    public SelectCondition where(Consumer<WhereClause> whereConditionConsumer) {
        whereConditionConsumer.accept(this);
        return this;
    }

    public String getSqlColumns() {
        if (columns == null || columns.size() == 0) {
            return "*";
        } else {
            return String.join(",", columns);
        }
    }

}
