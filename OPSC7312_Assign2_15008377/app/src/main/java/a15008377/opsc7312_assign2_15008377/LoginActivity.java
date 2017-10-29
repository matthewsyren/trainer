/*
 * Author: Matthew Syrén
 *
 * Date:   10 October 2017
 *
 * Description: Class used to allow the user to login to their account
 */

package a15008377.opsc7312_assign2_15008377;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    //Declarations
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            firebaseAuth = FirebaseAuth.getInstance();

            //Hides ProgressBar
            toggleProgressBarVisibility(View.INVISIBLE);

            //Takes the user to the appropriate Activity if they have already signed in
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

            //Tries to sign the user in using the Firebase Authentication Database
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

            //Takes the user to the appropriate Activity
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

    //Method initiates the password recovery feature of this app
    public void forgotPasswordOnClick(View view){
        try{
            displayInputMessage("Please enter your email address. An email with a link to reset your password will be sent to you.");
        }
        catch(Exception exc){
            Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Method displays an AlertDialog to get an email address from the user
    public void displayInputMessage(String message){
        try{
            //Creates AlertDialog content
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            TextView textView = new TextView(this);
            textView.setText(message);
            textView.setTypeface(null, Typeface.BOLD);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

            //Adds content to AlertDialog
            LinearLayout relativeLayout = new LinearLayout(this);
            relativeLayout.setOrientation(LinearLayout.VERTICAL);
            relativeLayout.addView(textView);
            relativeLayout.addView(input);
            alertDialog.setView(relativeLayout);

            //Creates OnClickListener for the Dialog message
            DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int button) {
                    switch(button){
                        case AlertDialog.BUTTON_POSITIVE:
                            String emailAddress = input.getText().toString();
                            if(emailAddress.length() > 0){
                                toggleProgressBarVisibility(View.VISIBLE);

                                //Sends the email to the user if the email address is valid
                                firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Displays message telling the user the email has been sent successfully
                                            Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            String exceptionMessage = task.getException().getMessage();

                                            //Displays appropriate error messages based on the exception message details
                                            if(exceptionMessage.contains("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                                Toast.makeText(getApplicationContext(), "There is no account associated with that email address, please re-enter your email address", Toast.LENGTH_LONG).show();
                                            }
                                            else if(exceptionMessage.contains("INVALID_EMAIL")){
                                                Toast.makeText(getApplicationContext(), "The email you entered is invalid, please ensure that the email address you enter exists", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        toggleProgressBarVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                            else{
                                //Asks the user to enter a valid email address
                                displayInputMessage("Please enter your email address");
                            }
                            break;
                    }
                }
            };

            //Assigns button and OnClickListener for the AlertDialog and displays the AlertDialog
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", dialogOnClickListener);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
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
                intent = new Intent(LoginActivity.this, QuizManagerActivity.class);
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