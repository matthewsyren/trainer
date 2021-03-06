/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class allows the user to add a new Quiz
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class QuizSetterActivity extends AppCompatActivity {
    //Declarations
    private final int SPEECH_INPUT_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz_setter);

            //Sets adapter for ListView
            ListView listView = (ListView) findViewById(R.id.list_view_quiz_questions);
            QuizQuestionListViewAdapter quizQuestionListViewAdapter = new QuizQuestionListViewAdapter(this, new ArrayList<Question>());
            listView.setAdapter(quizQuestionListViewAdapter);

            //Adds options to Spinner
            Spinner spinner = (Spinner) findViewById(R.id.spinner_correct_answer);
            String[] possibleAnswers = {"Option One", "Option Two", "Option Three", "Option Four"};
            ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_row, R.id.text_spinner_item_id, possibleAnswers);
            spinner.setAdapter(adapter);

            //Makes ListView within a ScrollView scrollable (Learnt from https://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view)
            listView.setOnTouchListener(new View.OnTouchListener() {
                //Sets onTouchListener to allow scrolling in the ListView within a ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            //Displays Back button in ActionBar
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("Quiz Creator");
            }

            //Prevents the keyboard from appearing automatically. Learnt from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method toggles the ProgressBar's visibility
    public void toggleProgressBarVisibility(int visible){
        try{
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(visible);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Takes the user back to the QuizManagerActivity when the back button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try{
            int id = item.getItemId();

            //Takes the user back to the QuizManagerActivity if the button that was pressed was the back button
            if (id == android.R.id.home) {
                Intent intent = new Intent(QuizSetterActivity.this, QuizManagerActivity.class);
                startActivity(intent);
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    //Method adds a new Question to the ListView
    public void addQuestionOnClick(View view){
        try{
            //View assignments
            EditText txtQuestion = (EditText) findViewById(R.id.text_question);
            EditText txtOptionOne = (EditText) findViewById(R.id.text_option_one);
            EditText txtOptionTwo = (EditText) findViewById(R.id.text_option_two);
            EditText txtOptionThree = (EditText) findViewById(R.id.text_option_three);
            EditText txtOptionFour = (EditText) findViewById(R.id.text_option_four);
            Spinner spnCorrectAnswer = (Spinner) findViewById(R.id.spinner_correct_answer);

            //Getting information from Views
            String questionText = txtQuestion.getText().toString();
            String optionOne = txtOptionOne.getText().toString();
            String optionTwo = txtOptionTwo.getText().toString();
            String optionThree = txtOptionThree.getText().toString();
            String optionFour = txtOptionFour.getText().toString();
            int correctAnswerPosition = spnCorrectAnswer.getSelectedItemPosition();

            //Adds Question to ListView if the Question is valid (no missing information)
            Question question = new Question(questionText, optionOne, optionTwo, optionThree, optionFour, correctAnswerPosition);
            if(question.validateQuestion(this)){
                //Adds Question to ListView adapter
                ListView listView = (ListView) findViewById(R.id.list_view_quiz_questions);
                QuizQuestionListViewAdapter quizQuestionListViewAdapter = (QuizQuestionListViewAdapter) listView.getAdapter();
                quizQuestionListViewAdapter.add(question);
                quizQuestionListViewAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Question Added", Toast.LENGTH_LONG).show();

                //Clears TextViews
                txtQuestion.setText("");
                txtOptionOne.setText("");
                txtOptionTwo.setText("");
                txtOptionThree.setText("");
                txtOptionFour.setText("");
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Uploads the quiz to Firebase
    public void addQuizOnClick(View view){
        try{
            EditText txtQuizName = (EditText) findViewById(R.id.text_quiz_name);
            String quizName = txtQuizName.getText().toString();
            ListView listView = (ListView) findViewById(R.id.list_view_quiz_questions);
            QuizQuestionListViewAdapter quizQuestionListViewAdapter = (QuizQuestionListViewAdapter) listView.getAdapter();
            ArrayList<Question> lstQuestions = new ArrayList<>();

            //Adds all Questions to the Quiz
            for(int i = 0; i < quizQuestionListViewAdapter.getCount(); i++){
                Question question = (Question) quizQuestionListViewAdapter.getItem(i);
                lstQuestions.add(question);
            }

            //Writes Quiz to Firebase if the Quiz is valid (no missing information)
            Quiz quiz = new Quiz(quizName, lstQuestions);
            if(quiz.validateQuiz(this)){
                quiz.requestWriteOfQuiz(this, null, new DataReceiver(new Handler()));

                //Displays ProgressBar
                toggleProgressBarVisibility(View.VISIBLE);
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Listens to user's speech input
    public void listenToSpeechOnClick(View view){
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say the question aloud");

            startActivityForResult(intent, SPEECH_INPUT_RESULT_CODE);
        }
        catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Speech input not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_INPUT_RESULT_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    //Writes the user's speech input result to the text_question EditText
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    TextView txtQuestion = (TextView) findViewById(R.id.text_question);
                    txtQuestion.setText(result.get(0));
                }
                break;
            }
        }
    }

    //Processes the data received from FirebaseService
    private class DataReceiver extends ResultReceiver {
        private DataReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult ( int resultCode, Bundle resultData){
            //Processes the result when the Quiz has been written to the Firebase Database
            if (resultCode == FirebaseService.ACTION_WRITE_QUIZ_RESULT_CODE) {
                Toast.makeText(getApplicationContext(), "Quiz uploaded successfully", Toast.LENGTH_LONG).show();

                //Hides ProgressBar
                toggleProgressBarVisibility(View.INVISIBLE);

                //Refreshes the QuizActivity
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }
}