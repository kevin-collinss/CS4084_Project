package ie.ul.ulthrift;

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

public class LoginActivity extends AppCompatActivity {
    EditText  email, password;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }

    public void signin(View view) {
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

        auth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));

                                }   else {
                                Toast.makeText(LoginActivity.this, "Login Failed"+task.getException(), Toast.LENGTH_SHORT).show();

                            }
                            }
                        });


    }

    public void signup(View view) {
        startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
    }
}