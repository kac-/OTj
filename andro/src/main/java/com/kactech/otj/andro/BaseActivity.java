package com.kactech.otj.andro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

public class BaseActivity extends Activity {
	ProgressDialog progress;
	Toast toast;
	Thread actionThread;
	BaseHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	void msg(String text, Integer... args) {
		Message msg = new Message();
		msg.obj = text;
		if (args.length > 0 && args[0] != null)
			msg.what = args[0];
		if (args.length > 1 && args[1] != null)
			msg.arg1 = args[1];
		if (args.length > 2 && args[2] != null)
			msg.arg2 = args[2];
		handler.sendMessage(msg);
	}

	void work(Runnable work, String title) {
		work(work, title, title);
	}

	void work(Runnable work, String title, String msg) {
		if (handler == null)
			throw new IllegalStateException("handler not set");
		progress = ProgressDialog.show(this, title, msg);
		actionThread = new Thread(work);
		actionThread.start();
	}

	public static class BaseHandler extends Handler {
		BaseActivity ctx;

		public BaseHandler(BaseActivity ctx) {
			super();
			this.ctx = ctx;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof String)
				ctx.dismiss((String) msg.obj);
			else
				ctx.dismiss(null);
		}
	}

	void toast(String msg) {
		if (toast == null) {
			toast = Toast.makeText(this, msg, 500);
			toast.setGravity(Gravity.TOP, 0, 0);
		} else
			toast.setText((String) msg);
		toast.show();
	}

	void dismiss(String txt) {
		if (progress != null && progress.isShowing())
			progress.dismiss();
		if (txt != null)
			toast(txt);
	}
}
