package com.frank.mediaplayer;

import android.widget.SeekBar;

public interface IMusic {
	public void dopause();
	public void doresume();
	public void dostart();
	public void dostop();
	public void init(SeekBar seekbar,onMusicOver over);
	public void doRset();
	public void doremove();
}

