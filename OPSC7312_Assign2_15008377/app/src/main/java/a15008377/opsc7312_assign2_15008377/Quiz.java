/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class provides the basis for a Quiz object
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.Intent;

import android.os.ResultReceiver;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz implements Serializable{
    //Declarations
    private String name;
    private String key;
    private ArrayList<Question> lstQuestions;

    //Default constructor (needed for Firebase)
    public Quiz(){

    }

    //Constructor
    public Quiz(String name, ArrayList<Question> lstQuestions) {
        this.name = name;
        this.lstQuestions = lstQuestions;
    }

    //Getter methods
    public String getName() {
        return name;
    }

    public ArrayList<Question> getLstQuestions() {
        return lstQuestions;
    }

    public String getKey() {
        return key;
    }

    //Setter method
    public void setKey(String key) {
        this.key = key;
    }

    //Ensures that all Quiz data is valid (returns true if data is valid)
    public boolean validateQuiz(Context context){
        boolean valid = false;
        if(name.isEmpty()){
            Toast.makeText(context, "Please enter a name for the quiz", Toast.LENGTH_LONG).show();
        }
        else if(lstQuestions.isEmpty()){
            Toast.makeText(context, "Please add at least one question to the quiz", Toast.LENGTH_LONG).show();
        }
        else{
            valid = true;
        }

        return valid;
    }

    //Requests Quizzes from the Firebase Database
    public void requestQuizzes(String searchTerm, Context context, ResultReceiver resultReceiver){
        try{
            //Requests Quiz information from the FirebaseService class
            Intent intent = new Intent(context, FirebaseService.class);
            intent.putExtra(FirebaseService.QUIZ_ID, searchTerm);
            intent.setAction(FirebaseService.ACTION_FETCH_QUIZ);
            intent.putExtra(FirebaseService.RECEIVER, resultReceiver);
            context.startService(intent);
        }
        catch(Exception exc){
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method calls the FirebaseService class and passes in a Quiz object that must be written to the Firebase Database
    public void requestWriteOfQuiz(Context context, String action, ResultReceiver resultReceiver){
        try{
            //Requests location information from the LocationService class
            Intent intent = new Intent(context, FirebaseService.class);
            intent.setAction(FirebaseService.ACTION_WRITE_QUIZ);
            intent.putExtra(FirebaseService.ACTION_WRITE_QUIZ, this);
            intent.putExtra(FirebaseService.ACTION_WRITE_QUIZ_INFORMATION, action);
            intent.putExtra(FirebaseService.RECEIVER, resultReceiver);
            context.startService(intent);
        }
        catch(Exception exc){
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
