package com.cyecize.baserepository.pagination;

import java.util.List;

public interface Page<T> {

    int getPage();

    int getSize();

    int getTotalPages();

    long getTotalItems();

    List<T> getItems();
}
