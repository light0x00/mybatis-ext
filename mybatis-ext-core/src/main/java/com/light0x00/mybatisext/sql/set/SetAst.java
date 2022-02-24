package com.light0x00.mybatisext.sql.set;

import com.light0x00.mybatisext.toolkit.MyBatisScripts;
import com.light0x00.mybatisext.toolkit.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author light
 * @since 2022/2/22
 */
public class SetAst {

    public static abstract class ASTNode {
        @Setter
        @Getter
        protected String accessSymbol;

        public abstract String eval();
    }

    @AllArgsConstructor
    public static class Set extends ASTNode {
        @NotNull
        private List<ASTNode> children;
        private String accessSymbol;
        private boolean setPrefix = true;

        public Set(@NotNull List<ASTNode> children, String paramSymbol) {
            this.children = children;
            this.accessSymbol = paramSymbol;
        }

        @Override
        public String eval() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < children.size(); i++) {
                children.get(i).setAccessSymbol(StringUtils.combineWithExactlyOneDot(accessSymbol, "children[" + i + "]"));
                sb.append(children.get(i).eval());
                sb.append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            if (sb.length() == 0) {
                return "";
            }
            return setPrefix ? "set " + sb : sb.toString();
        }
    }

    @AllArgsConstructor
    public static class SetValue extends ASTNode {
        private String column;
        private Object value;

        @Override
        public String eval() {
            return column + "=" + MyBatisScripts.hashExpr(accessSymbol + ".value");
        }
    }

    @AllArgsConstructor
    public static class IncrValue extends ASTNode {
        private String column;
        private Number value;

        @Override
        public String eval() {
            return column + "=" + column + "+" + MyBatisScripts.hashExpr(accessSymbol + ".value");
        }
    }

    @AllArgsConstructor
    public static class DecrValue extends ASTNode {
        private String column;
        private Number value;

        @Override
        public String eval() {
            return column + "=" + column + "-" + MyBatisScripts.hashExpr(accessSymbol + ".value");
        }
    }
}
