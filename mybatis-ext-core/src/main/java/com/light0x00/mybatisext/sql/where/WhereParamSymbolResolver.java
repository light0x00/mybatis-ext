package com.light0x00.mybatisext.sql.where;

import com.light0x00.mybatisext.toolkit.StringUtils;

import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author light
 * @since 2022/2/23
 */
public class WhereParamSymbolResolver extends WhereAstVisitor<Void> {

    private final String rootParamPrefix;

    public WhereParamSymbolResolver(String rootParamPrefix) {
        if (StringUtils.isBlank(rootParamPrefix)) {
            rootParamPrefix = "";
        } else if (rootParamPrefix.lastIndexOf(0) == '.') {
            rootParamPrefix = rootParamPrefix.substring(0, rootParamPrefix.length() - 1);
        }
        this.rootParamPrefix = rootParamPrefix;
    }

    private Stack<String> stack = new Stack<>();

    @Override
    protected Void visitEq(WhereAst.Eq eq) {
        eq.setParamSymbol(stack.pop() + ".value");
        return null;
    }

    @Override
    protected Void visitNe(WhereAst.Ne ne) {
        ne.setParamSymbol(stack.pop() + ".value");
        return null;
    }

    @Override
    protected Void visitIn(WhereAst.In in) {
        in.setParamSymbol(stack.pop());
        in.setValueAccessSymbols(IntStream.range(0, in.getValues().size())
                .mapToObj(i -> in.getParamSymbol() + ".values[" + i + "]")
                .collect(Collectors.toList()));
        return null;
    }

    @Override
    protected Void visitAnd(WhereAst.And and) {
        stack.pop();
        return null;
    }

    @Override
    protected Void visitOr(WhereAst.Or or) {
        stack.pop();
        return null;
    }

    @Override
    protected Void visitNestedClause(WhereAst.NestedClause nestedClause) {
        nestedClause.setParamSymbol(stack.pop());
        for (int i = 0; i < nestedClause.getChildren().size(); i++) {
            stack.push(nestedClause.getParamSymbol() + ".children[" + i + "]");
            visitNode(nestedClause.getChildren().get(i));
        }
        return null;
    }

    @Override
    public Void visit(WhereAst.Clause root) {
        root.setParamSymbol(rootParamPrefix);
        for (int i = 0; i < root.getChildren().size(); i++) {
            stack.push(rootParamPrefix + ".children[" + i + "]");
            visitNode(root.getChildren().get(i));
        }
        return null;
    }
}
