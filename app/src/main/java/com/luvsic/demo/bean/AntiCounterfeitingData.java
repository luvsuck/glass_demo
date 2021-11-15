package com.luvsic.demo.bean;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author zyy
 * @since 2021/10/13 09:44
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class AntiCounterfeitingData {

    private static final long serialVersionUID = 1L;

    private Date 创建时间;

    private Integer 编码长度;

    private Integer 编码个数;

    private String ExcelServerRCID;

    private Integer ExcelServerRN;
    private Integer ExcelServerCN;

    private String ExcelServerRC1;

    private String ExcelServerWIID;

    private String ExcelServerRTID;

    private Integer ExcelServerCHG;

    private Integer 创建序号;

}
