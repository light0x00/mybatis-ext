package io.github.light0x00.mybatisext.toolkit;

import io.github.light0x00.mybatisext.TableInfo;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author light
 * @since 2022/2/20
 */
public class MyBatisScripts {

    public static String script(String source) {
        return "<script> " + source + " </script>";
    }

    public static String hashExpr(String exp) {
        return "#{" + exp + "}";
    }

    public static String dollarExpr(String exp) {
        return "${" + exp + "}";
    }

    public static String getSqlColumnsFragment(String fieldPrefix, TableInfo tableInfo) {
        String columnsFrag = tableInfo.getMappings().stream()
                .map(m -> ifNotNullFragment(fieldPrefix + m.getFiledName(), m.getColumn() + ","))
                .collect(Collectors.joining("\n"));
        return trimFragment(columnsFrag, null, null, null, ",");
    }

    public static String getSqlValuesFragment(String fieldPrefix, TableInfo tableInfo) {
        String valuesFrag = tableInfo.getMappings().stream()
                .map(m -> ifNotNullFragment(fieldPrefix + m.getFiledName(), "#{" + fieldPrefix + m.getFiledName() + "},"))
                .collect(Collectors.joining("\n"));
        return trimFragment(valuesFrag, null, null, null, ",");
    }

    public static String getSqlSetFragment(String fieldPrefix, TableInfo tableInfo) {
        StringBuilder sqlSet = new StringBuilder();
        for (TableInfo.ColumnFieldMapping m : tableInfo.getMappings()) {
            sqlSet.append(equalsIfNotNullFragment(StringUtils.combineWithExactlyOneDot(fieldPrefix, m.getFiledName()), m.getColumn(), null, ","));
            sqlSet.append("\n");
        }
        sqlSet.deleteCharAt(sqlSet.length() - 1);
        return trimFragment(sqlSet.toString(), "set", null, null, ",");
    }

    /*trim*/

    private static String trimFragment(final String innerContent, final String prefix, final String suffix,
                                       final String prefixOverrides, final String suffixOverrides) {
        StringBuilder sb = new StringBuilder("<trim");
        if (StringUtils.isNotBlank(prefix)) {
            sb.append(" prefix=\"").append(prefix).append("\"");
        }
        if (StringUtils.isNotBlank(suffix)) {
            sb.append(" suffix=\"").append(suffix).append("\"");
        }
        if (StringUtils.isNotBlank(prefixOverrides)) {
            sb.append(" prefixOverrides=\"").append(prefixOverrides).append("\"");
        }
        if (StringUtils.isNotBlank(suffixOverrides)) {
            sb.append(" suffixOverrides=\"").append(suffixOverrides).append("\"");
        }
        return sb.append(">").append("\n").append(innerContent).append("\n").append("</trim>").toString();
    }

    /*if*/

    private static String equalsIfNotNullFragment(String fieldName, String columnName) {
        return equalsIfNotNullFragment(fieldName, columnName, null, null);
    }

    private static String equalsIfNotNullFragment(String fieldName, String columnName, String prefix, String suffix) {
        return ifFragment(fieldName + "!=null",
                Optional.ofNullable(prefix).orElse("") +
                        columnName + "=#{" + fieldName + "}" +
                        Optional.ofNullable(suffix).orElse(""));
    }

    private static String ifNotNullFragment(String fieldName, String innerContent) {
        return ifFragment(fieldName + "!=null", innerContent);
    }

    private static String ifFragment(String test, String innerContent) {
        String ifFrag = "<if test=\"%s\"> %s </if>";
        return String.format(ifFrag, test, innerContent);
    }

}
