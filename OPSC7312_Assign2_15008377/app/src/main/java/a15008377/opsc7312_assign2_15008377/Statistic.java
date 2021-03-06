/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class forms the basis for a Statistic object
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.widget.Toast;

import java.io.Serializable;

public class Statistic implements Serializable{
    private String userKey;
    private String quizKey;
    private double result;

    //Constructor
    public Statistic(String quizKey, double result) {
        this.quizKey = quizKey;
        this.result = result;
    }

    //Default constructor
    public Statistic(){

    }

    //Getter methods
    public String getQuizKey() {
        return quizKey;
    }

    public double getResult() {
        return result;
    }

    public String getUserKey() {
        return userKey;
    }

    //Setter methods

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setQuizKey(String quizKey) {
        this.quizKey = quizKey;
    }

    //Requests Statistics from the Firebase Database
    public void requestStatistics(String searchTerm, Context context, ResultReceiver resultReceiver){
        try{
            //Requests Statistic information from the FirebaseService class
            Intent intent = new Intent(context, FirebaseService.class);
            intent.setAction(FirebaseService.ACTION_FETCH_STATISTIC);
            intent.putExtra(FirebaseService.USER_KEY, searchTerm);
            intent.putExtra(FirebaseService.RECEIVER, resultReceiver);
            context.startService(intent);
        }
        catch(Exception exc){
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method calls the FirebaseService class and passes in a Statistic object that must be written to the Firebase Database
    public void requestWriteOfStatistic(Context context, ResultReceiver resultReceiver){
        try{
            //Requests write of Statistic information to the FirebaseService class
            Intent intent = new Intent(context, FirebaseService.class);
            intent.setAction(FirebaseService.ACTION_WRITE_STATISTIC);
            intent.putExtra(FirebaseService.ACTION_WRITE_STATISTIC, this);
            intent.putExtra(FirebaseService.RECEIVER, resultReceiver);
            context.startService(intent);
        }
        catch(Exception exc){
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method requests that all Statistics for a specific Quiz are deleted
    public void requestDeleteOfStatistic(String quizID, Context context, ResultReceiver resultReceiver){
        try{
            //Requests deletion of Statistic information from the FirebaseService class
            Intent intent = new Intent(context, FirebaseService.class);
            intent.setAction(FirebaseService.ACTION_DELETE_STATISTIC);
            intent.putExtra(FirebaseService.QUIZ_ID, quizID);
            intent.putExtra(FirebaseService.RECEIVER, resultReceiver);
            context.startService(intent);
        }
        catch(Exception exc){
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
