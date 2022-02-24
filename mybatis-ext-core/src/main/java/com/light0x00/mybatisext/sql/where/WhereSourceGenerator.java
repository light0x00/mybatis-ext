package com.light0x00.mybatisext.sql.where;

import com.light0x00.mybatisext.sql.where.WhereAst.*;
import com.light0x00.mybatisext.toolkit.MyBatisScripts;

import java.util.stream.Collectors;

import static com.light0x00.mybatisext.toolkit.MyBatisScripts.hashExp;

/**
 * Generating mybatis xml script , not final sql.
 *
 * @author light
 * @since 2022/2/21
 */
public class WhereSourceGenerator extends WhereAstVisitor<String> {

    @Override
    protected String visitEq(Eq eq) {
        if (eq.getValue() == null) {
            return eq.getColumn() + " is null";
        }
        return eq.getColumn() + "=" + hashExp(eq.getParamSymbol());
    }

    @Override
    protected String visitNe(Ne ne) {
        if (ne.getValue() == null) {
            return ne.getColumn() + " is not null";
        }
        return ne.getColumn() + "&lt;&gt;" + hashExp(ne.getParamSymbol());
    }

    @Override
    protected String visitIn(In in) {
        return in.getColumn() + " in (" +
                in.getValueAccessSymbols().stream()
                        .map(MyBatisScripts::hashExp)
                        .collect(Collectors.joining(",")) +
                ")";
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
    protected String visitNestedClause(NestedClause nestedClause) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode child : nestedClause.getChildren()) {
            sb.append(visitNode(child));
        }
        return "(" + sb + ")";
    }

    @Override
    public String visit(Clause root) {
        StringBuilder source = new StringBuilder();
        for (ASTNode child : root.getChildren()) {
            source.append(visitNode(child));
        }
        if (source.length() > 0) {
            return "where " + source;
        } else {
            return "";
        }
    }
}
