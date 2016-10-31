package com.frank.mediaplayer;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity2 extends Activity{
	MediaPlayer mp;
	SeekBar seekBar;
	boolean isInited=false;
	float volumn=0.5f;
	TextView tv;
	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout_2);
		findViewById(R.id.play).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isInited && mp!=null){
					mp.start();
					handler.postDelayed(run, 500);
				}
				Log.d("yy", "play");
			}
		});
		findViewById(R.id.pause).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isInited && mp!=null){
					mp.pause();
				}
				Log.d("yy", "pause");
			}
		});
		findViewById(R.id.stop).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isInited && mp!=null){
					mp.stop();
					handler.removeCallbacksAndMessages(null);
				}
				Log.d("yy", "stop");
			}
		});
		findViewById(R.id.setDataSource).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("yy", "setDataSource");
			}
		});
		
		seekBar=(SeekBar)findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) { }
			@Override public void onStartTrackingTouch(SeekBar seekBar) { }
			@Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(isInited && mp!=null && fromUser){
					mp.seekTo(progress);
					Log.d("yy", "onProgressChanged(): progress="+progress);
				}
			}
		});
		
		InitAsset();
		
		findViewById(R.id.up).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isInited && mp!=null){
					volumn+=0.05f;
					if(volumn>1.0f)
						volumn=1.0f;
					mp.setVolume(volumn, volumn);
					tv.setText(volumn+"");
				}
			}
		});
		findViewById(R.id.down).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isInited && mp!=null){
					volumn-=0.05f;
					if(volumn<0)
						volumn=0;
					mp.setVolume(volumn, volumn);
					tv.setText(volumn+"");
				}
			}
		});
		tv=(TextView)findViewById(R.id.volumn);
		lv=(ListView)findViewById(R.id.lv);
	}
	
	private void InitAsset(){
		try {
			AssetFileDescriptor fd=getAssets().openFd("test.mp3");
			if(mp==null)mp=new MediaPlayer();
			mp.setDataSource(fd.getFileDescriptor());
//			AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			
			mp.prepareAsync();
			mp.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					isInited=true;
					seekBar.setMax(mp.getDuration());
					Log.d("yy", "InitAsset() finished! duration="+mp.getDuration());
					Log.d("yy", "isInited="+isInited);
				}
			});
			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					Log.d("yy", "onCompletion");
					mp.stop();
					mp.reset();
				}
			});
			mp.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					Log.d("yy", "mp="+mp+",what="+what+",extra="+extra);
					return true;
				}
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	Handler handler=new Handler(){};
	
	Runnable run=new Runnable() {
		@Override public void run() {
			if(mp.isPlaying()){
				seekBar.setProgress(mp.getCurrentPosition());
			}
			handler.postDelayed(run, 500);
			Log.d("yy", "MediaPlayer.Position:"+mp.getCurrentPosition());
		}
	};
	
}
