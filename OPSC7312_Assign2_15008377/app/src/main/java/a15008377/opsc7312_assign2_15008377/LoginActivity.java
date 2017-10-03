/*
 * Author: Matthew Syrén
 *
 * Date:   29 August 2017
 *
 * Description: Class used to allow the user to login to their account
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            firebaseAuth = FirebaseAuth.getInstance();

            //Hides ProgressBar
            toggleProgressBarVisibility(View.INVISIBLE);

            //Takes the user to the HomeActivity if they have already signed in
            User user = new User(this);
            if(user.getUserEmailAddress() != null){
                signIn(user.isUserAdminRights());
            }
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method toggles the ProgressBar's visibility
    public void toggleProgressBarVisibility(int visibility){
        try{
            //Toggles ProgressBar visibility
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar) ;
            progressBar.setVisibility(visibility);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method prevents the user from going back to the previous Activity by clicking the back button
    @Override
    public void onBackPressed() {
    }

    //Method checks the information that the user has entered, and logs the user in if the information matches information in the Firebase Authentication Database
    public void loginOnClick(View view){
        try{
            //View assignments
            EditText txtEmail = (EditText) findViewById(R.id.text_login_email);
            EditText txtPassword = (EditText) findViewById(R.id.text_login_password);

            //Fetching input
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();
            final User user = new User(email, password);
            final LoginActivity loginActivity = this;

            //Displays ProgressBar
            toggleProgressBarVisibility(View.VISIBLE);

            //Tries to sign the user in using the Firebase authentication database
            firebaseAuth.signInWithEmailAndPassword(user.getUserEmailAddress(), user.getUserPassword()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Fetches the user's key from Firebase and then calls the writeToSharedPreferences method once the key is fetched
                        user.setKey(loginActivity);
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        toggleProgressBarVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method writes the user's data to SharedPreferences and then takes the user to the HomeActivity
    public void writeDataToSharedPreferences(String email, String key, boolean adminRights){
        try{
            //Saves the user's data in SharedPreferences
            SharedPreferences preferences = getSharedPreferences("", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userEmail", email);
            editor.putString("userKey", key);
            editor.putBoolean("userAdminRights", adminRights);
            editor.apply();

            signIn(adminRights);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method takes the user to the CreateAccountActivity
    public void createAccountOnClick(View view){
        try{
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method takes the user to the appropriate Activity when they sign in
    public void signIn(boolean adminRights){
        try{
            //Takes the user to the appropriate Activity (based on admin rights)
            Intent intent;
            if(adminRights){
                intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
            }
            else{
                intent = new Intent(LoginActivity.this, QuizFetcherActivity.class);
            }
            startActivity(intent);
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}