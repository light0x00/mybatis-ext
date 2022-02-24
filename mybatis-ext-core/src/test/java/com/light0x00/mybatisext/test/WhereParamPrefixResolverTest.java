package com.light0x00.mybatisext.test;

import com.light0x00.mybatisext.sql.where.WhereAst.*;
import com.light0x00.mybatisext.sql.where.WhereParamSymbolResolver;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author light
 * @since 2022/2/23
 */
public class WhereParamPrefixResolverTest {

    @Test
    public void test() {
        Clause clause = new Clause(Arrays.asList(
                new NestedClause(Arrays.asList(
                        new Eq("a", 1),
                        new And(),
                        new Eq("b", 2)
                )),
                new Or(),
                new NestedClause(Arrays.asList(
                        new Eq("c", 1),
                        new And(),
                        new Eq("d", 2)
                ))));
        new WhereParamSymbolResolver("root").visit(clause);
    }
}
