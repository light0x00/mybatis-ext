package com.light0x00.mybatisext.test;

import com.light0x00.mybatisext.annotations.Column;
import com.light0x00.mybatisext.annotations.TableName;
import lombok.Data;

@Data
@TableName(schema = "test")
public class User {

    @Column(primary = true)
    private Long pkId;
    private String name;
    private Integer age;
    private String email;

    @Override
    public String toString() {
        return "User{" +
                "pkId=" + pkId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }
}