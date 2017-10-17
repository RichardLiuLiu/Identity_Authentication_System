package com.recorder_test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.recorder_test.CMDExecute;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class MainActivity extends Activity {

	private Button start=null;
	private Button stop=null;
	private Button play=null;
	private Button send=null;
	private EditText result;
	private EditText mimsi;
	private int wholeii;
	public static Context context;
	public static TelephonyManager tm;
		
	private boolean isRecording=false ;
    private static int frequency=44100;
    private static int audioEncoding=AudioFormat.ENCODING_PCM_16BIT;
//    private static int audioSource=MediaRecorder.AudioSource.REMOTE_SUBMIX;
    private static int audioSource=MediaRecorder.AudioSource.MIC;
    
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		start=(Button)findViewById(R.id.start);
		stop=(Button)findViewById(R.id.stop);
		play=(Button)findViewById(R.id.decode);
		send=(Button)findViewById(R.id.post);
		result=(EditText)findViewById(R.id.result);
		mimsi = (EditText)findViewById(R.id.e_imsi);	
		context=MainActivity.this;
		tm=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		start.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Thread thread=new Thread(new Runnable() {
                    public void run() {
                      record();
                    }    
                });
                thread.start();
                findViewById(R.id.start).setEnabled(false) ;
                findViewById(R.id.stop).setEnabled(true) ;				
			}
		});
		
		stop.setEnabled(false) ;
		stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				isRecording=false ;
				findViewById(R.id.start).setEnabled(true) ;
                findViewById(R.id.stop).setEnabled(false) ;
			}
		});
		
		play.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
//				Thread thread1=new Thread(new Runnable() {
//                    public void run() {
                      play();
