package io.github.light0x00.mybatisext;

import io.github.light0x00.mybatisext.sql.DeleteCondition;
import io.github.light0x00.mybatisext.sql.InsertCondition;
import io.github.light0x00.mybatisext.sql.SelectCondition;
import io.github.light0x00.mybatisext.sql.UpdateCondition;
import io.github.light0x00.mybatisext.toolkit.MyBatisScripts;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.io.Serializable;

/**
 * @author light
 * @since 2022/2/18
 */
public class BaseMapperSqlSourceProvider {

    public static String insert(Object entity, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        return script("insert into %s (%s) values(%s)",
                tableInfo.getFullTableName(),
                MyBatisScripts.getSqlColumnsFragment("", tableInfo),
                MyBatisScripts.getSqlValuesFragment("", tableInfo)
        );
    }

    public static String insertIgnore(Object entity, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        return script("insert ignore into %s (%s) values(%s)",
                tableInfo.getFullTableName(),
                MyBatisScripts.getSqlColumnsFragment("", tableInfo),
                MyBatisScripts.getSqlValuesFragment("", tableInfo)
        );
    }

    public static String insertOnDupKey(Object entity, InsertCondition condition, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        String sqlColumns = entity == null ? condition.getSqlColumns() : MyBatisScripts.getSqlColumnsFragment("param1.", tableInfo);
        String sqlValues = entity == null ? condition.getSqlValues("param2") : MyBatisScripts.getSqlValuesFragment("param1.", tableInfo);
        return script("insert into %s (%s) values(%s) ON DUPLICATE KEY UPDATE %s",
                tableInfo.getFullTableName(),
                sqlColumns,
                sqlValues,
                condition.getSqlOnDupKey("param2"));
    }

    public static String deleteById(Serializable id, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        return script("delete from %s where %s=#{0}",
                tableInfo.getFullTableName(),
                tableInfo.getPrimary().getColumn());
    }

    public static String delete(DeleteCondition condition, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        return script("delete from %s %s",
                tableInfo.getFullTableName(),
                condition.getSqlWhere());
    }

    public static String updateById(Object entity, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        return script("update %s %s where %s=#{%s}",
                tableInfo.getFullTableName(),
                MyBatisScripts.getSqlSetFragment("", tableInfo),
                tableInfo.getPrimary().getColumn(),
                tableInfo.getPrimary().getFiledName());
    }

    public static String update(Object entity, UpdateCondition where, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        return script("update %s %s %s",
                tableInfo.getFullTableName(),
                MyBatisScripts.getSqlSetFragment("param1", tableInfo),
                where.getSqlWhere("param2"));
    }

    public static String updateByCondition(UpdateCondition condition, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        String tableName = tableInfo.getFullTableName();
        String sqlSet = condition.getSqlSet("");
        String sqlWhere = condition.getSqlWhere("");
        return script("update %s %s %s", tableName, sqlSet, sqlWhere);
    }

    public static String getById(Serializable id, ProviderContext context) {
        TableInfo tableInfo = TableInfoHolder.get(context.getMapperType());
        return script("select * from %s where %s=#{0}",
                tableInfo.getFullTableName(),
                tableInfo.getPrimary().getColumn());
    }

    public static String select(SelectCondition condition, ProviderContext context) {
        String tableName = TableInfoHolder.get(context.getMapperType()).getFullTableName();
        String sqlColumns = condition.getSqlColumns();
        String sqlWhere = condition.getSqlWhere("");
        String sqlGroupBy = condition.getSqlGroupBy();
        String sqlHaving = condition.getSqlHaving("");
        String sqlOrderBy = condition.getSqlOrderBy();
        return script("select %s from %s %s %s %s %s", sqlColumns, tableName, sqlWhere, sqlGroupBy, sqlHaving, sqlOrderBy);
    }

    public static String selectMaps(SelectCondition condition, ProviderContext context) {
        return select(condition, context);
    }

    public static String selectOne(SelectCondition condition, ProviderContext context) {
        return select(condition, context);
    }

    public static String selectCount(SelectCondition condition, ProviderContext context) {
        String tableName = TableInfoHolder.get(context.getMapperType()).getFullTableName();
        return script("select count(1) from %s %s", tableName, condition.getSqlWhere());
    }

    public static String selectCursor(SelectCondition condition, ProviderContext context) {
        return select(condition, context);
    }

    public static String selectStreaming(SelectCondition condition, ProviderContext context) {
        return select(condition, context);
    }

    private static String script(String sqlScript, Object... args) {
        return MyBatisScripts.script(String.format(sqlScript, args));
    }
}
