package com.real.doctor.realdoc.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;

public class ImageCardDialog {

    private View view;
    private Context context;
    private Dialog dialog;
    private Display display;
    private LinearLayout imageCardLinear;
    private TextView replaceImage;
    private TextView addAdvice;

    public interface ReplaceListener {
        void onReplaceListener();
    }

    public interface AdviceListener {
        void onAdviceClick();
    }

    public ImageCardDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public ImageCardDialog builder() {
        view = LayoutInflater.from(context).inflate(R.layout.image_card_dialog, null);
        imageCardLinear = view.findViewById(R.id.image_card_dialog);
        replaceImage = view.findViewById(R.id.replace_image);
        addAdvice = view.findViewById(R.id.add_advice);
        dialog = new Dialog(context, R.style.FloatDialogImage);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        imageCardLinear.setLayoutParams(new ScrollView.LayoutParams((int) (display
                .getWidth() * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT));
        return this;
    }

    public ImageCardDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public ImageCardDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public ImageCardDialog setClickBtn(final ReplaceListener replacelistener) {
        replaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replacelistener != null)
                    replacelistener.onReplaceListener();
                dialog.dismiss();
            }
        });
        return this;
    }

    public ImageCardDialog setAdviceClickBtn(final AdviceListener advicelistener) {
        addAdvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advicelistener != null)
                    advicelistener.onAdviceClick();
                dialog.dismiss();
            }
        });
        return this;
    }

    public ImageCardDialog show() {
        dialog.show();
        return this;
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
