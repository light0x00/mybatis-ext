package com.light0x00.mybatisext.sql;

import com.light0x00.mybatisext.toolkit.StringUtils;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

import static com.light0x00.mybatisext.sql.set.SetAst.*;

/**
 * @author light
 * @since 2022/2/21
 */
public class UpdateCondition extends WhereCondition<UpdateCondition> {

    List<ASTNode> setItems = new LinkedList<>();

    @Getter
    private Set setAstRoot;

    public UpdateCondition where() {
        return this;
    }

    public String getSqlSet(String paramSymbol) {
        setAstRoot = new Set(setItems, StringUtils.combineWithExactlyOneDot(paramSymbol, "setAstRoot"));
        return setAstRoot.eval();
    }

    public UpdateCondition set(String column, String value) {
        setItems.add(new SetValue(column, value));
        return this;
    }

    public UpdateCondition incr(String column, Number value) {
        setItems.add(new IncrValue(column, value));
        return this;
    }

    public UpdateCondition decr(String column, Number value) {
        setItems.add(new DecrValue(column, value));
        return this;
    }

}
