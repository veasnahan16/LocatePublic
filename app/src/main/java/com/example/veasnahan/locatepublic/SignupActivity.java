package com.example.veasnahan.locatepublic;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        p = new ProgressDialog(this);
        // final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final EditText txtEmail = (EditText) findViewById(R.id.email);
        final EditText txtPswd1 = (EditText) findViewById(R.id.pswd1);
        final EditText txtPswd2 = (EditText) findViewById(R.id.pswd2);
        Button button = (Button) findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                p.setMessage("Loading");
                p.setIndeterminate(true);
                p.setProgress(0);

                String strEmail = txtEmail.getText().toString();
                String strPswd1 = txtPswd1.getText().toString();
                String strPswd2 = txtPswd2.getText().toString();

                if (strEmail.matches("")) {
                    txtEmail.setError("Email is Required");
                    return;
                } else if (strPswd1.matches("")) {
                    txtPswd1.setError("Password is Required");
                    return;
                } else if (strPswd2.matches("")) {
                    txtPswd2.setError("Password is Required");
                    return;
                }
                if (!isEmailValid(strEmail)) {
                    txtEmail.setError("Not an Email");
                    return;
                }
                if (!strPswd1.equals(strPswd2)) {
                    txtPswd1.setText("");
                    txtPswd2.setText("");
                    showPasswordErrorDialog(getString(R.string.dialog_title), getString(R.string.dialog_message));
                    return;
                }
                p.show();
                register(strEmail, strPswd1);

            }
        });


        //  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void register(String strEmail, String strPswd1) {
        mAuth.createUserWithEmailAndPassword(strEmail, strPswd1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseAuth.getInstance().signOut();
                            p.hide();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            p.hide();
                            showPasswordErrorDialog(getString(R.string.dialog_error), task.getException().toString());
                            Log.i("SS", "DD");
                        }
                    }

                });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void showPasswordErrorDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle(title);
        builder.setMessage(text);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }
}
