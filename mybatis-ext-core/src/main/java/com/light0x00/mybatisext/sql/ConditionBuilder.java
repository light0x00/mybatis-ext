package com.light0x00.mybatisext.sql;

import com.light0x00.mybatisext.sql.condition.ConditionAst.*;
import com.light0x00.mybatisext.sql.condition.ConditionParamSymbolResolver;
import com.light0x00.mybatisext.sql.condition.ConditionSourceGenerator;
import com.light0x00.mybatisext.toolkit.StringUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author light
 * @since 2022/2/24
 */
class ConditionBuilder<R extends ConditionBuilder<R>> {

    @Getter
    protected Condition conditionAst;
    protected LinkedList<ASTNode> conditionNodes = new LinkedList<>();

    private ConditionSourceGenerator sqlGenerator = new ConditionSourceGenerator();

    protected NestedCondition buildNestedCondition() {
        return new NestedCondition(conditionNodes);
    }

    public String getSqlCondition(String paramSymbol) {
        conditionAst = new Condition(conditionNodes);
        new ConditionParamSymbolResolver(StringUtils.combineWithExactlyOneDot(paramSymbol, "conditionAst"))
                .visitCondition(conditionAst);
        return sqlGenerator.visitCondition(conditionAst);
    }

    public R eq(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.EQ, column, value));
        return thisAsR();
    }

    public R ne(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.NE, column, value));
        return thisAsR();
    }

    public R in(String column, Object... value) {
        return in(column, Arrays.asList(value));
    }

    public R in(String column, List<Object> value) {
        appendAndOperator(false);
        conditionNodes.add(new MultiValueMatch(ASTNodeType.IN, column, value));
        return thisAsR();
    }

    public R notIn(String column, Object... value) {
        return notIn(column, Arrays.asList(value));
    }

    public R notIn(String column, List<Object> value) {
        appendAndOperator(false);
        conditionNodes.add(new MultiValueMatch(ASTNodeType.NOT_IN, column, value));
        return thisAsR();
    }

    public R gt(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new Range(ASTNodeType.GT, column, value));
        return thisAsR();
    }

    public R gte(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new Range(ASTNodeType.GTE, column, value));
        return thisAsR();
    }

    public R lt(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new Range(ASTNodeType.LT, column, value));
        return thisAsR();
    }

    public R lte(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new Range(ASTNodeType.LTE, column, value));
        return thisAsR();
    }

    public R between(String column, Object begin, Object end) {
        appendAndOperator(false);
        conditionNodes.add(new BinaryRange(ASTNodeType.BETWEEN, column, begin, end));
        return thisAsR();
    }

    public R notBetween(String column, Object begin, Object end) {
        appendAndOperator(false);
        conditionNodes.add(new BinaryRange(ASTNodeType.NOT_BETWEEN, column, begin, end));
        return thisAsR();
    }

    public R like(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.LIKE, column, value));
        return thisAsR();
    }

    public R notLike(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.NOT_LIKE, column, value));
        return thisAsR();
    }

    public R isNull(String column) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.IS_NULL, column));
        return thisAsR();
    }

    public R isNotNull(String column) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.IS_NOT_NULL, column));
        return thisAsR();
    }

    public R or() {
        appendOrOperator(true);
        return thisAsR();
    }

    public R and() {
        appendAndOperator(true);
        return thisAsR();
    }

    public R nested(Consumer<ConditionBuilder<R>> nestedConditionConsumer) {
        appendAndOperator(false);
        nested0(nestedConditionConsumer);
        return thisAsR();
    }

    public R orNested(Consumer<ConditionBuilder<R>> nestedConditionConsumer) {
        or();
        nested0(nestedConditionConsumer);
        return thisAsR();
    }

    public R andNested(Consumer<ConditionBuilder<R>> nestedConditionConsumer) {
        and();
        nested0(nestedConditionConsumer);
        return thisAsR();
    }

    private void nested0(Consumer<ConditionBuilder<R>> nestedConditionConsumer) {
        ConditionBuilder<R> nestedBuilder = new ConditionBuilder<>();
        nestedConditionConsumer.accept(nestedBuilder);
        conditionNodes.addLast(nestedBuilder.buildNestedCondition());
    }

    private void appendOrOperator(boolean force) {
        if (conditionNodes.isEmpty()) {
            return;
        }
        //没有连接操作符,直接添加 Or
        if (conditionNodes.getLast().getType() != ASTNodeType.AND &&
                conditionNodes.getLast().getType() != ASTNodeType.OR) {
            conditionNodes.addLast(new Or());
        }
        //有连接操作符,但不是 Or
        else if (conditionNodes.getLast().getType() != ASTNodeType.OR) {
            if (force) { //强制替换为 Or
                conditionNodes.removeLast();
                conditionNodes.addLast(new Or());
            }
        }
    }

    private void appendAndOperator(boolean force) {
        if (conditionNodes.isEmpty()) {
            return;
        }
        //没有连接操作符,直接添加 And
        if (conditionNodes.getLast().getType() != ASTNodeType.AND &&
                conditionNodes.getLast().getType() != ASTNodeType.OR) {
            conditionNodes.addLast(new And());
        }
        //有连接操作符,但不是 And
        else if (conditionNodes.getLast().getType() != ASTNodeType.AND) {
            if (force) { //强制替换为 And
                conditionNodes.removeLast();
                conditionNodes.addLast(new And());
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    protected R thisAsR() {
        return (R) this;
    }

}
