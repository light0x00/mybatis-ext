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
public abstract class ConditionBuilder<T extends ConditionBuilder<T>> {

    @Getter
    protected Condition conditionAst;
    protected LinkedList<ASTNode> conditionNodes = new LinkedList<>();

    private ConditionSourceGenerator sqlGenerator = new ConditionSourceGenerator();

    protected abstract T newNestedBuilderInstance();

    protected NestedCondition buildNestedCondition() {
        return new NestedCondition(conditionNodes);
    }

    public String getSqlCondition(String paramSymbol) {
        conditionAst = new Condition(conditionNodes);
        new ConditionParamSymbolResolver(StringUtils.combineWithExactlyOneDot(paramSymbol, "conditionAst"))
                .visitCondition(conditionAst);
        return sqlGenerator.visitCondition(conditionAst);
    }

    public T eq(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.EQ, column, value));
        return thisAsSubType();
    }

    public T ne(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.NE, column, value));
        return thisAsSubType();
    }

    public T in(String column, Object... value) {
        return in(column, Arrays.asList(value));
    }

    public T in(String column, List<Object> value) {
        appendAndOperator(false);
        conditionNodes.add(new MultiValueMatch(ASTNodeType.IN, column, value));
        return thisAsSubType();
    }

    public T notIn(String column, Object... value) {
        return notIn(column, Arrays.asList(value));
    }

    public T notIn(String column, List<Object> value) {
        appendAndOperator(false);
        conditionNodes.add(new MultiValueMatch(ASTNodeType.NOT_IN, column, value));
        return thisAsSubType();
    }

    public T gt(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new Range(ASTNodeType.GT, column, value));
        return thisAsSubType();
    }

    public T gte(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new Range(ASTNodeType.GTE, column, value));
        return thisAsSubType();
    }

    public T lt(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new Range(ASTNodeType.LT, column, value));
        return thisAsSubType();
    }

    public T lte(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new Range(ASTNodeType.LTE, column, value));
        return thisAsSubType();
    }

    public T between(String column, Object begin, Object end) {
        appendAndOperator(false);
        conditionNodes.add(new BinaryRange(ASTNodeType.BETWEEN, column, begin, end));
        return thisAsSubType();
    }

    public T notBetween(String column, Object begin, Object end) {
        appendAndOperator(false);
        conditionNodes.add(new BinaryRange(ASTNodeType.NOT_BETWEEN, column, begin, end));
        return thisAsSubType();
    }

    public T like(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.LIKE, column, value));
        return thisAsSubType();
    }

    public T notLike(String column, Object value) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.NOT_LIKE, column, value));
        return thisAsSubType();
    }

    public T isNull(String column) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.IS_NULL, column));
        return thisAsSubType();
    }

    public T isNotNull(String column) {
        appendAndOperator(false);
        conditionNodes.add(new ValueMatch(ASTNodeType.IS_NOT_NULL, column));
        return thisAsSubType();
    }

    public T or() {
        appendOrOperator(true);
        return thisAsSubType();
    }

    public T and() {
        appendAndOperator(true);
        return thisAsSubType();
    }

    public T nested(Consumer<T> nestedConditionConsumer) {
        appendAndOperator(false);
        nested0(nestedConditionConsumer);
        return thisAsSubType();
    }

    public T orNested(Consumer<T> nestedConditionConsumer) {
        or();
        nested0(nestedConditionConsumer);
        return thisAsSubType();
    }

    public T andNested(Consumer<T> nestedConditionConsumer) {
        and();
        nested0(nestedConditionConsumer);
        return thisAsSubType();
    }

    private void nested0(Consumer<T> nestedConditionConsumer) {
        T nestedBuilder = newNestedBuilderInstance();
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
    private T thisAsSubType() {
        return (T) this;
    }

}
