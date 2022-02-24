package com.light0x00.mybatisext.sql.condition;

import com.light0x00.mybatisext.sql.condition.ConditionAst.*;
import com.light0x00.mybatisext.toolkit.MyBatisScripts;
import com.light0x00.mybatisext.toolkit.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.light0x00.mybatisext.toolkit.MyBatisScripts.hashExpr;

/**
 * Generating mybatis xml script , not final sql.
 *
 * @author light
 * @since 2022/2/21
 */
public class ConditionSourceGenerator extends ConditionAstVisitor<String> {

    @Override
    protected String visitEq(ValueMatch node) {
        if (node.getValue() == null) {
            return node.getColumn() + " is null";
        }
        return node.getColumn() + "=" + hashExpr(node.getParamSymbol());
    }

    @Override
    protected String visitNe(ValueMatch node) {
        if (node.getValue() == null) {
            return node.getColumn() + " is not null";
        }
        return node.getColumn() + "&lt;&gt;" + hashExpr(node.getParamSymbol());
    }

    @Override
    protected String visitIsNull(ValueMatch node) {
        return node.getColumn() + " is null";
    }

    @Override
    protected String visitIsNotNull(ValueMatch node) {
        return node.getColumn() + " is not null";
    }

    @Override
    protected String visitLike(ValueMatch node) {
        return node.getColumn() + " like " + hashExpr(node.getParamSymbol());
    }

    @Override
    protected String visitNotLike(ValueMatch node) {
        return node.getColumn() + " not like " + hashExpr(node.getParamSymbol());
    }

    @Override
    protected String visitIn(MultiValueMatch node) {
        return node.getColumn() + " in (" +
                valuesToHashExpr(node.getValuesParamSymbols()) +
                ")";
    }

    @Override
    protected String visitNotIn(MultiValueMatch node) {
        return node.getColumn() + " not in (" +
                valuesToHashExpr(node.getValuesParamSymbols()) +
                ")";
    }

    @Override
    protected String visitGt(Range node) {
        return node.getColumn() + "&gt;" + hashExpr(node.getParamSymbol());
    }

    @Override
    protected String visitGte(Range node) {
        return node.getColumn() + "&gt;=" + hashExpr(node.getParamSymbol());
    }

    @Override
    protected String visitLt(Range node) {
        return node.getColumn() + "&lt;" + hashExpr(node.getParamSymbol());
    }

    @Override
    protected String visitLte(Range node) {
        return node.getColumn() + "&lt;=" + hashExpr(node.getParamSymbol());
    }

    @Override
    protected String visitBetween(BinaryRange node) {
        return node.getColumn() + " between " + hashExpr(node.getBeginParamSymbol()) + " and " + hashExpr(node.getEndParamSymbol());
    }

    @Override
    protected String visitNotBetween(BinaryRange node) {
        return node.getColumn() + " not between " + hashExpr(node.getBeginParamSymbol()) + " and " + hashExpr(node.getEndParamSymbol());
    }

    @Override
    protected String visitAnd(And and) {
        return " and ";
    }

    @Override
    protected String visitOr(Or or) {
        return " or ";
    }

    @Override
    protected String visitNestedCondition(NestedCondition nestedClause) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode child : nestedClause.getChildren()) {
            sb.append(visitNode(child));
        }
        return "(" + sb + ")";
    }

    @Override
    public String visitWhere(Where node) {
        StringBuilder source = new StringBuilder();
        if (node.getCondition() != null) {
            String condition = visitCondition(node.getCondition());
            if (StringUtils.isNotBlank(condition)) {
                source.append("where ");
                source.append(condition);
            }
        }
        return source.toString();
    }

    @Override
    public String visitCondition(Condition root) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode child : root.getChildren()) {
            sb.append(visitNode(child));
        }
        return sb.toString();
    }

    private String valuesToHashExpr(List<String> valuesSymbol) {
        return valuesSymbol.stream()
                .map(MyBatisScripts::hashExpr)
                .collect(Collectors.joining(","));
    }
}
