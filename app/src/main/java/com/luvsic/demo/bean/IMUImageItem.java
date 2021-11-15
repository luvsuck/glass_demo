package com.luvsic.demo.bean;



/**
 * @author zyy
 * @since 2021-10-19
 */
public class IMUImageItem {
    private String title;
    private String desc;
    private int resId;

    public String getTitle() {
        return title;
    }

    public IMUImageItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public IMUImageItem setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public int getResId() {
        return resId;
    }

    public IMUImageItem setResId(int resId) {
        this.resId = resId;
        return this;
    }
}
