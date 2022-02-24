package com.light0x00.mybatisext.sql.condition;

import com.light0x00.mybatisext.sql.condition.ConditionAst.*;

/**
 * @author light
 * @since 2022/2/21
 */
public abstract class ConditionAstVisitor<T> {

    protected abstract T visitEq(ValueMatch node);

    protected abstract T visitNe(ValueMatch node);

    protected abstract T visitIsNull(ValueMatch node);

    protected abstract T visitIsNotNull(ValueMatch node);

    protected abstract T visitLike(ValueMatch node);

    protected abstract T visitNotLike(ValueMatch node);

    protected abstract T visitIn(MultiValueMatch node);

    protected abstract T visitNotIn(MultiValueMatch node);

    protected abstract T visitGt(Range node);

    protected abstract T visitGte(Range node);

    protected abstract T visitLt(Range node);

    protected abstract T visitLte(Range node);

    protected abstract T visitBetween(BinaryRange node);

    protected abstract T visitNotBetween(BinaryRange node);

    protected abstract T visitAnd(And node);

    protected abstract T visitOr(Or node);

    protected abstract T visitNestedCondition(NestedCondition node);

    public abstract T visitCondition(Condition root);

    public abstract T visitWhere(Where node);

    protected T visitNode(ASTNode node) {
        switch (node.getType()) {
            case EQ:
                return visitEq((ValueMatch) node);
            case NE:
                return visitNe((ValueMatch) node);
            case IS_NULL:
                return visitIsNull((ValueMatch) node);
            case IS_NOT_NULL:
                return visitIsNotNull((ValueMatch) node);
            case LIKE:
                return visitLike((ValueMatch) node);
            case NOT_LIKE:
                return visitNotLike((ValueMatch) node);
            case IN:
                return visitIn((MultiValueMatch) node);
            case NOT_IN:
                return visitNotIn((MultiValueMatch) node);
            case GT:
                return visitGt((Range) node);
            case GTE:
                return visitGte((Range) node);
            case LT:
                return visitLt((Range) node);
            case LTE:
                return visitLte((Range) node);
            case BETWEEN:
                return visitBetween((BinaryRange) node);
            case NOT_BETWEEN:
                return visitNotBetween((BinaryRange) node);
            case AND:
                return visitAnd((And) node);
            case OR:
                return visitOr((Or) node);
            case NESTED_CONDITION:
                return visitNestedCondition((NestedCondition) node);
            case CONDITION:
                return visitCondition((Condition) node);
            default:
                throw new IllegalStateException("Unhandled ast node type:" + node.getClass().getName());
        }
    }
}
