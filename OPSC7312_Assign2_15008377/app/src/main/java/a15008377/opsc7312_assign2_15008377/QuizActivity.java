package a15008377.opsc7312_assign2_15008377;

import android.os.CountDownTimer;
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

    //Method fetches the Quiz passed in from the previous Activity
    public void fetchQuiz(){
        try{
            Bundle bundle = getIntent().getExtras();
            quiz = (Quiz) bundle.getSerializable("quiz");
            displayQuiz();
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
                Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
            }
            displayCorrectAnswer(0);
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
                Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
            }
            displayCorrectAnswer(1);
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
                Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
            }
            displayCorrectAnswer(2);
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
                Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
            }
            displayCorrectAnswer(3);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method displays the correct answer in green
    public void displayCorrectAnswer(final int optionChosen){
        try{
            //Button assignments
            Button btnOptionOne = (Button) findViewById(R.id.button_option_one);
            Button btnOptionTwo = (Button) findViewById(R.id.button_option_two);
            Button btnOptionThree = (Button) findViewById(R.id.button_option_three);
            Button btnOptionFour = (Button) findViewById(R.id.button_option_four);
            final Button[] buttons = {btnOptionOne, btnOptionTwo, btnOptionThree, btnOptionFour};

            //Gets correct answer and turns the correct answer green, and the user's answer red if their answer is incorrect
            final int correctAnswer = quiz.getLstQuestions().get(questionNumber).getAnswerPosition();
            buttons[correctAnswer].setBackgroundResource(R.drawable.button_rounded_correct_answer);
            if(optionChosen != correctAnswer){
                buttons[optionChosen].setBackgroundResource(R.drawable.button_rounded_incorrect_answer);
            }

            //Pauses the program to allow the user to see the correct answer
            new CountDownTimer(5000, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    //Resets the buttons back to their normal colours and displays the next question
                    buttons[correctAnswer].setBackgroundResource(R.drawable.button_rounded);
                    buttons[optionChosen].setBackgroundResource(R.drawable.button_rounded);
                    questionNumber++;
                    displayQuiz();
                }
            }.start();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}