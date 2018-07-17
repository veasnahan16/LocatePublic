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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog p;

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        p = new ProgressDialog(this);
        // final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final EditText txtEmail = findViewById(R.id.email);
        final EditText txtPswd1 = findViewById(R.id.pswd1);
        final EditText txtPswd2 = findViewById(R.id.pswd2);
        Button button = findViewById(R.id.btnSignUp);
        Button btnSignUpNodeJS = findViewById(R.id.btnSignUpNodeJS);
        mAuth = FirebaseAuth.getInstance();
        btnSignUpNodeJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "MESSAGE");
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
                registerWithNodeJS(strEmail, strPswd1);

            }
        });
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
        mAuth.createUserWithEmailAndPassword(strEmail, strPswd1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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

    private void registerWithNodeJS(String strEmail, String strPswd1) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.ip_map) + "user/signup";
        HashMap data = new HashMap();
        data.put("email", strEmail);
        data.put("password", strPswd1);
        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                p.hide();

                try {
                    String type = response.getString("type");
                    String message = response.getString("message");

                    if (type.equals("M_EXT")) {
                        showPasswordErrorDialog("Error", message);
                        Log.i("ttt", type);
                    } else if (type.equals("CRT_U_S")) {
                        showPasswordSuccessDialog("Success", message);
                    }

                    Log.i("ttt", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAGG", error.toString());
                p.hide();
                showPasswordErrorDialog("Error", error.toString());
            }
        }) {
            //here I want to post data to sever
        };
        queue.add(jsonobj);
/*
For method GET
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("TAG", response);
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAGG", error.toString());
                //mTextView.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
*/
    }

    private void showPasswordErrorDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle(title);
        builder.setMessage(text);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive button logic

            }
        });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // negative button logic
            }
        });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    private void showPasswordSuccessDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle(title);
        builder.setMessage(text);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive button logic
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
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
