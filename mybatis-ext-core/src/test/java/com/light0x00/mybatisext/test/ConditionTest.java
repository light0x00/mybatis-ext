package com.light0x00.mybatisext.test;

import com.light0x00.mybatisext.sql.WhereCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author light
 * @since 2022/2/21
 */
public class ConditionTest {

    @Test
    public void testEq() {
        Assertions.assertEquals("where a=#{whereAstRoot.children[0].value}", new WhereCondition().eq("a", 1).getSqlWhere());
    }

    @Test
    public void testNe() {
        Assertions.assertEquals("where a&lt;&gt;#{whereAstRoot.children[0].value}", new WhereCondition().ne("a", 1).getSqlWhere());
    }

    @Test
    public void testIn() {
        Assertions.assertEquals("where a in (#{whereAstRoot.children[0].values[0]},#{whereAstRoot.children[0].values[1]})",
                new WhereCondition().in("a", 1, 3).getSqlWhere());
    }

    @Test
    public void testConjunction() {
        //c1 = v1 and c2 = v2
        String sqlWhere1 = new WhereCondition().eq("c1", "v1").and().eq("c2", "v2")
                .getSqlWhere();
        Assertions.assertEquals("where c1=#{whereAstRoot.children[0].value} and c2=#{whereAstRoot.children[2].value}", sqlWhere1);

        //c1 = v1 and (c2= v2 or c3 = v3)
        String sqlWhere2 = new WhereCondition()
                .eq("c1", "v1")
                .andNested(
                        (nested) -> nested.eq("c2", "v2").or().eq("c3", "v3")
                ).getSqlWhere();
        Assertions.assertEquals("where c1=#{whereAstRoot.children[0].value} and (c2=#{whereAstRoot.children[2].children[0].value}" +
                        " or " +
                        "c3=#{whereAstRoot.children[2].children[2].value})",
                sqlWhere2);

        //c1 = v1 or c2 = v2
        String sqlWhere3 = new WhereCondition().eq("c1", "v1").or().eq("c2", "v2")
                .getSqlWhere();
        Assertions.assertEquals("where c1=#{whereAstRoot.children[0].value} or c2=#{whereAstRoot.children[2].value}", sqlWhere3);

        //c1 = v1 or (c2 = v2 and c3 = v3)
        String sqlWhere4 = new WhereCondition()
                .eq("c1", "v1")
                .orNested(
                        (nested) -> nested.eq("c2", "v2").and().eq("c3", "v3")
                ).getSqlWhere();
        Assertions.assertEquals("where c1=#{whereAstRoot.children[0].value} or (c2=#{whereAstRoot.children[2].children[0].value} and c3=#{whereAstRoot.children[2].children[2].value})", sqlWhere4);

        //(c1 = v1 and c2 = v2) or (c3 = v3 and c4 = v4)
        String sqlWhere5 = new WhereCondition()
                .nested(nested -> nested.eq("c1", "v1").and().eq("c2", "v2"))
                .or()
                .nested(nested -> nested.eq("c3", "v3").and().eq("c4", "v4"))
                .getSqlWhere();
        Assertions.assertEquals("where " +
                "(c1=#{whereAstRoot.children[0].children[0].value} and c2=#{whereAstRoot.children[0].children[2].value})" +
                " or " +
                "(c3=#{whereAstRoot.children[2].children[0].value} and c4=#{whereAstRoot.children[2].children[2].value})", sqlWhere5);

        //(c1 = v1 and (c2 = v2 or c3 = v3 )) or (c4 = v4 and (c5 = v5 or c6 = v6))
        String sqlWhere6 = new WhereCondition()
                .nested(cond -> cond
                        .eq("c1", "v1")
                        .andNested(
                                subCond -> subCond.eq("c2", "v2").or().eq("c3", "v3")
                        ))
                .or()
                .nested(cond -> cond
                        .eq("c4", "v4")
                        .andNested(
                                subCond -> subCond.eq("c5", "v5").or().eq("c6", "v6")
                        )
                ).getSqlWhere();
        Assertions.assertEquals("where " +
                "(c1=#{whereAstRoot.children[0].children[0].value} and " +
                "(c2=#{whereAstRoot.children[0].children[2].children[0].value} or c3=#{whereAstRoot.children[0].children[2].children[2].value}))" +
                " or " +
                "(" +
                "c4=#{whereAstRoot.children[2].children[0].value} and " +
                "(c5=#{whereAstRoot.children[2].children[2].children[0].value} or c6=#{whereAstRoot.children[2].children[2].children[2].value})" +
                ")", sqlWhere6);
    }
}
