package io.github.light0x00.mybatisext;

import io.github.light0x00.mybatisext.sql.condition.ConditionAst;
import io.github.light0x00.mybatisext.sql.condition.ConditionParamSymbolResolver;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author light
 * @since 2022/2/23
 */
public class WhereParamPrefixResolverTest {

    @Test
    public void test() {
        ConditionAst.Condition clause = new ConditionAst.Condition(Arrays.asList(
                new ConditionAst.NestedCondition(Arrays.asList(
                        new ConditionAst.ValueMatch(ConditionAst.ASTNodeType.EQ, "a", 1),
                        new ConditionAst.And(),
                        new ConditionAst.ValueMatch(ConditionAst.ASTNodeType.EQ, "b", 2)
                )),
                new ConditionAst.Or(),
                new ConditionAst.NestedCondition(Arrays.asList(
                        new ConditionAst.ValueMatch(ConditionAst.ASTNodeType.EQ, "c", 1),
                        new ConditionAst.And(),
                        new ConditionAst.ValueMatch(ConditionAst.ASTNodeType.EQ, "d", 2)
                ))));
        new ConditionParamSymbolResolver("root").visitCondition(clause);
    }
}
