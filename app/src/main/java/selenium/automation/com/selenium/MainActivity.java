package selenium.automation.com.selenium;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class MainActivity extends AppCompatActivity {
    private EditText etuserEmail, etuserPassword;
    private Button btlogin;

    private TextView tvinfo, tvAttempts;
    private int No_Of_LoginAttempts = 3;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

//      find all the elements
        etuserEmail = (EditText) findViewById(R.id.etUserName);
        etuserPassword = (EditText) findViewById(R.id.etPassword);
        btlogin = (Button) findViewById(R.id.btLogin);

        tvinfo = (TextView) findViewById(R.id.info);
        tvAttempts = (TextView) findViewById(R.id.tvAttempts);

        tvAttempts.setText("No of Attempts Remaining: 3");

//      login by clicking on Login button
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
             }
        });

        tvinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

    }

    private void userLogin(){

       String userEmail = etuserEmail.getText().toString().trim();
       String password = etuserPassword.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            etuserEmail.setError("Valid Email is Required");
            etuserEmail.requestFocus();
            return;
        }

        if(password.length()<6){
            etuserPassword.setError("Minimum length should be 6");
            etuserPassword.requestFocus();
            return;

        }
        if (No_Of_LoginAttempts != 0) {
            mAuth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        finish();
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        No_Of_LoginAttempts--;
                        tvAttempts.setText("No Of Attempts Remaining: " + String.valueOf(No_Of_LoginAttempts));
                    }
                }
            });

        } else {
            btlogin.setEnabled(false);
            Toast.makeText(MainActivity.this, "Max Login Attempts Tried", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}
