package a15008377.opsc7312_assign2_15008377;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class QuizSetterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz_setter);

            ListView listView = (ListView) findViewById(R.id.list_view_quiz_questions);
            QuizQuestionListViewAdapter quizQuestionListViewAdapter = new QuizQuestionListViewAdapter(this, new ArrayList<Question>());
            listView.setAdapter(quizQuestionListViewAdapter);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method adds a new Question to the ListView
    public void addQuestionOnClick(View view){
        try{
            EditText txtQuestion = (EditText) findViewById(R.id.text_question);
            EditText txtOptionOne = (EditText) findViewById(R.id.text_option_one);
            EditText txtOptionTwo = (EditText) findViewById(R.id.text_option_two);
            EditText txtOptionThree = (EditText) findViewById(R.id.text_option_three);
            EditText txtOptionFour = (EditText) findViewById(R.id.text_option_four);

            String questionText = txtQuestion.getText().toString();
            String optionOne = txtOptionOne.getText().toString();
            String optionTwo = txtOptionTwo.getText().toString();
            String optionThree = txtOptionThree.getText().toString();
            String optionFour = txtOptionFour.getText().toString();

            Question question = new Question(questionText, optionOne, optionTwo, optionThree, optionFour, 5);
            if(question.validateQuestion(this)){
                ListView listView = (ListView) findViewById(R.id.list_view_quiz_questions);
                QuizQuestionListViewAdapter quizQuestionListViewAdapter = (QuizQuestionListViewAdapter) listView.getAdapter();
                quizQuestionListViewAdapter.add(question);
                quizQuestionListViewAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Question Added", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
