package a15008377.opsc7312_assign2_15008377;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matthew Syrén on 2017/09/16.
 */

public class PastQuizListViewAdapter extends ArrayAdapter {
    //Declarations
    private Context context;
    private ArrayList<Quiz> lstQuizzes;
    private ArrayList<Statistic> lstStatistics;

    //Constructor
    public PastQuizListViewAdapter(Context context, ArrayList<Quiz> lstQuizzes, ArrayList<Statistic> lstStatistics) {
        super(context, R.layout.list_view_row_past_quizzes, lstQuizzes);
        this.context = context;
        this.lstQuizzes = lstQuizzes;
        this.lstStatistics = lstStatistics;
    }

    //Method populates the appropriate Views with the appropriate data (stored in the shows ArrayList)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //View declarations
        TextView txtQuizName;
        TextView txtQuizResult;

        //Inflates the list_row view for the ListView
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_row_past_quizzes, parent, false);

        //View assignments
        txtQuizName = (TextView) convertView.findViewById(R.id.text_quiz_name);
        txtQuizResult = (TextView) convertView.findViewById(R.id.text_quiz_result);

        //Displays the data in the appropriate Views
        Resources resources = context.getResources();
        txtQuizName.setText(resources.getString(R.string.list_view_text_quiz_name, lstQuizzes.get(position).getName()));
        txtQuizResult.setText(resources.getString(R.string.list_view_text_quiz_result, lstStatistics.get(position).getResult()));
        return convertView;
    }
}