//                    }    
//                });
//                thread1.start();
			}
		});
		
		send.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				new Thread()
				{
					public void run()
					{
						post();
					}
				}.start();
				Log.d("22222222","after the start");
			}
		});
		
	}
	
	public String fetch_imsi()
	{
		String result=new String();
		result = tm.getSubscriberId() +"\n";
	    return result;
	}
	
	public void record() {
	      int channelConfiguration=AudioFormat.CHANNEL_IN_MONO;
	      File file=new File("/storage/emulated/0/PCM_test4.txt");
	      
	      // Delete any previous recording.
	      if (file.exists())
	        file.delete();
	 	 
	      // Create the new file.
	      try {
	        file.createNewFile();
	      } catch (IOException e) {
	        throw new IllegalStateException("Failed to create " + file.toString());
	      }
	      
	      try {
	        // Create a DataOuputStream to write the audio data into the saved file.
	        OutputStream os=new FileOutputStream(file);
	        BufferedOutputStream bos=new BufferedOutputStream(os);
	        DataOutputStream dos=new DataOutputStream(bos);
	        
//	        AudioManager mAudioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//	        mAudioManager.setRemoteSubmixOn(true, 0);
	        
	        // Create a new AudioRecord object to record the audio.

	        int bufferSize=AudioRecord.getMinBufferSize(frequency,channelConfiguration,audioEncoding);
	        Log.e("AudioRecord","1");
	        AudioRecord audioRecord=new AudioRecord(audioSource,frequency,channelConfiguration,audioEncoding,bufferSize);
	        Log.e("AudioRecord","2");
	      
	        short[] buffer=new short[bufferSize];  
	        audioRecord.startRecording();
	 
	        isRecording=true ;
	        int bufferReadResult=audioRecord.read(buffer, 0, bufferSize);
	        while (isRecording) {
	          bufferReadResult=audioRecord.read(buffer, 0, bufferSize);
	          for (int i=0; i < bufferReadResult; i++)
	            dos.writeShort(buffer[i]);
	        }
	  
	        audioRecord.stop();
	        audioRecord.release();
	        dos.close();
	        
	      } catch (Throwable t) {
	        Log.e("AudioRecord","Recording Failed");
	      }
	    }

	
	public void play() {
	      // Get the file we want to playback.
	      File file=new File("/storage/emulated/0/PCM_test4.txt");
	      // Get the length of the audio stored in the file (16 bit so 2 bytes per short)
	      // and create a short array to store the recorded audio.
	      int musicLength=(int)(file.length()/2);
	      short[] music=new short[musicLength];
	  
	      try {
	        // Create a DataInputStream to read the audio data back from the saved file.
	        InputStream is=new FileInputStream(file);
	        BufferedInputStream bis=new BufferedInputStream(is);
	        DataInputStream dis=new DataInputStream(bis);
	         
	        // Read the file into the music array.
	        int i=0;
/*	        
	        while (dis.available() > 0) {
	          music[i]=dis.readShort();
	          i++;
	        }
*/	 
	        while (dis.available() > 0) {
		          music[i]=dis.readShort();
		          i++; }
	        // Close the input streams.
	           
/*	 	 
	        // Create a new AudioTrack object using the same parameters as the AudioRecord
	        // object used to create the file.
	        AudioTrack audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC,
	        									 frequency,
	                                             AudioFormat.CHANNEL_OUT_MONO,
	                                             audioEncoding,
	                                             musicLength*2,
	                                             AudioTrack.MODE_STREAM);
	        // Start playback
	        audioTrack.play();
	     
	        // Write the music buffer to the AudioTrack object
	        audioTrack.write(music, 0, musicLength);	 
	        audioTrack.stop() ;
*/	        

	        double FREQTHRESHOLD=0.003;
			double FREQMAX = 3000;  //7500
			double FREQMIN = 800;  //2500
			int STEPNUM = 19;
			int CODEX = 16;  //16
			int CODEY = 17;  //17
			int CODEZ = 18;  //18
			ArrayList<Double> mFreqBuffer=new ArrayList<Double>();
			int mSessionID;
	        
	        
	        
	        int wholei=0;
	        int k,m;
	        
	        FileWriter fp;
			fp=new FileWriter("/storage/emulated/0/processing.txt");
		
			mFreqBuffer.clear();
	   short []musictemp=new short[(int)(0.8*frequency)];
	   while(wholei+musictemp.length<music.length) {	        
	        m=0;
	        for(k=wholei;k<wholei+musictemp.length;k++) {
	        	musictemp[m]=music[k];
	        	m=m+1;
	        }
	                
	        FFT calculator=new FFT(musictemp, frequency/16);
			double[] freqBuffer=calculator.transform();
			freqBuffer[0]=0;
			
			double sum=0;
			int maxp=0;
			
			for (i=0; i<freqBuffer.length;i++)
			{
				sum+=freqBuffer[i];
				if (freqBuffer[i]>freqBuffer[maxp])//&& frequency*i/freqBuffer.length>FREQMIN && frequency*i/freqBuffer.length<FREQMAX) 
					maxp=i;
			}
			if (sum>0 && freqBuffer[maxp]>sum*FREQTHRESHOLD) {
//			if (sum>0) {
				mFreqBuffer.add((double) (frequency*maxp/freqBuffer.length));
			}
			else {
//				mFreqBuffer.add((double) 0);
			}
			wholei=wholei+musictemp.length;

//			wholeii=wholei;
	   }
//			for (i=0; i<mFreqBuffer.size(); i++) {
//				fp.write(String.valueOf(mFreqBuffer.get(i))+" ");
//			}
				
		//数值标准化,去除无意义区段
		ArrayList<Integer> decodebuf=new ArrayList<Integer>();
		double loc,freq;
		for (i=0;i<mFreqBuffer.size();i++) {
			freq=mFreqBuffer.get(i);
			if (freq<FREQMIN || freq>FREQMAX) continue;
//			loc=(STEPNUM-1)*(freq-FREQMIN)/(FREQMAX-FREQMIN);
/*
			int j=0;
			double diffini=1000000;
			double diff;
			int freqF=0;
			for(j=0;j<19;j++) {
				diff=Math.abs(freq-Math.pow(2.0,j/12.0)*440.0*2);
				if(diff<diffini) {
					diffini=diff;
					freqF=j;
				}
			}
*/
//			Log.e("AudioTrack",String.valueOf(freq));
			loc=Math.log(freq/440.0/2)/Math.log(2.0)*12;
//			int temp=(int) (Math.floor(loc)+Math.floor(loc*2)%2);
//			Log.e("AudioTrack",String.valueOf(freq));
//			loc=(freq-800)/50.0;
//			Log.e("AudioTrack",String.valueOf(temp));
//			decodebuf.add((int) (Math.floor(loc)+Math.floor(loc*2)%2));
			decodebuf.add((int)loc);
//			decodebuf.add(freqF+1);
		}
				
		//合并重复采样
		int previous=-1,pos=0;
		while (pos<decodebuf.size()) {
			if (previous != decodebuf.get(pos)) {
				previous=decodebuf.get(pos);
				pos++;
			}
			else
				decodebuf.remove(pos);
		}
		for(i=0;i<decodebuf.size();i++)
			Log.e("AudioTrack",String.valueOf(decodebuf.get(i)));
		
		
		pos=-1;
		pos=decodebuf.indexOf(CODEX);
		if (pos<0) {
			result.setText("无效的数据源文件。");
			return;
		}
		decodebuf.subList(0, pos+1).clear();


		pos=-1;
		pos=decodebuf.indexOf(CODEY);
		if (pos<0) {
			result.setText("不完整的数据源文件。");
			return;
		}
		decodebuf.subList(pos, decodebuf.size()).clear();
		
/*		
		//截去首尾多余数据
		pos=-1;
		while (true) {
			pos=decodebuf.indexOf(CODEX);
			if (pos<0) {
				result.setText("无效的数据源文件。");
				return;
			}
			if (decodebuf.get(pos+1)==CODEY && decodebuf.get(pos+2)==CODEX) {
				decodebuf.subList(0, pos+3).clear();
				break;
			}
			else 
				decodebuf.subList(0, pos+1).clear();
		}
		pos=-1;
		while (true) {
			pos=decodebuf.indexOf(CODEY);
			if (pos<0) {
				result.setText("不完整的数据源文件。");
				return;
			}
			if (decodebuf.get(pos+1)==CODEX && decodebuf.get(pos+2)==CODEY) {
				decodebuf.subList(pos, decodebuf.size()).clear();
				break;
			}
			else 
				decodebuf.remove(pos);
		}
*/		
		//移除分割符
		while (decodebuf.remove((Integer)CODEZ));

/*	
		//校验和
		int checksum=decodebuf.remove(decodebuf.size()-1);
		int sum=0;
		for (i=0;i<decodebuf.size();i++) 
			sum+=decodebuf.get(i);
		if (sum%(STEPNUM-3) != checksum) {
			mTextOut.setText("校验不匹配，数据无效。");
			return -2;
		}
*/		

		Log.e("AudioTrack","      ");
		for(i=0;i<decodebuf.size();i++)
			Log.e("AudioTrack",String.valueOf(decodebuf.get(i)));
		
		mSessionID=0;
		for (i=0; i<decodebuf.size(); i++) {
			mSessionID=mSessionID*10+decodebuf.get(i);
		}
		
		result.setText(String.valueOf(mSessionID));
//		result.setText("123");
//		mimsi.setText("460015150340799");
		mimsi.setText(fetch_imsi());

		dis.close(); 
		fp.flush();
		fp.close();
			
	        
	      } catch (Throwable t) {
	        Log.e("AudioTrack","Playback Failed");
	      }
	    }
	
	public void post() {	
		Log.d("33333333","it is in the run");
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		Log.e("post","1111111111");
//  	HttpPost post = new HttpPost("http://121.248.49.96:80/phone_auth/cgi/post_data.php");//server url  121.248.49.96/phone_auth/cgi/post_data.php
		HttpPost post = new HttpPost("http://121.248.52.246:8081/login/cgi/post_data.php");
		Log.e("11111111","after the post");
				
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		String ssid=result.getText().toString();//+info;
		Log.e("4444444444",ssid);
		params.add(new BasicNameValuePair("ssid",ssid));
/*					
		params.add(new BasicNameValuePair("DeviceID(IMEI)", tm.getDeviceId()));
		params.add(new BasicNameValuePair("DevicesoftwareVersion",tm.getDeviceSoftwareVersion()));
//		params.add(new BasicNameValuePair("LineNumber",tm.getLine1Number()));
		params.add(new BasicNameValuePair("NetworkCountryIso",tm.getNetworkCountryIso()));
		params.add(new BasicNameValuePair("NetworkOperator",tm.getNetworkOperator()));
		params.add(new BasicNameValuePair("NetworkOperatorName",tm.getNetworkOperatorName()));
		params.add(new BasicNameValuePair("NetworkType",tm.getNetworkType()+"\n"));
		params.add(new BasicNameValuePair("PhoneType",tm.getPhoneType()+"\n"));	
		params.add(new BasicNameValuePair("SimCountryIso",tm.getSimCountryIso()));	
		params.add(new BasicNameValuePair("SimOperator ",tm.getSimOperator()));
//		params.add(new BasicNameValuePair("SimOperatorName",tm.getSimOperatorName()));
		params.add(new BasicNameValuePair("SimSerialNumber",tm.getSimSerialNumber()));
		params.add(new BasicNameValuePair("SimState",tm.getSimState()+"\n"));
		params.add(new BasicNameValuePair("SubscriberId(IMSI)",tm.getSubscriberId()));
//		params.add(new BasicNameValuePair("VoiceMailNumber",tm.getVoiceMailNumber()));
*/											
		Log.e("777777777","after the handlemessage");
		try{
			post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			Log.e("888888888","before the execute");
			HttpResponse response = httpClient.execute(post);// execute() has a return
			Log.e("999999999","after the execute");
				
			if(response.getStatusLine().getStatusCode() == 200)  
				Log.e("666666666",  EntityUtils.toString(response.getEntity()));  
			} catch(IOException e)  {
				Log.e("555555555","it is in the catch");
				e.printStackTrace();
			}	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
