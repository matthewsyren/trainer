package a15008377.opsc7312_assign2_15008377;

import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {
    //Declarations
    Quiz quiz;
    int questionNumber = 0;
    int correctAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz);

            fetchQuiz();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method fetches the Quiz from Firebase
    public void fetchQuiz(){
        try{
            Quiz quiz = new Quiz();
            quiz.requestQuizzes(null, this, new DataReceiver(new Handler()));
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method displays the Quiz to the user
    public void displayQuiz(){
        try{
            if(questionNumber < quiz.getLstQuestions().size()){
                //Component Assignments
                TextView txtQuestion = (TextView) findViewById(R.id.text_question);
                Button btnOptionOne = (Button) findViewById(R.id.button_option_one);
                Button btnOptionTwo = (Button) findViewById(R.id.button_option_two);
                Button btnOptionThree = (Button) findViewById(R.id.button_option_three);
                Button btnOptionFour = (Button) findViewById(R.id.button_option_four);

                Question question = quiz.getLstQuestions().get(questionNumber);
                txtQuestion.setText(question.getQuestion());
                btnOptionOne.setText(question.getOptionOne());
                btnOptionTwo.setText(question.getOptionTwo());
                btnOptionThree.setText(question.getOptionThree());
                btnOptionFour.setText(question.getOptionFour());
            }
            else{
                Toast.makeText(getApplicationContext(), "Correct answers: " + correctAnswers, Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method accepts the user's answer as option one
    public void optionOneOnClick(View view){
        try{
            if(quiz.getLstQuestions().get(questionNumber).getAnswerPosition() == 0){
                correctAnswers++;
                Toast.makeText(getApplicationContext(), "Corrrect!", Toast.LENGTH_LONG).show();
            }
            questionNumber++;
            displayQuiz();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method accepts the user's answer as option two
    public void optionTwoOnClick(View view){
        try{
            if(quiz.getLstQuestions().get(questionNumber).getAnswerPosition() == 1){
                correctAnswers++;
                Toast.makeText(getApplicationContext(), "Corrrect!", Toast.LENGTH_LONG).show();
            }
            questionNumber++;
            displayQuiz();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method accepts the user's answer as option three
    public void optionThreeOnClick(View view){
        try{
            if(quiz.getLstQuestions().get(questionNumber).getAnswerPosition() == 2){
                correctAnswers++;
                Toast.makeText(getApplicationContext(), "Corrrect!", Toast.LENGTH_LONG).show();
            }
            questionNumber++;
            displayQuiz();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method accepts the user's answer as option four
    public void optionFourOnClick(View view){
        try{
            if(quiz.getLstQuestions().get(questionNumber).getAnswerPosition() == 3){
                correctAnswers++;
                Toast.makeText(getApplicationContext(), "Corrrect!", Toast.LENGTH_LONG).show();
            }
            questionNumber++;
            displayQuiz();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class DataReceiver extends ResultReceiver {
        private DataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult ( int resultCode, Bundle resultData){
            //Processes the result when the Quiz has been written to the Firebase Database
            if (resultCode == FirebaseService.ACTION_FETCH_QUIZ_RESULT_CODE) {
                ArrayList<Quiz> lstQuizzes = (ArrayList<Quiz>) resultData.getSerializable(FirebaseService.ACTION_FETCH_QUIZ);

                if(lstQuizzes.size() >= 1){
                    quiz = lstQuizzes.get(0);
                }

                displayQuiz();
            }
        }
    }
}
