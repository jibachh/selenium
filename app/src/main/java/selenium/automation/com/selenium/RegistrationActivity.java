package selenium.automation.com.selenium;

import android.app.ProgressDialog;
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

public class RegistrationActivity extends AppCompatActivity {
    private EditText etRegusername,etRegPassword,etRegEmail;
    private Button register;
    private TextView registrationResult,GoToLoginActivity;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

         progressDialog = new ProgressDialog(this);
         firebaseAuth  = FirebaseAuth.getInstance();


         etRegEmail = (EditText)findViewById(R.id.etRegEmail);
         etRegPassword =  (EditText)findViewById(R.id.etRegPassword);

         register = (Button)findViewById(R.id.btRegister);
         GoToLoginActivity = (TextView)findViewById(R.id.tvGoToLogin);



         register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

              registerUser();
             }
         });

        GoToLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void registerUser(){

        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();


        if(email.isEmpty()){
            Toast.makeText(RegistrationActivity.this,"Email ID empty",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etRegEmail.setError("Valid Email is Required");
            etRegEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            Toast.makeText(RegistrationActivity.this,"Password empty",Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length()<6){
            etRegPassword.setError("Password should be at least 6 character long");
            etRegPassword.requestFocus();
            return;
        }
        progressDialog.setMessage("Registering User.....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    finish();
                    Toast.makeText(getApplicationContext(),"User Registered Succesfully",Toast.LENGTH_SHORT).show();
                }
                else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"User Already registered",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Could not Registered....Please try again later",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
