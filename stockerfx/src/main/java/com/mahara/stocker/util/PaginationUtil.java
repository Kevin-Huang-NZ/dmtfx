package com.mahara.stocker.util;

import java.util.List;

public class PaginationUtil {
    public static int getNextPageNo(int currentPageNo, int totalPage) {
        var nextPageNo = currentPageNo + 1;
        if (nextPageNo > totalPage) {
            nextPageNo = totalPage;
        }
        if (nextPageNo < 1) {
            nextPageNo = 1;
        }
        return nextPageNo;
    }

    public static int getPreviousPageNo(int currentPageNo) {
        var previousPageNo = currentPageNo - 1;
        if (previousPageNo < 1) {
            previousPageNo = 1;
        }
        return previousPageNo;
    }

    public static int getGotoPageNo(String gotoPageNo, int totalPage) {
        if (!CheckUtil.patternCheck(CheckUtil.PATTERN_INT_POSITIVE, gotoPageNo)) {
            return 1;
        }
        int tmpPageNo = Integer.valueOf(gotoPageNo);
        if (tmpPageNo > totalPage) {
            tmpPageNo = totalPage;
        }
        if (tmpPageNo < 1) {
            tmpPageNo = 1;
        }
        return tmpPageNo;
    }
}
