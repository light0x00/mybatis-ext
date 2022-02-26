package io.github.light0x00.mybatisext.sql;

import io.github.light0x00.mybatisext.sql.set.SetAst;
import io.github.light0x00.mybatisext.toolkit.MyBatisScripts;
import io.github.light0x00.mybatisext.toolkit.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author light
 * @since 2022/2/23
 */
public class InsertCondition {
    List<String> columns = new LinkedList<>();
    List<Object> values = new LinkedList<>();
    List<SetAst.ASTNode> updateItemsOnDupKey = new LinkedList<>();

    public InsertCondition insertValue(String column, Object value) {
        columns.add(column);
        values.add(value);
        return this;
    }

    public InsertCondition updateValueOnDupKey(String column, Object value) {
        updateItemsOnDupKey.add(new SetAst.SetValue(column, value));
        return this;
    }

    public InsertCondition updateIncrValueOnDupKey(String column, Number value) {
        updateItemsOnDupKey.add(new SetAst.IncrValue(column, value));
        return this;
    }

    public InsertCondition updateDecrValueOnDupKey(String column, Number value) {
        updateItemsOnDupKey.add(new SetAst.DecrValue(column, value));
        return this;
    }

    public String getSqlColumns() {
        return String.join(",", columns);
    }

    public String getSqlValues(String accessSymbol) {
        String valuesAccessSymbol = StringUtils.combineWithExactlyOneDot(accessSymbol, "values");
        return IntStream.range(0, this.values.size())
                .mapToObj(i -> MyBatisScripts.hashExpr(valuesAccessSymbol + "[" + i + "]"))
                .collect(Collectors.joining(","));
    }

    public String getSqlOnDupKey(String accessSymbol) {
        return new SetAst.Set(updateItemsOnDupKey, accessSymbol, false).eval();
    }
}
