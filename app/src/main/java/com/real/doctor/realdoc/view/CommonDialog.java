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

public class CommonDialog {

    private View view;
    private Context context;
    private Dialog dialog;
    private Display display;
    private LinearLayout commonLinear;
    private TextView content;
    private TextView cancel;
    private TextView confirm;

    public interface CancelListener {
        void onCancelListener();
    }

    public interface ConfirmListener {
        void onConfrimClick();
    }

    public CommonDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public CommonDialog builder() {
        view = LayoutInflater.from(context).inflate(R.layout.common_dialog, null);
        commonLinear = view.findViewById(R.id.common_dialog);
        content = view.findViewById(R.id.content);
        cancel = view.findViewById(R.id.cancel);
        confirm = view.findViewById(R.id.confirm);
        dialog = new Dialog(context, R.style.FloatDialogImage);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        commonLinear.setLayoutParams(new ScrollView.LayoutParams((int) (display
                .getWidth() * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT));
        return this;
    }

    public CommonDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public CommonDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public CommonDialog setCancelClickBtn(final CancelListener cancellistener) {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancellistener != null)
                    cancellistener.onCancelListener();
                dialog.dismiss();
            }
        });
        return this;
    }

    public CommonDialog setConfirmClickBtn(final ConfirmListener confrimlistener) {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confrimlistener != null)
                    confrimlistener.onConfrimClick();
                dialog.dismiss();
            }
        });
        return this;
    }

    public CommonDialog show() {
        dialog.show();
        return this;
    }

    public CommonDialog setContent(String contentStr) {
        content.setText(contentStr);
        return this;
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
