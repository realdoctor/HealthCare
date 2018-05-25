package com.real.doctor.realdoc.model;

import java.util.ArrayList;

/**
 * Created by ZFT on 2018/5/8.
 */

public class PageModel<T> {
    public ArrayList<T> list;
    public int total=-1;
    public int  pages=-1;
    public int pageSize=10;
    public int pageNum=-1;
}
