package com.lv.note.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.lv.note.R;


/**
 * User: 吕勇
 * Date: 2016-03-17
 * Time: 17:40
 * Description:加载对话框
 */
public class LoadingDialog extends Dialog {

    private boolean outSideCancele = false;
    public LoadingDialog(Context context) {
        this(context, false);
    }

    public LoadingDialog(Context context, boolean outSideCancele) {
        super(context, R.style.loading_dialog_style);
        this.outSideCancele = outSideCancele;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        setCanceledOnTouchOutside(outSideCancele);
    }
}
