package com.light0x00.mybatisext.sql.where;

import com.light0x00.mybatisext.sql.where.WhereAst.*;

/**
 * @author light
 * @since 2022/2/21
 */
public abstract class WhereAstVisitor<T> {

    protected abstract T visitEq(Eq eq);

    protected abstract T visitNe(Ne ne);

    protected abstract T visitIn(In in);

    protected abstract T visitAnd(And and);

    protected abstract T visitOr(Or or);

    protected abstract T visitNestedClause(NestedClause nestedClause);

    public abstract T visit(Clause root);

    protected T visitNode(ASTNode astNode) {
        if (astNode instanceof Eq) {
            return visitEq((Eq) astNode);
        } else if (astNode instanceof Ne) {
            return visitNe((Ne) astNode);
        } else if (astNode instanceof In) {
            return visitIn((In) astNode);
        } else if (astNode instanceof And) {
            return visitAnd((And) astNode);
        } else if (astNode instanceof Or) {
            return visitOr((Or) astNode);
        } else if (astNode instanceof NestedClause) {
            return visitNestedClause((NestedClause) astNode);
        } else {
            throw new IllegalStateException("Unhandled ast node type:" + astNode.getClass().getName());
        }
    }
}
