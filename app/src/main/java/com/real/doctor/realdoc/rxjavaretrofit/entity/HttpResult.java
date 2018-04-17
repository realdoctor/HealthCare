package com.real.doctor.realdoc.rxjavaretrofit.entity;

/**
 * @author zhujiabin
 * @package rxjavaretrofit.entity
 * @fileName ${Name}
 * @Date 2018-1-3 0003
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class HttpResult{

    private int count;
    private int start;
    private int total;
    private String title;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "count=" + count + '\'' +
                ", start=" + start + '\'' +
                ", total=" + total + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
