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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Matthew Syrén on 2017/09/16.
 */

public class QuizListViewAdapter extends ArrayAdapter {
    //Declarations
    private Context context;
    private ArrayList<Quiz> lstQuizzes;
    NotificationManager notificationManager;
    Notification.Builder notificationBuilder;
    int notificationID = 1;
    Notification notification;

    //Constructor

    public QuizListViewAdapter(Context context, ArrayList<Quiz> lstQuizzes) {
        super(context, R.layout.list_view_row_quizzes, lstQuizzes);
        this.context = context;
        this.lstQuizzes = lstQuizzes;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new Notification.Builder(context);
    }

    @SuppressWarnings("VisibleForTests")
    //Method populates the appropriate Views with the appropriate data (stored in the shows ArrayList)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //View declarations
        TextView txtQuizName;
        final ImageButton btnDownloadVideo;

        //Inflates the list_row view for the ListView
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_row_quizzes, parent, false);

        //View assignments
        txtQuizName = (TextView) convertView.findViewById(R.id.text_quiz_name);
        btnDownloadVideo = (ImageButton) convertView.findViewById(R.id.button_download_video);

        boolean fileDownloaded = checkForFile(position);

        if(fileDownloaded){
            btnDownloadVideo.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        else{
            btnDownloadVideo.setImageResource(R.drawable.ic_file_download_black_24dp);
        }

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
                        //Downloads file if file hasn't been downloaded already
                        notificationBuilder.setOngoing(false)
                                .setContentTitle("Teaching")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentText("Video is downloading...")
                                .setProgress(100, 100, false);

                        //Display the notification
                        notification = notificationBuilder.build();
                        notificationManager.notify(notificationID, notification);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference store = storageReference.child(lstQuizzes.get(position).getKey() + ".mp4");

                        File file = new File(context.getFilesDir() , lstQuizzes.get(position).getKey() + ".mp4");
                        store.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(context, "Video successfully downloaded!", Toast.LENGTH_LONG).show();
                                btnDownloadVideo.setImageResource(R.drawable.ic_play_arrow_black_24dp);
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
                                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
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

        return convertView;
    }

    public boolean checkForFile(int position){
        File[] files = context.getFilesDir().listFiles();

        boolean fileDownloaded = false;

        for(int i = 0; i < files.length; i++){
            if(files[i].getName().equals(lstQuizzes.get(position).getKey() + ".mp4")){
                fileDownloaded = true;
                break;
            }
        }

        return fileDownloaded;
    }
}