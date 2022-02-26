package io.github.light0x00.mybatisext;

import io.github.light0x00.mybatisext.sql.SelectCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lighto
 * @since 2022/2/21
 */
public class ConditionTest {

    @Test
    public void eq() {
        Assertions.assertEquals("where a=#{conditionAst.children[0].value}", new SelectCondition().eq("a", 1).getSqlWhere());
    }

    @Test
    public void ne() {
        Assertions.assertEquals("where a&lt;&gt;#{conditionAst.children[0].value}", new SelectCondition().ne("a", 1).getSqlWhere());
    }

    @Test
    public void in() {
        Assertions.assertEquals("where a in (#{conditionAst.children[0].values[0]},#{conditionAst.children[0].values[1]})",
                new SelectCondition().in("a", 1, 3).getSqlWhere());
    }

    @Test
    public void notIn() {
        Assertions.assertEquals("where a not in (#{conditionAst.children[0].values[0]},#{conditionAst.children[0].values[1]})",
                new SelectCondition().notIn("a", 1, 3).getSqlWhere());
    }

    @Test
    public void gt() {
        Assertions.assertEquals("where a&gt;#{conditionAst.children[0].value}",
                new SelectCondition().gt("a", 1).getSqlWhere());
    }

    @Test
    public void gte() {
        Assertions.assertEquals("where a&gt;=#{conditionAst.children[0].value}",
                new SelectCondition().gte("a", 1).getSqlWhere());
    }


    @Test
    public void lt() {
        Assertions.assertEquals("where a&lt;#{conditionAst.children[0].value}",
                new SelectCondition().lt("a", 1).getSqlWhere());
    }

    @Test
    public void lte() {
        Assertions.assertEquals("where a&lt;=#{conditionAst.children[0].value}",
                new SelectCondition().lte("a", 1).getSqlWhere());
    }

    @Test
    public void between() {
        Assertions.assertEquals("where a between #{conditionAst.children[0].begin} and #{conditionAst.children[0].end}",
                new SelectCondition().between("a", 1, 2).getSqlWhere());
    }

    @Test
    public void notBetween() {
        Assertions.assertEquals("where a not between #{conditionAst.children[0].begin} and #{conditionAst.children[0].end}",
                new SelectCondition().notBetween("a", 1, 2).getSqlWhere());
    }

    @Test
    public void like() {
        Assertions.assertEquals("where a like #{conditionAst.children[0].value}",
                new SelectCondition().like("a", "%a").getSqlWhere());
    }

    @Test
    public void notLike() {
        Assertions.assertEquals("where a not like #{conditionAst.children[0].value}",
                new SelectCondition().notLike("a", "%a").getSqlWhere());
    }

    @Test
    public void isNull() {
        Assertions.assertEquals("where a is null",
                new SelectCondition().isNull("a").getSqlWhere());
    }

    @Test
    public void isNotNull() {
        Assertions.assertEquals("where a is not null",
                new SelectCondition().isNotNull("a").getSqlWhere());
    }

    @Test
    public void conjunction() {
        //c1 = v1 and c2 = v2
        String sqlWhere1 = new SelectCondition().eq("c1", "v1").and().eq("c2", "v2")
                .getSqlWhere();
        Assertions.assertEquals("where c1=#{conditionAst.children[0].value} and c2=#{conditionAst.children[2].value}", sqlWhere1);

        //c1 = v1 and (c2= v2 or c3 = v3)
        String sqlWhere2 = new SelectCondition()
                .eq("c1", "v1")
                .andNested(
                        (nested) -> nested.eq("c2", "v2").or().eq("c3", "v3")
                ).getSqlWhere();
        Assertions.assertEquals("where c1=#{conditionAst.children[0].value} and (c2=#{conditionAst.children[2].children[0].value}" +
                        " or " +
                        "c3=#{conditionAst.children[2].children[2].value})",
                sqlWhere2);

        //c1 = v1 or c2 = v2
        String sqlWhere3 = new SelectCondition().eq("c1", "v1").or().eq("c2", "v2")
                .getSqlWhere();
        Assertions.assertEquals("where c1=#{conditionAst.children[0].value} or c2=#{conditionAst.children[2].value}", sqlWhere3);

        //c1 = v1 or (c2 = v2 and c3 = v3)
        String sqlWhere4 = new SelectCondition()
                .eq("c1", "v1")
                .orNested(
                        (nested) -> nested.eq("c2", "v2").and().eq("c3", "v3")
                ).getSqlWhere();
        Assertions.assertEquals("where c1=#{conditionAst.children[0].value} or (c2=#{conditionAst.children[2].children[0].value} and c3=#{conditionAst.children[2].children[2].value})", sqlWhere4);

        //(c1 = v1 and c2 = v2) or (c3 = v3 and c4 = v4)
        String sqlWhere5 = new SelectCondition()
                .nested(nested -> nested.eq("c1", "v1").and().eq("c2", "v2"))
                .or()
                .nested(nested -> nested.eq("c3", "v3").and().eq("c4", "v4"))
                .getSqlWhere();
        Assertions.assertEquals("where " +
                "(c1=#{conditionAst.children[0].children[0].value} and c2=#{conditionAst.children[0].children[2].value})" +
                " or " +
                "(c3=#{conditionAst.children[2].children[0].value} and c4=#{conditionAst.children[2].children[2].value})", sqlWhere5);

        //(c1 = v1 and (c2 = v2 or c3 = v3 )) or (c4 = v4 and (c5 = v5 or c6 = v6))
        String sqlWhere6 = new SelectCondition()
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
