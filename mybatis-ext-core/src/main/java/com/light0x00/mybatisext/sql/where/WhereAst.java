package com.light0x00.mybatisext.sql.where;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author light
 * @since 2022/2/21
 */
public class WhereAst {

    @Getter
    @Setter
    public static abstract class ASTNode {
        private String paramSymbol;
    }

    @AllArgsConstructor
    @Getter
    public static class Clause extends ASTNode {
        private List<ASTNode> children;
    }

    @AllArgsConstructor
    @Getter
    public static class NestedClause extends ASTNode {
        private List<ASTNode> children;
    }

    public interface ConjunctionOperator {
    }

    public static class Or extends ASTNode implements ConjunctionOperator {
    }

    public static class And extends ASTNode implements ConjunctionOperator {

    }

    @AllArgsConstructor
    @Getter
    public static class Eq extends ASTNode {
        private String column;
        private Object value;
    }

    @AllArgsConstructor
    @Getter
    public static class Ne extends ASTNode {
        private String column;
        private Object value;
    }

    @AllArgsConstructor
    @Getter
    public static class In extends ASTNode {
        private String column;
        private List<Object> values;
        private List<String> valueAccessSymbols;

        public In(String column, List<Object> values) {
            this.column = column;
            this.values = values;
        }

        public void setValueAccessSymbols(List<String> valueAccessSymbols) {
            this.valueAccessSymbols = valueAccessSymbols;
        }
    }
}
