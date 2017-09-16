package a15008377.opsc7312_assign2_15008377;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matthew Syrén on 2017/09/16.
 */

public class QuizQuestionListViewAdapter extends ArrayAdapter {
    //Declarations
    private Context context;
    private ArrayList<Question> lstQuestions;

    //Constructor

    public QuizQuestionListViewAdapter(Context context, ArrayList<Question> lstQuestions) {
        super(context, R.layout.list_view_row_quiz_questions, lstQuestions);
        this.context = context;
        this.lstQuestions = lstQuestions;
    }

    //Method populates the appropriate Views with the appropriate data (stored in the shows ArrayList)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //View declarations
        TextView txtQuestion;
        TextView txtOptionOne;
        TextView txtOptionTwo;
        TextView txtOptionThree;
        TextView txtOptionFour;

        //Inflates the list_row view for the ListView
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_row_quiz_questions, parent, false);

        //View assignments
        txtQuestion = (TextView) convertView.findViewById(R.id.text_question);
        txtOptionOne = (TextView) convertView.findViewById(R.id.text_option_one);
        txtOptionTwo = (TextView) convertView.findViewById(R.id.text_option_two);
        txtOptionThree = (TextView) convertView.findViewById(R.id.text_option_three);
        txtOptionFour = (TextView) convertView.findViewById(R.id.text_option_four);

        //Displays the data in the appropriate Views
        Resources resources = context.getResources();
        txtQuestion.setText(resources.getString(R.string.list_view_text_question, lstQuestions.get(position).getQuestion()));
        txtOptionOne.setText(resources.getString(R.string.list_view_text_option_one, lstQuestions.get(position).getOptionOne()));
        txtOptionTwo.setText(resources.getString(R.string.list_view_text_option_two, lstQuestions.get(position).getOptionTwo()));
        txtOptionThree.setText(resources.getString(R.string.list_view_text_option_three, lstQuestions.get(position).getOptionThree()));
        txtOptionFour.setText(resources.getString(R.string.list_view_text_option_four, lstQuestions.get(position).getOptionFour()));

        return convertView;
    }
}