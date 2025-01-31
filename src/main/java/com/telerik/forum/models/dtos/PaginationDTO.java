package com.telerik.forum.models.dtos;

import java.util.List;

public class  PaginationDTO <T> {

    private List<T> entityList;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public PaginationDTO() {}

    public PaginationDTO(List<T> entities, int pageNumber, int pageSize, long totalElements, int totalPages) {
        this.entityList = entities;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<T> entityList) {
        this.entityList = entityList;
    }
}