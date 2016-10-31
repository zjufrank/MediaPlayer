package com.frank.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MusicService extends Service {

	private MediaPlayer mp;
	private String path;

	@Override
	public IBinder onBind(Intent intent) {
		path = intent.getStringExtra("path");
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("onCreate");
		try {
			mp = new MediaPlayer();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void pause() {
		mp.pause();
	}
	
	public void resume() {
		mp.start();
	}

	class MyBinder extends Binder implements IMusic {
		
		private SeekBar seekbar;
		Handler handle = new Handler() {};
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (mp.isPlaying()) {
					seekbar.setProgress(mp.getCurrentPosition());
				}
				handle.postDelayed(runnable, 500);
			}
		};

		public void dopause() {
			pause();
		}  

		public void doresume() {
			resume();
		}
		public void doremove(){
			handle.removeCallbacks(runnable);
		}
     
		public void init(SeekBar seekbar,final onMusicOver over) {
			System.out.println("*********init******");
			this.seekbar = seekbar;
			mp.reset();
			try {
				mp.setDataSource(path);
				mp.prepare();
				seekbar.setMax(mp.getDuration());
				seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override public void onStopTrackingTouch(SeekBar seekBar) { }
					@Override public void onStartTrackingTouch(SeekBar seekBar) { }
					@Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if(fromUser){
							mp.seekTo(progress);
						}
					}
				});
				mp.start();
				handle.post(runnable);
				mp.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						over.onMusicComplete();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void dostart() {
			try {
				mp.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void dostop() {
			if (mp != null && mp.isPlaying()) {
				mp.stop();
				mp.release();
				mp = null;
			}
		}

		public void doRset() {
			mp.release();
		}
	}
}
