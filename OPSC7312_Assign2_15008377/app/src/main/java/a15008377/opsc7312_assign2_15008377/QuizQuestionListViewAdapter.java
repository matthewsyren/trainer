/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class allows the user to view and delete the Questions included in the Quiz
 */

package a15008377.opsc7312_assign2_15008377;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

    //Method populates the appropriate Views with the appropriate data
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //View declarations
        TextView txtQuestion;
        TextView txtOptionOne;
        TextView txtOptionTwo;
        TextView txtOptionThree;
        TextView txtOptionFour;
        TextView txtCorrectOption;
        ImageButton btnDelete;

        //Inflates the list_row view for the ListView
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_view_row_quiz_questions, parent, false);

        //View assignments
        txtQuestion = (TextView) convertView.findViewById(R.id.text_question);
        txtOptionOne = (TextView) convertView.findViewById(R.id.text_option_one);
        txtOptionTwo = (TextView) convertView.findViewById(R.id.text_option_two);
        txtOptionThree = (TextView) convertView.findViewById(R.id.text_option_three);
        txtOptionFour = (TextView) convertView.findViewById(R.id.text_option_four);
        txtCorrectOption = (TextView) convertView.findViewById(R.id.text_correct_option);
        btnDelete = (ImageButton) convertView.findViewById(R.id.button_delete_question);

        //Displays the data in the appropriate Views
        Resources resources = context.getResources();
        txtQuestion.setText(resources.getString(R.string.list_view_text_question, lstQuestions.get(position).getQuestion()));
        txtOptionOne.setText(resources.getString(R.string.list_view_text_option_one, lstQuestions.get(position).getOptionOne()));
        txtOptionTwo.setText(resources.getString(R.string.list_view_text_option_two, lstQuestions.get(position).getOptionTwo()));
        txtOptionThree.setText(resources.getString(R.string.list_view_text_option_three, lstQuestions.get(position).getOptionThree()));
        txtOptionFour.setText(resources.getString(R.string.list_view_text_option_four, lstQuestions.get(position).getOptionFour()));
        txtCorrectOption.setText(resources.getString(R.string.list_view_text_correct_option, lstQuestions.get(position).getAnswerPosition() + 1));

        //Adds OnClickListener to delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lstQuestions.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Question removed", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }
}