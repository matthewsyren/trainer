package a15008377.opsc7312_assign2_15008377;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class VideoViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_video_viewer);

            playVideo();
        }
        catch(Exception exc) {
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void playVideo(){
        try{
            Bundle bundle = getIntent().getExtras();
            String fileName = bundle.getString("fileName");

            //Loops through all files and plays the file that matches the filename that was passed in
            File[] files = getFilesDir().listFiles();
            for(int i = 0; i < files.length; i++){
                String name = files[i].getName();

                if(fileName.equals(name)){
                    VideoView videoView =  (VideoView) findViewById(R.id.video_view);
                    videoView.setVideoURI(Uri.parse(files[i].getAbsolutePath()));
                    MediaController mediaController = new MediaController(this);
                    videoView.setMediaController(mediaController);
                    mediaController.setAnchorView(videoView);
                    videoView.start();
                }
            }
        }
        catch(Exception exc) {
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
