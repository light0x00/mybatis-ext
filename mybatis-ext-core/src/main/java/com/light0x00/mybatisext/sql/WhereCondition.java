package com.light0x00.mybatisext.sql;

import com.light0x00.mybatisext.sql.where.WhereParamSymbolResolver;
import com.light0x00.mybatisext.sql.where.WhereSourceGenerator;
import com.light0x00.mybatisext.toolkit.StringUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.light0x00.mybatisext.sql.where.WhereAst.*;

/**
 * @author light
 * @since 2022/2/20
 */
public class WhereCondition {

    /*
    clause -> condition
    condition -> factor or condition | factor and condition | ε
    factor -> eq | neq | in | not in | gt | gte | lt | lte | between | not between | like | not like | is null | is not null | clause
    */
    private LinkedList<ASTNode> astNodes = new LinkedList<>();

    private WhereSourceGenerator sqlGenerator = new WhereSourceGenerator();

    @Getter
    private Clause whereAstRoot;

    private NestedClause buildNestedClause() {
        return new NestedClause(astNodes);
    }

    public String getSqlWhere(String paramSymbol) {
        whereAstRoot = new Clause(astNodes);
        new WhereParamSymbolResolver(StringUtils.combineWithExactlyOneDot(paramSymbol, "whereAstRoot")).visit(whereAstRoot);
        return sqlGenerator.visit(whereAstRoot);
    }

    public String getSqlWhere() {
        return getSqlWhere("");
    }

    public WhereCondition eq(String column, Object value) {
        appendAndOperator(false);
        astNodes.add(new Eq(column, value));
        return this;
    }

    public WhereCondition ne(String column, Object value) {
        appendAndOperator(false);
        astNodes.add(new Ne(column, value));
        return this;
    }

    public WhereCondition in(String column, Object... value) {
        return in(column, Arrays.asList(value));
    }

    public WhereCondition in(String column, List<Object> value) {
        appendAndOperator(false);
        astNodes.add(new In(column, value));
        return this;
    }

    public WhereCondition or() {
        appendOrOperator(true);
        return this;
    }

    public WhereCondition and() {
        appendAndOperator(true);
        return this;
    }

    public WhereCondition nested(Consumer<WhereCondition> nestedConditionConsumer) {
        appendAndOperator(false);
        nested0(nestedConditionConsumer);
        return this;
    }

    public WhereCondition orNested(Consumer<WhereCondition> nestedConditionConsumer) {
        or();
        nested0(nestedConditionConsumer);
        return this;
    }

    public WhereCondition andNested(Consumer<WhereCondition> nestedConditionConsumer) {
        and();
        nested0(nestedConditionConsumer);
        return this;
    }

    private void nested0(Consumer<WhereCondition> nestedConditionConsumer) {
        WhereCondition nestedCondition = new WhereCondition();
        nestedConditionConsumer.accept(nestedCondition);
        astNodes.addLast(nestedCondition.buildNestedClause());
    }

    private void appendOrOperator(boolean force) {
        if (astNodes.isEmpty()) {
            return;
        }
        //没有连接操作符,直接添加 Or
        if (!(astNodes.getLast() instanceof ConjunctionOperator)) {
            astNodes.addLast(new Or());
        }
        //有连接操作符,但不是 Or
        else if (!(astNodes.getLast() instanceof Or)) {
            if (force) { //强制替换为 Or
                astNodes.removeLast();
                astNodes.addLast(new Or());
            }
        }
    }

    private void appendAndOperator(boolean force) {
        if (astNodes.isEmpty()) {
            return;
        }
        //没有连接操作符,直接添加 And
        if (!(astNodes.getLast() instanceof ConjunctionOperator)) {
            astNodes.addLast(new And());
        }
        //有连接操作符,但不是 And
        else if (!(astNodes.getLast() instanceof And)) {
            if (force) { //强制替换为 And
                astNodes.removeLast();
                astNodes.addLast(new And());
            }
        }
    }

}
