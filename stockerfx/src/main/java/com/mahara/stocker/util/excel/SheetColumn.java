package com.mahara.stocker.util.excel;

public class SheetColumn {
    private String title;
    private Integer width;
    private String comment;

    public SheetColumn(String title) {
        this(title, 200);
    }

    public SheetColumn(String title, int width) {
        this(title, width, null);
    }

    public SheetColumn(String title, int width, String comment) {
        this.title = title;
        this.width = width;
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public Integer getWidth() {
        return width;
    }

    public String getComment() {
        return comment;
    }
}
