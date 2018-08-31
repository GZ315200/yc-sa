package com.unistack.tamboo.mgt.common.page;

/**
 * @author Gyges Zean
 * @date 2017/11/30
 * 用于分页底层实现
 */
public class Pagination {

    private long pageIndex;
    private long totalPage;
    private long resultSize;
    private long pageSize;

    public long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getResultSize() {
        return resultSize;
    }

    public void setResultSize(long resultSize) {
        this.resultSize = resultSize;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
}
