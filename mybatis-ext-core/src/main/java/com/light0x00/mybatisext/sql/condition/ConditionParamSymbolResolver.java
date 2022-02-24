package com.light0x00.mybatisext.sql.condition;

import com.light0x00.mybatisext.sql.condition.ConditionAst.*;
import com.light0x00.mybatisext.toolkit.StringUtils;

import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author light
 * @since 2022/2/23
 */
public class ConditionParamSymbolResolver extends ConditionAstVisitor<Void> {

    private Stack<String> stack = new Stack<>();

    public ConditionParamSymbolResolver(String rootParamSymbol) {
        if (StringUtils.isBlank(rootParamSymbol)) {
            rootParamSymbol = "";
        } else if (rootParamSymbol.lastIndexOf(0) == '.') {
            rootParamSymbol = rootParamSymbol.substring(0, rootParamSymbol.length() - 1);
        }
        stack.push(rootParamSymbol);
    }

    @Override
    protected Void visitEq(ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitNe(ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitIsNull(ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitIsNotNull(ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitLike(ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitNotLike(ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitIn(MultiValueMatch node) {
        resolveMultiValueMatch(node);
        return null;
    }

    @Override
    protected Void visitNotIn(MultiValueMatch node) {
        resolveMultiValueMatch(node);
        return null;
    }

    @Override

    protected Void visitGt(Range node) {
        resolveRange(node);
        return null;
    }

    @Override
    protected Void visitGte(Range node) {
        resolveRange(node);
        return null;
    }

    @Override
    protected Void visitLt(Range node) {
        resolveRange(node);
        return null;
    }

    @Override
    protected Void visitLte(Range node) {
        resolveRange(node);
        return null;
    }

    @Override
    protected Void visitBetween(BinaryRange node) {
        resolveBinaryRange(node);
        return null;
    }

    @Override
    protected Void visitNotBetween(BinaryRange node) {
        resolveBinaryRange(node);
        return null;
    }

    @Override
    protected Void visitAnd(And node) {
        stack.pop();
        return null;
    }

    @Override
    protected Void visitOr(Or node) {
        stack.pop();
        return null;
    }

    @Override
    protected Void visitNestedCondition(NestedCondition node) {
        node.setParamSymbol(stack.pop());
        for (int i = 0; i < node.getChildren().size(); i++) {
            stack.push(node.getParamSymbol() + ".children[" + i + "]");
            visitNode(node.getChildren().get(i));
        }
        return null;
    }

    @Override
    public Void visitCondition(Condition node) {
        node.setParamSymbol(stack.pop());
        for (int i = 0; i < node.getChildren().size(); i++) {
            stack.push(node.getParamSymbol() + ".children[" + i + "]");
            visitNode(node.getChildren().get(i));
        }
        return null;
    }

    @Override
    public Void visitWhere(Where node) {
        node.setParamSymbol(stack.pop());
        stack.push(node.getParamSymbol() + ".condition");
        visitCondition(node.getCondition());
        return null;
    }

    private void resolveRange(Range node) {
        node.setParamSymbol(stack.pop() + ".value");
    }

    private void resolveBinaryRange(BinaryRange node) {
        node.setParamSymbol(stack.pop());
        node.setBeginParamSymbol(node.getParamSymbol() + ".begin");
        node.setEndParamSymbol(node.getParamSymbol() + ".end");
    }

    private void resolveValueMatch(ValueMatch node) {
        node.setParamSymbol(stack.pop() + ".value");
    }

    private void resolveMultiValueMatch(MultiValueMatch node) {
        node.setParamSymbol(stack.pop());
        node.setValuesParamSymbols(IntStream.range(0, node.getValues().size())
                .mapToObj(i -> node.getParamSymbol() + ".values[" + i + "]")
                .collect(Collectors.toList()));
    }

}
