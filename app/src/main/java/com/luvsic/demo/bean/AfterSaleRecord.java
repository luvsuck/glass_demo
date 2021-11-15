package com.luvsic.demo.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author: zyy
 * @since: 2021/10/27
 */
@Data
@ToString
@NoArgsConstructor
public class AfterSaleRecord {
    /**
     * SHXC9001 开始流水
     */
    private String formNo;
    private String createDate;
    private String clientName;
    private String deviceLocation;
    private String liaison;
    private String phone;
    private String fax;
    private String deviceNo;
    private String supportByWho;
    private String liftSpec;
    private String deviceSpec;
    private String supportGroupByWho;
    private String returnVisitLiaison;
    private String returnVisitPhone;
    private String returnVisitByWho;
    private String faultDescription;
    private String transactionRecord;
    private String afterSaleSummary;
    private String afterSaleDate;
    /**
     * 客户反映情况 附件
     */
    private String faultDescriptionAtt;
    /**
     * 现场情况及处理记录 附件
     */
    private String transactionRecordAtt;
    /**
     * 售后总结 附件
     */
    private String afterSaleSummaryAtt;

    /**
     * 服务状态 -1取消 0创建 1完结
     */
    private int taskStatus;

    /**
     * 图片附件组,多张图片用逗号(分割) 例如: 1.png,2.jpg
     */
    private String attachmentsImg;

    /**
     * 防伪码 仅作为dto传递用
     */
    private String antiCode;


    /**
     * 生产日期 仅作为dto传递用
     */
    private String manuDate;
}
