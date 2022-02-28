package io.github.light0x00.mybatisext.sql;

import io.github.light0x00.mybatisext.sql.set.SetAst;
import io.github.light0x00.mybatisext.toolkit.StringUtils;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author light
 * @since 2022/2/21
 */
public class UpdateCondition extends WhereCondition<UpdateCondition> {

    List<SetAst.ASTNode> setItems = new LinkedList<>();

    @Getter
    private SetAst.Set setAstRoot;

    public UpdateCondition where() {
        return this;
    }

    public String getSqlSet(String paramSymbol) {
        setAstRoot = new SetAst.Set(setItems, StringUtils.combineWithExactlyOneDot(paramSymbol, "setAstRoot"));
        return setAstRoot.eval();
    }

    public UpdateCondition set(String column, String value) {
        setItems.add(new SetAst.SetValue(column, value));
        return this;
    }

    public UpdateCondition incr(String column, Number value) {
        setItems.add(new SetAst.IncrValue(column, value));
        return this;
    }

    public UpdateCondition decr(String column, Number value) {
        setItems.add(new SetAst.DecrValue(column, value));
        return this;
    }

    @Override
    protected UpdateCondition newNestedInstance() {
        return new UpdateCondition();
    }
}
