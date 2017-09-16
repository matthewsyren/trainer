package a15008377.opsc7312_assign2_15008377;

import android.content.ContentResolver;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by Matthew Syrén on 2017/09/16.
 */

public class Question {
    //Declarations
    private String question;
    private String optionOne;
    private String optionTwo;
    private String optionThree;
    private String optionFour;
    private int answerPosition;

    public Question(){

    }

    public Question(String question, String optionOne, String optionTwo, String optionThree, String optionFour, int answerPosition) {
        this.question = question;
        this.optionOne = optionOne;
        this.optionTwo = optionTwo;
        this.optionThree = optionThree;
        this.optionFour = optionFour;
        this.answerPosition = answerPosition;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionOne() {
        return optionOne;
    }

    public String getOptionTwo() {
        return optionTwo;
    }

    public String getOptionThree() {
        return optionThree;
    }

    public String getOptionFour() {
        return optionFour;
    }

    public int getAnswerPosition() {
        return answerPosition;
    }

    //Ensures that all Question data is valid (returns true if data is valid)
    public boolean validateQuestion(Context context){
        boolean valid = false;
        if(question.isEmpty()){
            Toast.makeText(context, "Please enter a question", Toast.LENGTH_LONG).show();
        }
        else if(optionOne.isEmpty()){
            Toast.makeText(context, "Please enter a value for option one", Toast.LENGTH_LONG).show();
        }
        else if(optionTwo.isEmpty()){
            Toast.makeText(context, "Please enter a value for option two", Toast.LENGTH_LONG).show();
        }
        else if(optionThree.isEmpty()){
            Toast.makeText(context, "Please enter a value for option three", Toast.LENGTH_LONG).show();
        }
        else if(optionFour.isEmpty()){
            Toast.makeText(context, "Please enter a value for option four", Toast.LENGTH_LONG).show();
        }
        else{
            valid = true;
        }

        return valid;
    }
}
