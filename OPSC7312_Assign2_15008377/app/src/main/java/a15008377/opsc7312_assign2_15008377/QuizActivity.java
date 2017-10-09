package a15008377.opsc7312_assign2_15008377;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    //Declarations
    Quiz quiz;
    int questionNumber = 0;
    int correctAnswers = 0;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz);

            fetchQuiz();

            //Displays Back button in ActionBar
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.setTitle(quiz.getName());
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Takes the user back to the QuizFetcherActivity when the back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            int id = item.getItemId();

            //Takes the user back to the DeliveryControlActivity if the button that was pressed was the back button
            if (id == android.R.id.home) {
                Intent intent = new Intent(QuizActivity.this, QuizFetcherActivity.class);
                startActivity(intent);
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
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

    //Method reads the question out loud using TextToSpeech
    public void readQuestionOnClick(View view){
        try{
            textToSpeech = new TextToSpeech(this, this);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(),exc.getMessage(), Toast.LENGTH_LONG).show();
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
                saveScore();
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method saves the user's score to the Firebase Database
    public void saveScore(){
        try{
            double percentage = ((double) correctAnswers / quiz.getLstQuestions().size()) * 100;
            double result = Math.round(percentage);
            Statistic statistic = new Statistic(quiz.getKey(), result);
            statistic.requestWriteOfStatistic(this, new DataReceiver(new Handler()));

            //Displays popup that gives the User their result and asks them if they'd like to take the Quiz again
            AlertDialog alertDialog = new AlertDialog.Builder(QuizActivity.this).create();
            alertDialog.setTitle("Quiz Complete...");
            alertDialog.setMessage("You scored " + result + "%. \n\nWould you like to take the quiz again?");

            //Creates OnClickListener for the Dialog message
            DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    Intent intent;
                    switch(button){
                        case AlertDialog.BUTTON_POSITIVE:
                            intent = getIntent();
                            finish();
                            startActivity(intent);
                            break;
                        case AlertDialog.BUTTON_NEGATIVE:
                            intent = new Intent(QuizActivity.this, QuizFetcherActivity.class);
                            startActivity(intent);
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

            //Disables touch input from the user. (Learnt from https://stackoverflow.com/questions/4280608/disable-a-whole-activity-from-user-action)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            //Gets correct answer and turns the correct answer green, and the user's answer red if their answer is incorrect
            final int correctAnswer = quiz.getLstQuestions().get(questionNumber).getAnswerPosition();
            buttons[correctAnswer].setBackgroundResource(R.drawable.button_rounded_correct_answer);
            buttons[correctAnswer].setTextColor(getResources().getColor(R.color.black));
            if(optionChosen != correctAnswer){
                buttons[optionChosen].setBackgroundResource(R.drawable.button_rounded_incorrect_answer);
                buttons[optionChosen].setTextColor(getResources().getColor(R.color.black));
            }

            //Pauses the program to allow the user to see the correct answer
            new CountDownTimer(3500, 100) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    //Enables touch input for the screen
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    //Resets the buttons back to their normal colours and displays the next question
                    buttons[correctAnswer].setBackgroundResource(R.drawable.button_rounded);
                    buttons[optionChosen].setBackgroundResource(R.drawable.button_rounded);
                    buttons[correctAnswer].setTextColor(getResources().getColor(R.color.colorPrimary));
                    buttons[optionChosen].setTextColor(getResources().getColor(R.color.colorPrimary));
                    questionNumber++;
                    displayQuiz();
                }
            }.start();
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Reads the question out loud to the user
    @Override
    public void onInit(int status) {
        TextView txtQuestion = (TextView) findViewById(R.id.text_question);
        Button btnOptionOne = (Button) findViewById(R.id.button_option_one);
        Button btnOptionTwo = (Button) findViewById(R.id.button_option_two);
        Button btnOptionThree = (Button) findViewById(R.id.button_option_three);
        Button btnOptionFour = (Button) findViewById(R.id.button_option_four);

        String question = txtQuestion.getText().toString() + ". " + " Option one: " + btnOptionOne.getText().toString() + ". " + " Option two: " + btnOptionTwo.getText().toString() + ". " + " Option three: " + btnOptionThree.getText().toString() + ". " + " Option four: " + btnOptionFour.getText().toString();
        textToSpeech.setLanguage(Locale.US);
        textToSpeech.speak(question, TextToSpeech.QUEUE_FLUSH, null);
    }

    private class DataReceiver extends ResultReceiver {
        private DataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult ( int resultCode, Bundle resultData){
            //Processes the result when the Stock has been written to the Firebase Database
            if (resultCode == FirebaseService.ACTION_WRITE_STATISTIC_RESULT_CODE) {
                Toast.makeText(getApplicationContext(), "Quiz score saved successfully!", Toast.LENGTH_LONG).show();
            }
        }
    }
}