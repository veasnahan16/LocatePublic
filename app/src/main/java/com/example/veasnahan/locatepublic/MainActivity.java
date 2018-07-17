package com.example.veasnahan.locatepublic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String DEFAULTSTR = "N/A";
    private static final int RC_SIGN_IN = 9001;
    public String TAG = "LOCATE_PUBLIC";
    public TextInputLayout TIL;
    public Boolean phoneCheck;
    private FirebaseAuth mAuth;
    private ProgressDialog p;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient mGoogleSignInClient;

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isPhoneValid(String phone) {
        //String expression = "^[+][0-9]{10,13}$";
        String expression = "^[0-9]{9,10}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    //public ImageView ep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        phoneCheck = false;
        mAuth = FirebaseAuth.getInstance();
        p = new ProgressDialog(this);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setMessage("Loading");
        p.setIndeterminate(true);
        p.setProgress(0);
p.show();
        super.onCreate(savedInstanceState);

        //saved token
        sharedPreferences = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
        final String getStrEmail = sharedPreferences.getString("str_token", DEFAULTSTR);
        Toast.makeText(this, getStrEmail, Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final EditText txtEmail = findViewById(R.id.email);
        final EditText txtPassword = findViewById(R.id.password);
        Button btnSignIn = findViewById(R.id.btnSignIn);
        Button btnSignInNode = findViewById(R.id.btnSignInNode);

        Button button = findViewById(R.id.btnSignUp);
        Button btnGSignin = findViewById(R.id.btnGAccount);
        Switch swtPhone = findViewById(R.id.tglPhone);
        final ImageView ep = findViewById(R.id.ep);
        final LinearLayout laypwd = findViewById(R.id.laypwd);
        //ToggleButton btnTglPhone = (ToggleButton) findViewById(R.id.tglPhone);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);  //This is line 50
            }
        });

        TIL = findViewById(R.id.changehint);
        swtPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    phoneCheck = true;
                    TIL.setHint("Input Phone Number");
                    txtPassword.setEnabled(false);
                    txtEmail.setInputType(InputType.TYPE_CLASS_PHONE);
                    txtEmail.setText("015575284");
                    ep.setImageResource(R.drawable.ic_phone_android_black_24dp);
                    laypwd.setVisibility(View.INVISIBLE);
                } else {
                    phoneCheck = false;
                    TIL.setHint("Input E-mail");
                    txtPassword.setEnabled(true);
                    txtEmail.setText("");
                    txtEmail.setHint("");
                    txtEmail.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                    ep.setImageResource(R.drawable.ic_email_black_24dp);
                    laypwd.setVisibility(View.VISIBLE);
                }
            }
        });

        btnGSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmail.getText().toString().matches("")) {
                    txtEmail.setError("Email is Required");
                    return;
                }
                if (txtPassword.getText().toString().matches("")) {
                    txtPassword.setError("Password is Required");
                    return;
                }
                if (!isEmailValid(txtEmail.getText().toString())) {
                    txtEmail.setError("Not an Email");
                    return;
                }
                p.show();
                //signIn(txtEmail.getText().toString(), txtPassword.getText().toString());
            }
        });

        btnSignInNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneCheck) {
                    if (txtEmail.getText().toString().matches("")) {
                        txtEmail.setError("Email is Required");
                        return;
                    }
                    if (txtPassword.getText().toString().matches("")) {
                        txtPassword.setError("Password is Required");
                        return;
                    }
                    if (!isEmailValid(txtEmail.getText().toString())) {
                        txtEmail.setError("Not an Email");
                        return;
                    }
                    p.show();
                    signInWithNode(txtEmail.getText().toString(), txtPassword.getText().toString());
                } else {
                    if (!isPhoneValid(txtEmail.getText().toString())) {
                        txtEmail.setError("Not a Phone number");
                        return;
                    }
                    signInWithNodePhone(txtEmail.getText().toString());
                }
            }
        });

        if (!getStrEmail.equals("N/A")) {

            RequestQueue q1 = Volley.newRequestQueue(this);
            String url1 = getString(R.string.ip_map) + "products/gett";
            final HashMap d1 = new HashMap();
            String tokenn = "Bearer " + getStrEmail;
            //d1.put("Authorization", tokenn);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url1, new JSONObject(d1), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String m = response.getString("message");
                        //showPasswordErrorDialog("Success", m);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
p.hide();
                    Intent intent1 = new Intent(MainActivity.this, HomeTwoActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    sharedPreferences.edit().remove("str_token").apply();
p.hide();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", "Bearer " + getStrEmail);
                    return params;
                    //return super.getHeaders();
                }
            };
            q1.add(jsonObjectRequest);
        }else{p.hide();
        Log.i("TAGG", "p.hide()");}

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.i(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.i(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.i(TAG, "On Pause");
    }

    @Override
    protected void onStart() {
        super.onStart();
/*
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            sharedPreferences = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.commit();

            Intent intent = new Intent(MainActivity.this, HomeTwoActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            //Log.i("GG", "Email Null");
        }
*/
    }


/*
    public void signIn(final String strEmail, String strPassword) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            Log.i("DD", "Succs");
                            p.hide();
                            Intent intent = new Intent(MainActivity.this, HomeTwoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            sharedPreferences = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", strEmail);
                            editor.commit();
                            startActivity(intent);
                        } else {
                            Log.i("DD", task.getException().toString());
                            p.hide();
                            showPasswordErrorDialog(getString(R.string.sign_in_err), task.getException().toString());
                        }
                    }
                });
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Toast.makeText(this, "About ", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signInWithNode(final String strEmail, String strPassword) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.ip_map) + "user/login";
        final HashMap data = new HashMap();
        data.put("email", strEmail);
        data.put("password", strPassword);

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                p.hide();
                try {
                    String message = response.getString("message");
                    if (response.has("token")) {
                        String token = response.getString("token");
                        Intent intent = new Intent(MainActivity.this, HomeTwoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sharedPreferences = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("str_token", token);
                        editor.commit();
                        startActivity(intent);
                    } else {
                        showPasswordErrorDialog("Error", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i(TAG, "onErrorResponse");
                //Log.i("TAGG", error.toString());
                p.hide();
                showPasswordErrorDialog("Error", error.toString());
            }
        }) {

        };
        queue.add(jsonobj);
    }


    public void signInWithNodePhone(final String strPhone) {
        p.show();
        String phonereal;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.ip_map) + "user/phonelogin";
        final HashMap data = new HashMap();
        phonereal = "+855" + strPhone.toString();
        data.put("number", phonereal);

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //showPasswordErrorDialog("title", "text");
                    String type = response.getString("type");
                    String message = response.getString("message");
                    if (type.equals("PH_CON")) {
                        p.hide();
                        OpenCategroyDialogBox(message);
                    } else {
                        p.hide();
                        showPasswordErrorDialog("Error", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                p.hide();
                showPasswordErrorDialog("Error", error.toString());
            }
        }) {
        };
        queue.add(jsonobj);
    }

    private void showPasswordErrorDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent = new Intent(MainActivity.this, HomeTwoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    sharedPreferences = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", user.getEmail().toString());
                    editor.commit();
                    startActivity(intent);
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    //updateUI(null);
                }
                // [START_EXCLUDE]
                //hideProgressDialog();
                // [END_EXCLUDE]
            }
        });
    }

    // [END auth_with_google]
    private void OpenCategroyDialogBox(final String msg) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.addnewcategory, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Confirmation Code");
        alert.setView(promptView);
        final EditText input = promptView.findViewById(R.id.etCategory);
        input.requestFocus();
        input.setHint("Confirmation Code");
        input.setTextColor(Color.BLACK);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String newCategoryName = input.getText().toString();
                // Do something with value!
                if (newCategoryName.equals("")) {
                    input.setError("Name Required");
                    OpenCategroyDialogBox("");
                } else {
                    functionClickOkayWithcode(msg, newCategoryName);  //req_id and code
                    Toast.makeText(getApplicationContext(), newCategoryName.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                Toast.makeText(getApplicationContext(), "Ok Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        // create an alert dialog
        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void functionClickOkayWithcode(final String reqid, String code) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.ip_map) + "user/phoneconf";
        final HashMap data = new HashMap();
        data.put("request_id", reqid);
        data.put("code", code);

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("token")) {
                        String token = response.getString("token");
                        Intent intent = new Intent(MainActivity.this, HomeTwoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        sharedPreferences = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("str_token", token);
                        editor.commit();
                        startActivity(intent);
                    } else {
                        OpenCategroyDialogBox(reqid);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showPasswordErrorDialog("Error", error.toString());
            }
        }) {

        };
        queue.add(jsonobj);
    }

    @Override
    public void onClick(View v) {

    }
}
//71fb53fd1a94174d2b9b93e90dcd68074a54737an1hr9yA1owr4qpfBBmf9vAX5b