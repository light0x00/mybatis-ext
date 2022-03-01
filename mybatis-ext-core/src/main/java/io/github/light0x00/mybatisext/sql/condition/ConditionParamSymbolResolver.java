package io.github.light0x00.mybatisext.sql.condition;

import io.github.light0x00.mybatisext.toolkit.StringUtils;

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
        } else {
            rootParamSymbol = StringUtils.trimTailDot(rootParamSymbol);
        }
        stack.push(rootParamSymbol);
    }

    @Override
    protected Void visitEq(ConditionAst.ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitNe(ConditionAst.ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitIsNull(ConditionAst.ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitIsNotNull(ConditionAst.ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitLike(ConditionAst.ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitNotLike(ConditionAst.ValueMatch node) {
        resolveValueMatch(node);
        return null;
    }

    @Override
    protected Void visitIn(ConditionAst.MultiValueMatch node) {
        resolveMultiValueMatch(node);
        return null;
    }

    @Override
    protected Void visitNotIn(ConditionAst.MultiValueMatch node) {
        resolveMultiValueMatch(node);
        return null;
    }

    @Override

    protected Void visitGt(ConditionAst.Range node) {
        resolveRange(node);
        return null;
    }

    @Override
    protected Void visitGte(ConditionAst.Range node) {
        resolveRange(node);
        return null;
    }

    @Override
    protected Void visitLt(ConditionAst.Range node) {
        resolveRange(node);
        return null;
    }

    @Override
    protected Void visitLte(ConditionAst.Range node) {
        resolveRange(node);
        return null;
    }

    @Override
    protected Void visitBetween(ConditionAst.BinaryRange node) {
        resolveBinaryRange(node);
        return null;
    }

    @Override
    protected Void visitNotBetween(ConditionAst.BinaryRange node) {
        resolveBinaryRange(node);
        return null;
    }

    @Override
    protected Void visitAnd(ConditionAst.And node) {
        stack.pop();
        return null;
    }

    @Override
    protected Void visitOr(ConditionAst.Or node) {
        stack.pop();
        return null;
    }

    @Override
    protected Void visitNestedCondition(ConditionAst.NestedCondition node) {
        node.setParamSymbol(stack.pop());
        for (int i = 0; i < node.getChildren().size(); i++) {
            stack.push(node.getParamSymbol() + ".children[" + i + "]");
            visitNode(node.getChildren().get(i));
        }
        return null;
    }

    @Override
    public Void visitCondition(ConditionAst.Condition node) {
        node.setParamSymbol(stack.pop());
        for (int i = 0; i < node.getChildren().size(); i++) {
            stack.push(node.getParamSymbol() + ".children[" + i + "]");
            visitNode(node.getChildren().get(i));
        }
        return null;
    }

    private void resolveRange(ConditionAst.Range node) {
        node.setParamSymbol(stack.pop() + ".value");
    }

    private void resolveBinaryRange(ConditionAst.BinaryRange node) {
        node.setParamSymbol(stack.pop());
        node.setBeginParamSymbol(node.getParamSymbol() + ".begin");
        node.setEndParamSymbol(node.getParamSymbol() + ".end");
    }

    private void resolveValueMatch(ConditionAst.ValueMatch node) {
        node.setParamSymbol(stack.pop() + ".value");
    }

    private void resolveMultiValueMatch(ConditionAst.MultiValueMatch node) {
        node.setParamSymbol(stack.pop());
        node.setValuesParamSymbols(IntStream.range(0, node.getValues().size())
                .mapToObj(i -> node.getParamSymbol() + ".values[" + i + "]")
                .collect(Collectors.toList()));
    }

}
