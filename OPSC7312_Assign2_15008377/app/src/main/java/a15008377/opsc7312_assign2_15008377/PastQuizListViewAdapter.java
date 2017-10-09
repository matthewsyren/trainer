/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class displays information about Quizzes the user has completed in a ListView
 */

package a15008377.opsc7312_assign2_15008377;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class PastQuizListViewAdapter extends ArrayAdapter {
    //Declarations
    private Context context;
    private ArrayList<Quiz> lstQuizzes;
    private ArrayList<Statistic> lstStatistics;
    NotificationManager notificationManager;
    Notification.Builder notificationBuilder;
    int notificationID = 1;
    Notification notification;

    //Constructor
    public PastQuizListViewAdapter(Context context, ArrayList<Quiz> lstQuizzes, ArrayList<Statistic> lstStatistics) {
        super(context, R.layout.list_view_row_past_quizzes, lstQuizzes);
        this.context = context;
        this.lstQuizzes = lstQuizzes;
        this.lstStatistics = lstStatistics;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new Notification.Builder(context);
    }

    //Method populates the appropriate Views with the appropriate data
    @SuppressWarnings("VisibleForTests")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //View declarations
        TextView txtQuizName;
        TextView txtQuizResult;
        final ImageButton btnDownloadVideo;
        final ProgressBar progressBar;

        //Inflates the list_row view for the ListView
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_row_past_quizzes, parent, false);

        //View assignments
        txtQuizName = (TextView) convertView.findViewById(R.id.text_quiz_name);
        txtQuizResult = (TextView) convertView.findViewById(R.id.text_quiz_result);
        btnDownloadVideo = (ImageButton) convertView.findViewById(R.id.button_download_video);
        btnDownloadVideo.setFocusable(false);
        progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar_video_download);
        progressBar.setVisibility(View.INVISIBLE);

        //Checks to see if the user has already downloaded the video for the Quiz onto their phone
        boolean fileDownloaded = checkForFile(position);

        //Sets the button icon based on whether the video has already been downloaded
        if(fileDownloaded){
            btnDownloadVideo.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        else{
            btnDownloadVideo.setImageResource(R.drawable.ic_file_download_black_24dp);
        }

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference store = storageReference.child(lstQuizzes.get(position).getKey() + ".mp4");

        //Compares the last modified field of the file to the video's time created timestamp to ensure that the user has the latest video for the Quiz
        store.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                //Fetches downloaded videos
                File[] files = context.getFilesDir().listFiles();

                //Loops through downloaded files and deletes a video if it was modified before the timestamp of the Quiz's video (in other words, there is a new video)
                for(int i = 0; i < files.length; i++){
                    if(files[i].getName().equals(lstQuizzes.get(position).getKey() + ".mp4") && files[i].lastModified() < storageMetadata.getCreationTimeMillis()){
                        btnDownloadVideo.setImageResource(R.drawable.ic_file_download_black_24dp);
                        files[i].delete();
                        Toast.makeText(context, "The video for " + lstQuizzes.get(position).getName() + " has been updated, please download it if you haven't taken the Quiz", Toast.LENGTH_LONG).show();
                        notifyDataSetChanged();
                    }
                }
            }
        });

        //Downloads the video for the Quiz from Firebase Storage, or plays it if it has already been downloaded
        btnDownloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean fileDownloaded = checkForFile(position);
                if(fileDownloaded){
                    //Plays video if it has been downloaded
                    Intent intent = new Intent(context, VideoViewerActivity.class);
                    intent.putExtra("fileName", lstQuizzes.get(position).getKey() + ".mp4");
                    context.startActivity(intent);
                }
                else{
                    //Downloads video
                    try{
                        progressBar.setVisibility(View.VISIBLE);
                        btnDownloadVideo.setVisibility(View.INVISIBLE);

                        //Sets up notification
                        notificationBuilder.setOngoing(false)
                                .setContentTitle("Trainer")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentText("Video is downloading...")
                                .setProgress(100, 100, false);

                        //Display the notification
                        notification = notificationBuilder.build();
                        notificationManager.notify(notificationID, notification);

                        //Downloads the video and saves it into internal storage with the name of the Quiz's key
                        File file = new File(context.getFilesDir() , lstQuizzes.get(position).getKey() + ".mp4");
                        store.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(context, "Video successfully downloaded!", Toast.LENGTH_LONG).show();

                                //Changes the button's icon to a play button
                                btnDownloadVideo.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                btnDownloadVideo.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //Updates the download progress
                                int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                notificationBuilder.setProgress(100, progress, false);
                                notificationBuilder.setContentText("Downloading: " + progress + "%");

                                //Displays the notification
                                if (progress == 100) {
                                    notificationManager.cancel(notificationID);
                                }
                                else {
                                    notification = notificationBuilder.build();
                                    notificationManager.notify(notificationID, notification);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "There is no video uploaded for this quiz yet, please try again later...", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                btnDownloadVideo.setVisibility(View.VISIBLE);
                                notificationManager.cancel(notificationID);
                            }
                        });
                    }
                    catch(Exception exc){
                        Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Displays the data in the appropriate Views
        Resources resources = context.getResources();
        txtQuizName.setText(resources.getString(R.string.list_view_text_quiz_name, lstQuizzes.get(position).getName()));
        txtQuizResult.setText(resources.getString(R.string.list_view_text_quiz_result, lstStatistics.get(position).getResult()));

        return convertView;
    }

    //Method checks to see if the video has already been downloaded
    public boolean checkForFile(int position){
        File[] files = context.getFilesDir().listFiles();
        boolean fileDownloaded = false;

        //Loops through downloaded files to see if the video has already been downloaded
        for(int i = 0; i < files.length; i++){
            if(files[i].getName().equals(lstQuizzes.get(position).getKey() + ".mp4")){
                fileDownloaded = true;
                break;
            }
        }

        return fileDownloaded;
    }
}