package com.cyecize.baserepository.pagination;

public interface Pageable {

    static Pageable of(int page, int size) {
        return new Pageable() {
            @Override
            public int getPage() {
                return Math.max(1, page);
            }

            @Override
            public int getSize() {
                return Math.max(0, size);
            }
        };
    }

    int getPage();

    int getSize();
}
