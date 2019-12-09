package com.cyecize.baserepository.pagination;

import java.util.List;

public class PageImpl<T> implements Page<T> {

    private final int currentPage;

    private final int currentPageSize;

    private final List<T> items;

    private final long totalItems;

    private final int totalPages;

    public PageImpl(Pageable pageInfo, List<T> items, long totalItems) {
        this.currentPage = pageInfo.getPage();
        this.currentPageSize = pageInfo.getSize();
        this.items = items;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil(totalItems * 1.0 / pageInfo.getSize());
    }

    @Override
    public int getPage() {
        return this.currentPage;
    }

    @Override
    public int getSize() {
        return this.currentPageSize;
    }

    @Override
    public int getTotalPages() {
        return this.totalPages;
    }

    @Override
    public long getTotalItems() {
        return this.totalItems;
    }

    @Override
    public List<T> getItems() {
        return this.items;
    }
}
