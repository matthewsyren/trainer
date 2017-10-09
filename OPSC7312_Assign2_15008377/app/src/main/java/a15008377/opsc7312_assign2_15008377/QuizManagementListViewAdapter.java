package a15008377.opsc7312_assign2_15008377;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

/**
 * Created by Matthew Syrén on 2017/09/16.
 */

public class QuizManagementListViewAdapter extends ArrayAdapter {
    //Declarations
    private Context context;
    private ArrayList<Quiz> lstQuizzes;
    NotificationManager notificationManager;
    Notification.Builder notificationBuilder;
    int notificationID = 1;
    Notification notification;
    int quizToDelete;

    //Constructor

    public QuizManagementListViewAdapter(Context context, ArrayList<Quiz> lstQuizzes) {
        super(context, R.layout.list_view_row_quiz_management, lstQuizzes);
        this.context = context;
        this.lstQuizzes = lstQuizzes;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new Notification.Builder(context);
    }

    //Method populates the appropriate Views with the appropriate data (stored in the lstQuizzes ArrayList)
    @SuppressWarnings("VisibleForTests")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //View declarations
        TextView txtQuizName;
        ImageButton btnRecordVideo;
        final ImageButton btnDownloadVideo;
        final ProgressBar progressBar;
        final ImageButton btnDeleteQuiz;

        //Inflates the list_row view for the ListView
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_row_quiz_management, parent, false);

        //View assignments
        txtQuizName = (TextView) convertView.findViewById(R.id.text_quiz_name);
        btnRecordVideo = (ImageButton) convertView.findViewById(R.id.button_record_video);
        btnDownloadVideo = (ImageButton) convertView.findViewById(R.id.button_download_video);
        btnDeleteQuiz = (ImageButton) convertView.findViewById(R.id.button_delete_quiz);
        progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar_video_download);
        progressBar.setVisibility(View.INVISIBLE);

        boolean fileDownloaded = checkForFile(position);

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

        btnDownloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean fileDownloaded = checkForFile(position);
                if(fileDownloaded){
                    Intent intent = new Intent(context, VideoViewerActivity.class);
                    intent.putExtra("fileName", lstQuizzes.get(position).getKey() + ".mp4");
                    context.startActivity(intent);
                }
                else{
                    try{
                        progressBar.setVisibility(View.VISIBLE);
                        btnDownloadVideo.setVisibility(View.INVISIBLE);

                        //Downloads file if file hasn't been downloaded already
                        notificationBuilder.setOngoing(false)
                                .setContentTitle("Teaching")
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
                                btnDownloadVideo.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                btnDownloadVideo.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                notificationBuilder.setProgress(100, progress, false);
                                notificationBuilder.setContentText("Downloading: " + progress + "%");

                                //Send the notification:
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

        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoMakerActivity.class);
                intent.putExtra("quizKey", lstQuizzes.get(position).getKey());
                context.startActivity(intent);
            }
        });

        //Deletes the Quiz and the results for that Quiz for each user
        btnDeleteQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Displays popup that asks the user if they're sure they'd like to delete the Quiz
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Delete Quiz?");
                alertDialog.setMessage("Are you sure you would like to delete this Quiz? All user results associated with this Quiz will also be deleted.");

                //Creates OnClickListener for the Dialog message
                DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        Intent intent;
                        switch(button){
                            case AlertDialog.BUTTON_POSITIVE:
                                quizToDelete = position;
                                new Statistic().requestDeleteOfStatistic(lstQuizzes.get(position).getKey(), context, new DataReceiver(new Handler()));
                                break;
                            case AlertDialog.BUTTON_NEGATIVE:
                                Toast.makeText(context, "Deletion cancelled", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                //Assigns buttons and OnClickListener for the AlertDialog and displays the AlertDialog
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", dialogOnClickListener);
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", dialogOnClickListener);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
        return convertView;
    }

    //Method checks to see if the video has already been downloaded
    public boolean checkForFile(int position) {
        File[] files = context.getFilesDir().listFiles();
        boolean fileDownloaded = false;

        //Loops through downloaded files to see if the video has already been downloaded
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(lstQuizzes.get(position).getKey() + ".mp4")) {
                fileDownloaded = true;
                break;
            }
        }

        return fileDownloaded;
    }

    //Creates a ResultReceiver to retrieve information from the FirebaseService
    private class DataReceiver extends ResultReceiver {
        private DataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData){
            //Processes the result when the Statistics have been deleted from the Firebase Database
            if(resultCode == FirebaseService.ACTION_DELETE_STATISTIC_RESULT_CODE){
                lstQuizzes.get(quizToDelete).requestWriteOfQuiz(context, "delete", new DataReceiver(new Handler()));
            }
            else if(resultCode == FirebaseService.ACTION_WRITE_QUIZ_RESULT_CODE){
                lstQuizzes.remove(quizToDelete);
                notifyDataSetChanged();
                Toast.makeText(context, "Quiz successfully deleted", Toast.LENGTH_LONG).show();
            }
        }
    }
}