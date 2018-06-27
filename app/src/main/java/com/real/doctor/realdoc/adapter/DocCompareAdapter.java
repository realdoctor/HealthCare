package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.CellBean;
import com.real.doctor.realdoc.model.ColBean;
import com.real.doctor.realdoc.model.RowBean;
import com.real.doctor.realdoc.view.excelpanel.BaseExcelPanelAdapter;

/**
 * Created by zhujiabin on 2018/5/21.
 */

public class DocCompareAdapter extends BaseExcelPanelAdapter<RowBean, ColBean, CellBean> {

    private Context context;
    private View.OnClickListener blockListener;

    public DocCompareAdapter(Context context, View.OnClickListener blockListener) {
        super(context);
        this.context = context;
        this.blockListener = blockListener;
    }

    //=========================================content's cell===========================================
    @Override
    public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_compare_table_item, parent, false);
        CellHolder cellHolder = new CellHolder(layout);
        return cellHolder;
    }

    @Override
    public void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition) {
        CellBean cell = getMajorItem(verticalPosition, horizontalPosition);
        if (null == holder || !(holder instanceof CellHolder) || cell == null) {
            return;
        }
        CellHolder viewHolder = (CellHolder) holder;
        viewHolder.cellContainer.setTag(cell);
        viewHolder.cellContainer.setOnClickListener(blockListener);
        viewHolder.content.setText(cell.getContent());
    }

    static class CellHolder extends RecyclerView.ViewHolder {

        public final TextView content;
        public final LinearLayout cellContainer;

        public CellHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);
            cellContainer = (LinearLayout) itemView.findViewById(R.id.cell_container);
        }
    }


    //=========================================top cell===========================================
    @Override
    public RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_compare_table_header, parent, false);
        TopHolder topHolder = new TopHolder(layout);
        return topHolder;
    }

    @Override
    public void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position) {
        RowBean rowTitle = getTopItem(position);
        if (null == holder || !(holder instanceof TopHolder) || rowTitle == null) {
            return;
        }
        TopHolder viewHolder = (TopHolder) holder;
        viewHolder.headerContent.setText(rowTitle.getHeaderContent());
    }

    static class TopHolder extends RecyclerView.ViewHolder {

        public final TextView headerContent;

        public TopHolder(View itemView) {
            super(itemView);
            headerContent = (TextView) itemView.findViewById(R.id.header_content);
        }
    }

    //=========================================left cell===========================================
    @Override
    public RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_compare_table_left, parent, false);
        LeftHolder leftHolder = new LeftHolder(layout);
        return leftHolder;
    }

    @Override
    public void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position) {
        ColBean colTitle = getLeftItem(position);
        if (null == holder || !(holder instanceof LeftHolder) || colTitle == null) {
            return;
        }
        LeftHolder viewHolder = (LeftHolder) holder;
        viewHolder.leftContent.setText(colTitle.getNumber());
        ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
        viewHolder.root.setLayoutParams(lp);
    }

    static class LeftHolder extends RecyclerView.ViewHolder {

        public final TextView leftContent;
        public final View root;

        public LeftHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            leftContent = (TextView) itemView.findViewById(R.id.left_content);
        }
    }

    //=========================================left-top cell===========================================
    @Override
    public View onCreateTopLeftView() {
        return LayoutInflater.from(context).inflate(R.layout.doc_compare_table_item, null);
    }
}
