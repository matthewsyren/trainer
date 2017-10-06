package a15008377.opsc7312_assign2_15008377;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matthew Syrén on 2017/09/16.
 */

public class QuizManagementListViewAdapter extends ArrayAdapter {
    //Declarations
    private Context context;
    private ArrayList<Quiz> lstQuizzes;

    //Constructor

    public QuizManagementListViewAdapter(Context context, ArrayList<Quiz> lstQuizzes) {
        super(context, R.layout.list_view_row_quiz_management, lstQuizzes);
        this.context = context;
        this.lstQuizzes = lstQuizzes;
    }

    //Method populates the appropriate Views with the appropriate data (stored in the shows ArrayList)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //View declarations
        TextView txtQuizName;
        ImageButton btnRecordVideo;

        //Inflates the list_row view for the ListView
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_row_quiz_management, parent, false);

        //View assignments
        txtQuizName = (TextView) convertView.findViewById(R.id.text_quiz_name);
        btnRecordVideo = (ImageButton) convertView.findViewById(R.id.button_record_video);

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
        return convertView;
    }
}