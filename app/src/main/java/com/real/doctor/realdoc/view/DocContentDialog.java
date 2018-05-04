package com.real.doctor.realdoc.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.real.doctor.realdoc.R;

public class DocContentDialog {

    private View view;
    private Context context;
    private Dialog dialog;
    private Display display;
    private TextView docContentText;
    private TextView confirm;
    private String content;
    private LinearLayout docContentLinear;
    private ConfirmListener confirmListener;

    public interface ConfirmListener {
        void onConfirmClick();
    }

    public DocContentDialog(Context context,String content) {
        this.context = context;
        this.content = content;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public DocContentDialog builder() {
        view = LayoutInflater.from(context).inflate(R.layout.doc_content_dialog, null);
        docContentLinear = view.findViewById(R.id.doc_content_dialog);
        docContentText = view.findViewById(R.id.doc_content_text);
        confirm = view.findViewById(R.id.confirm);
        dialog = new Dialog(context, R.style.FloatDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        docContentLinear.setLayoutParams(new ScrollView.LayoutParams((int) (display
                .getWidth() * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT));
        docContentText.setText(content);
        return this;
    }

    public DocContentDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public DocContentDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public DocContentDialog setConfirmBtn(final ConfirmListener listener) {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onConfirmClick();
                dialog.dismiss();
            }
        });
        return this;
    }

    public DocContentDialog show() {
        dialog.show();
        return this;
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
