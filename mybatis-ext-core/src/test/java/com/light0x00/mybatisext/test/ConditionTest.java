package com.light0x00.mybatisext.test;

import com.light0x00.mybatisext.sql.WhereClause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lighto
 * @since 2022/2/21
 */
public class ConditionTest {

    @Test
    public void eq() {
        Assertions.assertEquals("where a=#{conditionAst.children[0].value}", new WhereClause().eq("a", 1).getSqlWhere());
    }

    @Test
    public void ne() {
        Assertions.assertEquals("where a&lt;&gt;#{conditionAst.children[0].value}", new WhereClause().ne("a", 1).getSqlWhere());
    }

    @Test
    public void in() {
        Assertions.assertEquals("where a in (#{conditionAst.children[0].values[0]},#{conditionAst.children[0].values[1]})",
                new WhereClause().in("a", 1, 3).getSqlWhere());
    }

    @Test
    public void notIn() {
        Assertions.assertEquals("where a not in (#{conditionAst.children[0].values[0]},#{conditionAst.children[0].values[1]})",
                new WhereClause().notIn("a", 1, 3).getSqlWhere());
    }

    @Test
    public void gt() {
        Assertions.assertEquals("where a&gt;#{conditionAst.children[0].value}",
                new WhereClause().gt("a", 1).getSqlWhere());
    }

    @Test
    public void gte() {
        Assertions.assertEquals("where a&gt;=#{conditionAst.children[0].value}",
                new WhereClause().gte("a", 1).getSqlWhere());
    }


    @Test
    public void lt() {
        Assertions.assertEquals("where a&lt;#{conditionAst.children[0].value}",
                new WhereClause().lt("a", 1).getSqlWhere());
    }

    @Test
    public void lte() {
        Assertions.assertEquals("where a&lt;=#{conditionAst.children[0].value}",
                new WhereClause().lte("a", 1).getSqlWhere());
    }

    @Test
    public void between() {
        Assertions.assertEquals("where a between #{conditionAst.children[0].begin} and #{conditionAst.children[0].end}",
                new WhereClause().between("a", 1, 2).getSqlWhere());
    }

    @Test
    public void notBetween() {
        Assertions.assertEquals("where a not between #{conditionAst.children[0].begin} and #{conditionAst.children[0].end}",
                new WhereClause().notBetween("a", 1, 2).getSqlWhere());
    }

    @Test
    public void like() {
        Assertions.assertEquals("where a like #{conditionAst.children[0].value}",
                new WhereClause().like("a", "%a").getSqlWhere());
    }

    @Test
    public void notLike() {
        Assertions.assertEquals("where a not like #{conditionAst.children[0].value}",
                new WhereClause().notLike("a", "%a").getSqlWhere());
    }

    @Test
    public void isNull() {
        Assertions.assertEquals("where a is null",
                new WhereClause().isNull("a").getSqlWhere());
    }

    @Test
    public void isNotNull() {
        Assertions.assertEquals("where a is not null",
                new WhereClause().isNotNull("a").getSqlWhere());
    }

    @Test
    public void conjunction() {
        //c1 = v1 and c2 = v2
        String sqlWhere1 = new WhereClause().eq("c1", "v1").and().eq("c2", "v2")
                .getSqlWhere();
        Assertions.assertEquals("where c1=#{conditionAst.children[0].value} and c2=#{conditionAst.children[2].value}", sqlWhere1);

        //c1 = v1 and (c2= v2 or c3 = v3)
        String sqlWhere2 = new WhereClause()
                .eq("c1", "v1")
                .andNested(
                        (nested) -> nested.eq("c2", "v2").or().eq("c3", "v3")
                ).getSqlWhere();
        Assertions.assertEquals("where c1=#{conditionAst.children[0].value} and (c2=#{conditionAst.children[2].children[0].value}" +
                        " or " +
                        "c3=#{conditionAst.children[2].children[2].value})",
                sqlWhere2);

        //c1 = v1 or c2 = v2
        String sqlWhere3 = new WhereClause().eq("c1", "v1").or().eq("c2", "v2")
                .getSqlWhere();
        Assertions.assertEquals("where c1=#{conditionAst.children[0].value} or c2=#{conditionAst.children[2].value}", sqlWhere3);

        //c1 = v1 or (c2 = v2 and c3 = v3)
        String sqlWhere4 = new WhereClause()
                .eq("c1", "v1")
                .orNested(
                        (nested) -> nested.eq("c2", "v2").and().eq("c3", "v3")
                ).getSqlWhere();
        Assertions.assertEquals("where c1=#{conditionAst.children[0].value} or (c2=#{conditionAst.children[2].children[0].value} and c3=#{conditionAst.children[2].children[2].value})", sqlWhere4);

        //(c1 = v1 and c2 = v2) or (c3 = v3 and c4 = v4)
        String sqlWhere5 = new WhereClause()
                .nested(nested -> nested.eq("c1", "v1").and().eq("c2", "v2"))
                .or()
                .nested(nested -> nested.eq("c3", "v3").and().eq("c4", "v4"))
                .getSqlWhere();
        Assertions.assertEquals("where " +
                "(c1=#{conditionAst.children[0].children[0].value} and c2=#{conditionAst.children[0].children[2].value})" +
                " or " +
                "(c3=#{conditionAst.children[2].children[0].value} and c4=#{conditionAst.children[2].children[2].value})", sqlWhere5);

        //(c1 = v1 and (c2 = v2 or c3 = v3 )) or (c4 = v4 and (c5 = v5 or c6 = v6))
        String sqlWhere6 = new WhereClause()
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
                "(c1=#{conditionAst.children[0].children[0].value} and " +
                "(c2=#{conditionAst.children[0].children[2].children[0].value} or c3=#{conditionAst.children[0].children[2].children[2].value}))" +
                " or " +
                "(" +
                "c4=#{conditionAst.children[2].children[0].value} and " +
                "(c5=#{conditionAst.children[2].children[2].children[0].value} or c6=#{conditionAst.children[2].children[2].children[2].value})" +
                ")", sqlWhere6);
    }
}
