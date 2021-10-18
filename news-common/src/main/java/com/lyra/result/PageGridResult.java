package com.lyra.result;

import java.util.List;

public class PageGridResult {
    // 当前页数
    private Long page;
    // 总页数
    private Long total;
    // 总记录数
    private long records;
    // 每行显示的内容
    private List<?> rows;


    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
