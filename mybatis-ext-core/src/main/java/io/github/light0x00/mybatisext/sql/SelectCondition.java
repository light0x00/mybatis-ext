package io.github.light0x00.mybatisext.sql;

import io.github.light0x00.mybatisext.toolkit.CollectionUtils;
import io.github.light0x00.mybatisext.toolkit.StringUtils;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author light
 * @since 2022/2/21
 */
public class SelectCondition extends WhereCondition<SelectCondition> {

    private List<String> selectColumns;

    private List<String> groupByColumns;

    @Getter
    private HavingCondition havingCondition;

    private String orderByClause;

    public SelectCondition select(String... columns) {
        this.selectColumns = CollectionUtils.toList(columns);
        return this;
    }

    public SelectCondition groupBy(String... columns) {
        this.groupByColumns = CollectionUtils.toList(columns);
        return this;
    }

    public SelectCondition having(Consumer<HavingCondition> havingConsumer) {
        havingCondition = new HavingCondition();
        havingConsumer.accept(havingCondition);
        return this;
    }

    public SelectCondition orderByClause(String clause) {
        this.orderByClause = clause;
        return this;
    }

    public String getSqlColumns() {
        if (CollectionUtils.isEmpty(selectColumns)) {
            return "*";
        } else {
            return String.join(",", selectColumns);
        }
    }

    public String getSqlGroupBy() {
        if (CollectionUtils.isEmpty(groupByColumns)) {
            return "";
        }
        return "group by " + String.join(",", groupByColumns);
    }

    public String getSqlHaving(String paramSymbol) {
        if (havingCondition == null) {
            return "";
        } else {
            return havingCondition.getSqlHaving(StringUtils.combineWithExactlyOneDot(paramSymbol, "havingCondition"));
        }
    }

    public String getOrderByClause() {
        return StringUtils.isBlank(orderByClause) ? "" : orderByClause;
    }

    @Override
    protected SelectCondition newNestedInstance() {
        return new SelectCondition();
    }
}
