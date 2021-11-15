package com.luvsic.demo.bean;

/**
 * @author zyy
 * @since 2021/10/13 11:11
 */
public class ImageEntity {
    private String imageUrl;
    private String title;
    private String notes;

    public ImageEntity(String imageUrl, String title, String notes) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.notes = notes;
    }

    public ImageEntity() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ImageEntity setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ImageEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public ImageEntity setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    @Override
    public String toString() {
        return "ImageEntity{" +
                "imageUrl='" + imageUrl + '\'' +
                ", title='" + title + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
