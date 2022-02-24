package com.light0x00.mybatisext;

import com.light0x00.mybatisext.toolkit.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author light
 * @since 2022/2/19
 */
@Data
@Accessors(chain = true)
public class TableInfo {
    private String schema;
    private String tableName;
    private Class<?> entityType;
    private List<ColumnFieldMapping> mappings;
    private ColumnFieldMapping primary;

    private String sqlSetScrip;

    @Data
    public static class ColumnFieldMapping {
        private String column;
        private String filedName;
        private Field filed;

        public ColumnFieldMapping(String column, Field filed) {
            this.column = column;
            this.filed = filed;
            this.filedName = filed.getName();
        }
    }

    public String getPrimaryKey() {
        return primary.column;
    }

    public String getFullTableName() {
        return StringUtils.isBlank(schema) ? tableName : schema + "." + tableName;
    }

}
