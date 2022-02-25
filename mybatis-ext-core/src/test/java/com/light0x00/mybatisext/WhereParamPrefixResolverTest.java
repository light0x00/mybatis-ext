package com.light0x00.mybatisext;

import com.light0x00.mybatisext.sql.condition.ConditionAst.*;
import com.light0x00.mybatisext.sql.condition.ConditionParamSymbolResolver;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author light
 * @since 2022/2/23
 */
public class WhereParamPrefixResolverTest {

    @Test
    public void test() {
        Condition clause = new Condition(Arrays.asList(
                new NestedCondition(Arrays.asList(
                        new ValueMatch(ASTNodeType.EQ, "a", 1),
                        new And(),
                        new ValueMatch(ASTNodeType.EQ, "b", 2)
                )),
                new Or(),
                new NestedCondition(Arrays.asList(
                        new ValueMatch(ASTNodeType.EQ, "c", 1),
                        new And(),
                        new ValueMatch(ASTNodeType.EQ, "d", 2)
                ))));
        new ConditionParamSymbolResolver("root").visitCondition(clause);
    }
}
