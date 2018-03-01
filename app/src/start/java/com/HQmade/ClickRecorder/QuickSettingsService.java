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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.drawable.Icon;
import android.media.MediaRecorder;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class QuickSettingsService
   extends android.service.quicksettings.TileService {
    private MediaRecorder mMediaRecorder;
    private File mOutputFile;
    private boolean isRecording = false;
    private static final String TAG = "ClickRecorder";

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    private boolean prepareRecording() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_AUDIO);
        if (mOutputFile == null) {
            return false;
        }
        mMediaRecorder.setOutputFile(mOutputFile.getPath());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void startRecording() {
        if (prepareRecording()) {
            mMediaRecorder.start();
            isRecording = true;
        }else {
            // prepare didn't work, release the camera
            releaseMediaRecorder();
        }
    }

    /**
     * Called when the tile is added to the Quick Settings.
     * @return TileService constant indicating tile state
     */
    @Override
    public void onTileAdded() {
        Log.d(TAG, "Tile added");
    }

    /**
     * Called when this tile begins listening for events.
     */
    @Override
    public void onStartListening() { Log.d(TAG, "Start listening"); }

    /**
     * Called when the user taps the tile.
     */
    @Override
    public void onClick() {
        Log.d(TAG, "Tile tapped");

        if (isRecording) {
            isRecording = false;
            Toast.makeText(getApplicationContext(),"File save to "+mOutputFile.getPath(),
                    Toast.LENGTH_SHORT).show();
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e) {
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                mOutputFile.delete();
            }
            releaseMediaRecorder();

        } else {
            isRecording = true;
            Toast.makeText(getApplicationContext(),
                    R.string.record_start, Toast.LENGTH_SHORT).show();
            startRecording();
        }
        Log.d(TAG, "isRecording = "+isRecording);
        updateTile();
        //Log.d(TAG, "Tile State:"+getQsTile().getState());
    }

    /**
     * Called when this tile moves out of the listening state.
     */
    @Override
    public void onStopListening() { Log.d(TAG, "Stop Listening"); }

    /**
     * Called when the user removes this tile from Quick Settings.
     */
    @Override
    public void onTileRemoved() {
        Log.d(TAG, "Tile removed");
    }


    private void updateTile() {
        Tile tile = this.getQsTile();

        Icon newIcon;
        String newLabel;
        int newState;

        // Change the tile to match the service status.
        if (isRecording) {

            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_active));

            newIcon = Icon.createWithResource(getApplicationContext(),
                                                R.drawable.ic_mic_black_24dp);

            newState = Tile.STATE_ACTIVE;

        } else {
            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.tile_label),
                    getString(R.string.service_inactive));

            newIcon = Icon.createWithResource(getApplicationContext(),
                                                R.drawable.ic_mic_off_black_24dp);

            newState = Tile.STATE_INACTIVE;
        }

        // Change the UI of the tile.
        tile.setState(newState);
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);


        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }
}
