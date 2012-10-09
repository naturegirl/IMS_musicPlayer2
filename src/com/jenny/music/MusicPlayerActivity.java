package com.jenny.music;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MusicPlayerActivity extends Activity implements SensorEventListener {
    /** Called when the activity is first created. */
	
	// stuff related to sound playback
	Button startButton;
	Button stopButton;
	private SoundPool soundPool;			// stores sound file
    private HashMap<Integer, Integer> soundsMap;
	final int SOUND1=1;
	int streamId;	// Id of the sound stream. usually 1
	private boolean isPlaying = false;
	
	// stuff related to xyz coordinates
	private SensorManager sensorManager;
	TextView xCoor; // declare X axis object
	TextView yCoor; // declare Y axis object
	TextView zCoor; // declare Z axis object
    
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~
	OnClickListener startButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// start playing the song
			playSound(SOUND1, 1.0f);
			isPlaying = true;
		}
	};
	
	OnClickListener stopButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// stop playing the song
			soundPool.stop(streamId);
			isPlaying = false;
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startButton = (Button)findViewById(R.id.button1);
        stopButton = (Button)findViewById(R.id.button2);
        
        startButton.setOnClickListener(startButtonListener);
        stopButton.setOnClickListener(stopButtonListener);
        
        xCoor=(TextView)findViewById(R.id.xcoor); // create X axis object
		yCoor=(TextView)findViewById(R.id.ycoor); // create Y axis object
		zCoor=(TextView)findViewById(R.id.zcoor); // create Z axis object
        
        // init soundpool
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundsMap = new HashMap<Integer, Integer>();
        soundsMap.put(SOUND1, soundPool.load(this, R.raw.gangnam, 1));
        
        // init sensormanager
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		// add listener. The listener will be HelloAndroid (this) class
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
				
    }
    
    // for song playback
    private void playSound(int sound, float fSpeed) {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        
        streamId = soundPool.play(soundsMap.get(sound), volume, volume, 1, 0, fSpeed);
        Log.d("mee", "return: " + streamId);
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
		
			// assign directions
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];

			
			if (isPlaying) {
				float speed = getSpeed(x);	 // speed == pitch
				float volume = getVolume(y);
				soundPool.setVolume(streamId, volume, volume);
				soundPool.setRate(streamId, speed);
				xCoor.setText("X: "+x + " speed: " + speed);
				yCoor.setText("Y: "+y + " volume: " + volume);
			}
			else {
				xCoor.setText("X: "+x);
				yCoor.setText("Y: "+y);
			}
			zCoor.setText("Z: "+z);
		}		
	}
	
	/* do mapping
	 * x --> speed
	 * y --> volume
	 */
	private float getVolume(float y) {
		if (y < -10) y = -10;
		else if (y > 10) y = 10;
		float volume = (y+10) / 20;
		return volume;
	}
	/* x -10 ~ 10
	 * speed: 0.5 ~ 2.0
	 */
	private float getSpeed(float x) {
		if (x > 10) x = 10;
		else if (x < -10) x = -10;
		float speed;
		if (x < 0)
			speed = (x+10) / 10.0f * 0.5f + 0.5f;	// 0.5 ~ 1
		else
			speed = x/10.0f + 1;		// 1 ~ 2
		return speed;
	}
}