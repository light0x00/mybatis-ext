package io.github.light0x00.mybatisext.sql;

/**
 * @author light
 * @since 2022/2/25
 */
public class DeleteCondition extends WhereCondition<DeleteCondition> {
    @Override
    protected DeleteCondition newNestedInstance() {
        return new DeleteCondition();
    }
}
