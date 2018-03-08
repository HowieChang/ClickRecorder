// Copyright 2016 Google Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//      http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.HQmade.ClickRecorder;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public boolean isPlay = false;
    public int playingID = -1;

    public ProgressBar progressBar;
    private MediaPlayer mp;
    private static final String TAG = "ClickRecorder";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC), "ClickRecorder");
        if (! mediaStorageDir.exists()) {
            Log.d(TAG, "Folder not exit");
        } else {
            Log.d(TAG, "Folder exit");
        }
        File[] FileListArray = mediaStorageDir.listFiles();
        Arrays.sort(FileListArray);
        ListView FileList = (ListView) findViewById(R.id.FileList);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_activated_1);

        for(File file:FileListArray) {
            // prints file and directory paths
            adapter.add(file.getName());

        }


        FileList.setAdapter(adapter);

        FileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView arg0, final View arg1, final int arg2,
                                    final long arg3) {

                TextView statusText = (TextView) findViewById(R.id.StatusText);

                ListView FileList = (ListView) arg0;
                //Log.d(TAG, "Clicked, POS:"+arg2+", ID:"+arg3);
                //Log.d(TAG, "Clicked View ID:"+arg1.getId());

                File path = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC), "ClickRecorder");
                File file = new File(path, FileList.getItemAtPosition(arg2).toString());



                isPlay = !isPlay;
                if (isPlay) {
                    arg1.setPressed(true);
                    arg1.setBackgroundColor(Color.LTGRAY);
                    playingID = arg2;
                    Log.d(TAG, "Start play ID:"+arg3);
                    statusText.setText(R.string.Status_2);
                    mp = new MediaPlayer() {};
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mp.setDataSource(file.toString());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            ListView mListView = (ListView) findViewById(R.id.FileList);
                            TextView statusText = (TextView) findViewById(R.id.StatusText);
                            mListView.getOnItemClickListener().onItemClick(arg0, arg1, arg2, arg3);
                            //arg1.setBackgroundColor(Color.WHITE);
                            Log.d(TAG, "Complete play ID:"+arg3);
                            statusText.setText(R.string.Status_3);
                            isPlay=false;
                        }
                    });


                    mp.start();

                } else {
                    //arg1.
                    //Log.d(TAG, "isPressed:"+arg1.isPressed());



                    FileList.getChildAt(playingID).setBackgroundColor(Color.WHITE);
                    Log.d(TAG, "Stop play ID:"+arg3);

                    statusText.setText(R.string.Status_1);

                    mp.stop();
                    mp.reset();
                    mp.release();
                }
                /*
                Toast.makeText(
                        getApplicationContext(),
                        "ID：" + arg3 +
                                "   選單文字："+ FileList.getItemAtPosition(arg2).toString(),
                        Toast.LENGTH_LONG).show();
                */
            }
        });
    }








}
