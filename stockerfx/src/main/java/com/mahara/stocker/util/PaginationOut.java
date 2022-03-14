package com.mahara.stocker.util;

import java.util.ArrayList;
import java.util.List;

public class PaginationOut<T> {
    private int pageNo;
    private int pageSize;
    private int totalSize = 0;
    private int totalPage = 0;
    private List<T> data = null;

    public PaginationOut() {
        this(1, 10);
    }

    public PaginationOut(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PaginationOut(int pageNo, int pageSize, List<T> data) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.data = data;
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

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

//    public int getNextPageNo() {
//        var nextPageNo = pageNo + 1;
//        if (nextPageNo > totalPage) {
//            nextPageNo = totalPage;
//        }
//        return nextPageNo;
//    }
//
//    public int getPreviousPageNo() {
//        var previousPageNo = pageNo - 1;
//        if (previousPageNo < 1) {
//            previousPageNo = 1;
//        }
//        return previousPageNo;
//    }
}
