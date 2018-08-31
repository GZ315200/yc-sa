package com.unistack.tamboo.mgt.common.page;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2017/11/30
 * 用于分页公共类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationData<T> {

    private Pagination pagination;
    private List<T> pageData;

    public List<T> getPageData() {
        return pageData;
    }

    public void setPageData(List<T> pageData) {
        this.pageData = pageData;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public PaginationData(Pagination pagination, List<T> pageData) {
        this.setPageData(pageData);
        this.setPagination(pagination);
    }
    public PaginationData() {

    }
}
