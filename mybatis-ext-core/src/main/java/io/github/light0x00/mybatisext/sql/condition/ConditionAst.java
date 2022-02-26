package io.github.light0x00.mybatisext.sql.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
where -> 'where' condition | ε
condition -> expr |  expr ('and'|'or')  condition | nested_condition
nested_condition -> '(' condition  ')'
expr -> eq | neq | in | not in | gt | gte | lt | lte | between | not between | like | not like | is null | is not null
*/

/**
 * @author light
 * @since 2022/2/21
 */
public class ConditionAst {

    public enum ASTNodeType {
        WHERE,
        CONDITION, NESTED_CONDITION, OR, AND, EQ, NE, IN, NOT_IN,
        GT, GTE, LT, LTE, BETWEEN, NOT_BETWEEN,
        LIKE, NOT_LIKE,
        IS_NULL, IS_NOT_NULL,
        GROUP_BY, HAVING, OrderBy, LIMIT
    }

    @Getter
    public static class Where extends ASTNode {
        private Condition condition;

        public Where(Condition condition) {
            super(ASTNodeType.WHERE);
            this.condition = condition;
        }
    }

    public static abstract class ASTNode {
        @Getter
        @Setter
        private String paramSymbol;
        @Getter
        protected ASTNodeType type;

        public ASTNode(ASTNodeType type) {
            this.type = type;
        }
    }

    @Getter
    public static class Condition extends ASTNode {
        private List<ASTNode> children;

        public Condition(List<ASTNode> children) {
            super(ASTNodeType.CONDITION);
            this.children = children;
        }
    }

    @Getter
    public static class NestedCondition extends ASTNode {
        private List<ASTNode> children;

        public NestedCondition(List<ASTNode> children) {
            super(ASTNodeType.NESTED_CONDITION);
            this.children = children;
        }
    }

    public static class Or extends ASTNode {
        public Or() {
            super(ASTNodeType.OR);
        }
    }

    public static class And extends ASTNode {
        public And() {
            super(ASTNodeType.AND);
        }
    }

    @Getter
    public static class ValueMatch extends ASTNode {
        private String column;
        private Object value;

        public ValueMatch(ASTNodeType type, String column, Object value) {
            super(type);
            this.column = column;
            this.value = value;
        }

        public ValueMatch(ASTNodeType type, String column) {
            super(type);
            this.column = column;
        }
    }

    @Getter
    public static class MultiValueMatch extends ASTNode {
        private String column;
        private List<Object> values;
        @Setter
        private List<String> valuesParamSymbols;

        public MultiValueMatch(ASTNodeType type, String column, List<Object> values) {
            super(type);
            this.column = column;
            this.values = values;
        }
    }

    @Getter
    public static class Range extends ASTNode {
        private String column;
        private Object value;

        public Range(ASTNodeType type, String column, Object value) {
            super(type);
            this.column = column;
            this.value = value;
        }
    }

    @Getter
    public static class BinaryRange extends ASTNode {
        private String column;
        private Object begin;
        private Object end;

        @Setter
        private String beginParamSymbol;
        @Setter
        private String endParamSymbol;

        public BinaryRange(ASTNodeType type, String column, Object begin, Object end) {
            super(type);
            this.column = column;
            this.begin = begin;
            this.end = end;
        }
    }

}
