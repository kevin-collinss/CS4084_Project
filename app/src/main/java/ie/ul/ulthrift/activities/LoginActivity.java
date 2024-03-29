package ie.ul.ulthrift.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ie.ul.ulthrift.R;

public class LoginActivity extends AppCompatActivity {
    // Declares the  email and password variables so we can put inputs to them
    EditText  email, password;
    // Firebase instance to authenticate user
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sets the content view for this activity
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // creates firebase authentication instance
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }

    // Called when the user clicks the sign-in button
    public void signin(View view) {
        // Gets user inputs
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();



        //Check if email is empty
        if (TextUtils.isEmpty(userEmail)) {

            Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
            return;

        }

        //Check if password is empty
        if (TextUtils.isEmpty(userPassword)) {

            Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show();
            return;

        }

        //Check if password too short
        if (userPassword.length() < 8) {
            Toast.makeText(this, "Password is too short, minimum 8 characters", Toast.LENGTH_SHORT).show();
        } else if (userPassword.length() > 20) {
            // Check if the password is too long
            Toast.makeText(this, "Password is too long, maximum 20 characters!", Toast.LENGTH_SHORT).show();
        } else {
            // Check if the password contains a capital letter and a special character
            Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",.<>?]).*$");
            Matcher matcher = pattern.matcher(userPassword);


            if (!matcher.matches()) {
                Toast.makeText(this, "Password must contain a capital letter and a special character", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        // Attempts to sign in with Firebase Auth
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            // Checks if the sign-in was successful
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    // Redirects to the MainActivity upon successful login
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                }   else {
                                    //Shows login failed toast error
                                Toast.makeText(LoginActivity.this, "Login Failed"+task.getException(), Toast.LENGTH_SHORT).show();

                            }
                            }
                        });


    }


    // Redirects to RegistrationActivity when the user clicks the sign up buttoon
    public void signup(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }
}