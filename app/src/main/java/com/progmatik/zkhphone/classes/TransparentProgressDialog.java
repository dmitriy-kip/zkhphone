package com.progmatik.zkhphone.classes;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.progmatik.zkhphone.R;

public class TransparentProgressDialog extends Dialog {
		
//	private ImageView iv;

	public TransparentProgressDialog(Context context, String message) {
		super(context, R.style.TransparentProgressDialog);
        	WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        	wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        	getWindow().setAttributes(wlmp);
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addContentView(layout, lparams);

		ProgressBar loadingIndicator = new ProgressBar(context, null, android.R.attr.progressBarStyleLargeInverse);
        loadingIndicator.setIndeterminate(true);
        loadingIndicator.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,64);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(loadingIndicator,params);

        if ( !message.equals("") ) {
            TextView loadingLabel = new TextView(context);
            LinearLayout.LayoutParams tparams = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
            loadingLabel.setText(message);
            tparams.setMargins(0, 24, 0, 0);
            layout.addView(loadingLabel,tparams);
        }
	}
		
//	@Override
//	public void show() {
//		super.show();
//		RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
//		anim.setInterpolator(new LinearInterpolator());
//		anim.setRepeatCount(Animation.INFINITE);
//		anim.setDuration(3000);
//		iv.setAnimation(anim);
//		iv.startAnimation(anim);
//	}
}
