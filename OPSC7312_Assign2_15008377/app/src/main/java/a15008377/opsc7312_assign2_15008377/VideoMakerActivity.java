package a15008377.opsc7312_assign2_15008377;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.LeadingMarginSpan;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class VideoMakerActivity extends AppCompatActivity {
    //Declarations
    NotificationManager notificationManager;
    Notification.Builder notificationBuilder;
    int notificationID = 1;
    Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_video_maker);

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder = new Notification.Builder(getApplicationContext());
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method displays the video in the VideoView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try{
            if (requestCode == 1 && resultCode == RESULT_OK) {
                VideoView videoView = (VideoView) findViewById(R.id.video_view);
                Uri videoUri = intent.getData();
                videoView.setVideoURI(videoUri);
                videoView.requestFocus();
                uploadVideo(videoUri);
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("VisibleForTests")
    //Method uploads the video that the user has taken to Firebase Storage
    private void uploadVideo(Uri file){
        try{
            //Displays notification for upload progress
            notificationBuilder.setOngoing(true)
                    .setContentTitle("Teaching")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("Uploading...")
                    .setProgress(100, 0, false);
            notification = notificationBuilder.build();
            notificationManager.notify(notificationID, notification);

            //Gets reference to Firebase Storage and Database
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            //Generates random key for the Quiz (which will be used as the video name to link it to the Quiz)
            String key = getIntent().getStringExtra("quizKey");
            StorageReference store = storageReference.child(key + ".mp4");
            store.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Video uploaded successfully", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload failed, please try again. " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                            notificationBuilder.setProgress(100, progress, false);
                            notificationBuilder.setContentText("Uploading: " + progress + "%");

                            //Displays the upload progress
                            if(progress == 100){
                                notificationManager.cancel(notificationID);
                            }
                            else{
                                notification = notificationBuilder.build();
                                notificationManager.notify(notificationID, notification);
                            }
                        }
                    });
        }
        catch(Exception exc) {
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method opens the phone's video camera
    public void recordVideoOnClick(View view) {
        try{
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, 1);
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}