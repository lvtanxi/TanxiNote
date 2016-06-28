package com.lv.note.widget.progressview;

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
public class ColorfulRingProgressDialog extends Dialog {


    private ColorfulRingProgressView mPercentageRing;

    public ColorfulRingProgressDialog(Context context) {
        super(context, R.style.loading_dialog_style_color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colorfulringprogress_dialog);
        setCanceledOnTouchOutside(false);
        mPercentageRing= (ColorfulRingProgressView) findViewById(R.id.colorful_ring_progressview);
    }
    public void setPercent(int proess){
        mPercentageRing.setPercent(proess);

    }
}
