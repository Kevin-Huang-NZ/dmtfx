package com.mahara.stocker.util;

public class PaginationIn {
    private int pageNo;
    private int pageSize;

    public PaginationIn() {
        this(1, 10);
    }

    public PaginationIn(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
