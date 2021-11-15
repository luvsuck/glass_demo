package com.luvsic.demo.bean;



import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Data;

/**
 * @author zyy
 * @since 2021/10/12 10:20
 */
@Data
public class AfterServiceHeader {
    private String 防伪码;
    private String 安全器编号;
    private String 生产日期;
    private String 客户名称;
    private String 安全器型号;

    public AfterServiceHeader(ResultSet resultSet) throws SQLException {
        防伪码 = resultSet.getString("防伪码");
        安全器编号 = resultSet.getString("安全器编号");
        生产日期 = resultSet.getString("生产日期");
        客户名称 = resultSet.getString("客户名称");
        安全器型号 = resultSet.getString("安全器型号");
    }

    public String get防伪码() {
        return 防伪码;
    }

    public void set防伪码(String 防伪码) {
        this.防伪码 = 防伪码;
    }

    public String get安全器编号() {
        return 安全器编号;
    }

    public void set安全器编号(String 安全器编号) {
        this.安全器编号 = 安全器编号;
    }

    public String get生产日期() {
        return 生产日期;
    }

    public void set生产日期(String 生产日期) {
        this.生产日期 = 生产日期;
    }

    @Override
    public String toString() {
        return "AfterServiceHeader{" +
                "防伪码='" + 防伪码 + '\'' +
                ", 安全器编号='" + 安全器编号 + '\'' +
                ", 生产日期='" + 生产日期 + '\'' +
                ", 客户名称='" + 客户名称 + '\'' +
                ", 安全器型号='" + 安全器型号 + '\'' +
                '}';
    }

    public String get客户名称() {
        return 客户名称;
    }

    public void set客户名称(String 客户名称) {
        this.客户名称 = 客户名称;
    }

    public String get安全器型号() {
        return 安全器型号;
    }

    public void set安全器型号(String 安全器型号) {
        this.安全器型号 = 安全器型号;
    }
}
