package com.frank.mediaplayer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {

	static String[] listView_list=new String[0];
	static boolean isMusic=true;
	private ProgressDialog pd;
	ArrayList<String> all_files=new ArrayList<String>();
	
	final String[] MUSIC_SUFFIX={".mp3",".wav",".amr",".au",".mid",".aac"};
	final String[] VIDEO_SUFFIX={".mp4",".rmvb",".avi",".mpeg",".vdat"};
	
	
	private void GetList(final boolean bMusic,final String[] param){
		GetFiles t=new GetFiles(this,pd,new Callback() {
			@Override
			public void callback(String[] result) {
				listView_list=result;
				isMusic=bMusic;
				RefreshListView();
			}
		});
		
		t.execute(param);
		
	}
	
	protected String[] GetFileList(String[] suffixs) {
		File root=new File(Environment.getExternalStorageDirectory().getPath());
		all_files.clear();
		SearchFiles(root, suffixs);
		Object[] tmp=(Object[])all_files.toArray();
		Arrays.sort(tmp);
		String[] ret=new String[tmp.length];
		for(int i=0;i<tmp.length;i++){
			ret[i]=tmp[i].toString();
		}
		Log.d("yy", "Total items:"+tmp.length);
		return ret;
	}
	private void SearchFiles(File f,String[] suffixs){
		if(f==null)return;
		if(f.isFile()){
			if(suffixs==null || suffixs.length==0){
				all_files.add(f.getAbsolutePath());
			}else{
				for(int i=0;i<suffixs.length;i++){
					if(f.getName().endsWith(suffixs[i])){
						all_files.add(f.getAbsolutePath());
						break;
					}
				}
			}
		}else if(f.isDirectory()){
			File[] files=f.listFiles();
			for(int i=0;i<files.length;i++){
				SearchFiles(files[i], suffixs);
			}
		}
	}
	
	ListView list_v;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.bt_music).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GetList(true,MUSIC_SUFFIX);
			}
		});
        findViewById(R.id.bt_video).setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		GetList(false, VIDEO_SUFFIX);
        	}
        });
        list_v=(ListView)findViewById(R.id.list1);
        RefreshListView();
    }

    private void RefreshListView(){
    	SimpleAdapter adapter=new SimpleAdapter(listView_list,this);
    	list_v.setAdapter(adapter);
    	list_v.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(isMusic){
				}else{
				}
			}
		});
    }
    
}

class SimpleAdapter extends BaseAdapter{

//	PlaceHolder p;
	String[] data=new String[0];
	Context mContext;
	public SimpleAdapter(String[] data,Context mCtx){
		this.mContext=mCtx;
		this.data=data;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		PlaceHolder p=new PlaceHolder();
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, null);
			p.v=(TextView)convertView.findViewById(android.R.id.text1);
			convertView.setTag(p);
		}else{
			p=(PlaceHolder)convertView.getTag();
		}
//		String s=new File(data[position]).getName();
		p.v.setText(data[position]);
		return convertView;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public Object getItem(int position) {
		return data[position];
	}
	
	@Override
	public int getCount() {
		return data.length;
	}
	
}
class PlaceHolder{
	TextView v;
}
class GetFiles extends AsyncTask<String[], Integer, String[]>{

	private ArrayList<String> all_files;
	private Context context;
	ProgressDialog pd;
	Callback callback;
	int totalFils=0;
	int curScanFils=0;
	public GetFiles(Context context,ProgressDialog pd,Callback callback){
		this.all_files=new ArrayList<String>();
		this.context=context;
		this.pd=pd;
		this.callback=callback;
		
	}
	@Override
	protected String[] doInBackground(String[]... suffixs) {
		
		File root=new File(Environment.getExternalStorageDirectory().getPath());
		all_files.clear();
		SearchFiles(root, suffixs[0]);
		Object[] tmp=(Object[])all_files.toArray();
		Arrays.sort(tmp);
		String[] ret=new String[tmp.length];
		for(int i=0;i<tmp.length;i++){
			ret[i]=tmp[i].toString();
		}
		Log.d("yy", "Total items:"+tmp.length);
		return ret;
	}
	private void SearchFiles(File f,String[] suffixs){
		if(f==null)return;
		if(f.isFile()){
			if(suffixs==null || suffixs.length==0){
				all_files.add(f.getAbsolutePath());
				curScanFils++;
			}else{
				for(int i=0;i<suffixs.length;i++){
					if(f.getName().endsWith(suffixs[i])){
						all_files.add(f.getAbsolutePath());
						curScanFils++;
						break;
					}
				}
			}
			totalFils++;
			publishProgress(curScanFils,totalFils);
		}else if(f.isDirectory()){
			File[] files=f.listFiles();
			for(int i=0;i<files.length;i++){
				SearchFiles(files[i], suffixs);
			}
		}
	}
	
	@Override
	protected void onPostExecute(String[] result) {
		pd.dismiss();
		pd=null;
		callback.callback(result);
	}
	@Override
	protected void onPreExecute() {
		pd=new ProgressDialog(context);
		pd.setTitle("请稍后...");
		pd.setMessage("正在搜索文件...");
		pd.setCanceledOnTouchOutside(false);
		pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pd.dismiss();
				cancel(true);
			}
		});
		pd.show();
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		pd.setMessage("正在搜索文件...("+values[0]+"/"+values[1]+")");
	}
}
interface Callback{
	void callback(String[] result);
}
