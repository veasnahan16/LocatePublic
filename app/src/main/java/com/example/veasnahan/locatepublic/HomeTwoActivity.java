package com.example.veasnahan.locatepublic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeTwoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    List<Product> productList;
    RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    public static final String DEFAULTSTR = "N/A";
    private SharedPreferences sharedPreferences;
    private static final int MENU_ADD = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );


        mAuth = FirebaseAuth.getInstance ();
        setContentView ( R.layout.activity_home_two );
        Toolbar toolbar = findViewById ( R.id.toolbar );
        setSupportActionBar ( toolbar );

        FloatingActionButton fab = findViewById ( R.id.fab );
        fab.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Snackbar.make ( view, "Replace with your own action", Snackbar.LENGTH_LONG ).setAction ( "Action", null ).show ();
            }
        } );

        DrawerLayout drawer = findViewById ( R.id.drawer_layout );
        /*COMMAND FOR NAVIGATION MENU*/
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle ( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        //drawer.addDrawerListener ( toggle );
        //toggle.syncState ();


        NavigationView navigationView = findViewById ( R.id.nav_view );
        View headerView = navigationView.getHeaderView ( 0 );
        final TextView navUsername = (TextView) headerView.findViewById ( R.id.tokenmail );
        //navUsername.setText("Your Text Here");
        navigationView.setNavigationItemSelectedListener ( this );

        FirebaseUser currentUser = mAuth.getCurrentUser ();
        if (currentUser != null) {
            String email = currentUser.getEmail ();
            Log.i ( "Home2", email );
        }

        sharedPreferences = getSharedPreferences ( "Mydata", Context.MODE_PRIVATE );
        final String getStrEmail = sharedPreferences.getString ( "str_token", DEFAULTSTR );
        Toast.makeText ( this, getStrEmail, Toast.LENGTH_LONG ).show ();
        Log.i ( "TAG", getStrEmail );
        if (!getStrEmail.equals ( "N/A" )) {
            RequestQueue q1 = Volley.newRequestQueue ( this );
            String url1 = getString ( R.string.ip_map ) + "products/gett";
            final HashMap d1 = new HashMap ();
            String tokenn = "Bearer " + getStrEmail;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest ( Request.Method.POST, url1, new JSONObject ( d1 ), new Response.Listener<JSONObject> () {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i ( "TAG", "suc response" );
                    try {
                        String mailtok = response.getString ( "tok_user" );
                        Log.i ( "TAG", mailtok );
                        navUsername.setText ( mailtok );
                        //showPasswordErrorDialog("Success", m);
                    } catch (JSONException e) {
                        Log.i ( "TAG", "e catch" );
                        e.printStackTrace ();
                    }

                }
            }, new Response.ErrorListener () {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i ( "tokenn", "suck" );
                    sharedPreferences.edit ().remove ( "str_token" ).apply ();
                    Intent intent1 = new Intent ( HomeTwoActivity.this, MainActivity.class );
                    intent1.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity ( intent1 );
                }
            } ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String> ();
                    params.put ( "Content-Type", "application/json; charset=UTF-8" );
                    params.put ( "Authorization", "Bearer " + getStrEmail );
                    return params;
                    //return super.getHeaders();
                }
            };
            q1.add ( jsonObjectRequest );

        }


        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById ( R.id.recyclerView );
        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );

        //initializing the productlist
        productList = new ArrayList<> ();
        final String URL_DATA = getString ( R.string.ip_map ) + "products/getall";

        RequestQueue queue = Volley.newRequestQueue ( this );
        JsonObjectRequest jsonobj = new JsonObjectRequest ( Request.Method.GET, URL_DATA, null, new Response.Listener<JSONObject> () {
            @Override
            public void onResponse(JSONObject response) {

                if (response.has ( "products" )) {
                    try {
                        String products = response.getString ( "products" );
                        JSONArray products_array = response.getJSONArray ( "products" );
                        for (int i = 0; i < products_array.length (); i++) {
                            JSONObject o = products_array.getJSONObject ( i );

                            productList.add ( new Product ( o.getString ( "name" ),o.getString ( "eng" ), o.getString ( "khm" ), o.getString ( "url" ), o.getBoolean ( "bybrand" ) ) );

                        }
                        Log.i ( "TAGGG", products_array.toString () );
                        //creating recyclerview adapter
                        ProductAdapter adapter = new ProductAdapter ( HomeTwoActivity.this, productList );

                        //setting adapter to recyclerview
                        recyclerView.setAdapter ( adapter );
                        //Log.i("TAGG", String.valueOf(the_json_array.length ()));
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
                }

            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i ( "TAGG", "error" );
            }
        } ) {

        };
        queue.add ( jsonobj );


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById ( R.id.drawer_layout );
        if (drawer.isDrawerOpen ( GravityCompat.START )) {
            drawer.closeDrawer ( GravityCompat.START );
        } else {
            super.onBackPressed ();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear ();
        //getMenuInflater().inflate(R.menu.home_two, menu);
        sharedPreferences = getSharedPreferences ( "Mydata", Context.MODE_PRIVATE );
        String getStrEmail = sharedPreferences.getString ( "email", DEFAULTSTR );
        getMenuInflater ().inflate ( R.menu.home_two, menu );
        if (getStrEmail.matches ( "pomchanveasna@gmail.com" )) {
            menu.add ( 108, MENU_ADD, 103, "Local Adm" );
        }

        return super.onPrepareOptionsMenu ( menu );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home_two, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            mAuth.signOut ();
            Intent intent = new Intent ( HomeTwoActivity.this, MainActivity.class );
            startActivity ( intent );
            //sharedPreferences.edit().remove("Mydata").commit();
            sharedPreferences.edit ().remove ( "str_token" ).apply ();
            finish ();
        }

        if (id == 20) {
            Log.i ( "GG", "Local Add" );
        }
        return super.onOptionsItemSelected ( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId ();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(HomeTwoActivity.this, MyLocationUsingLocationAPI.class);
            startActivity(intent);
            Log.i("nav_camera","nav_camera");
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById ( R.id.drawer_layout );
        drawer.closeDrawer ( GravityCompat.START );
        return true;
    }


    public void onClickBrand(View v) {
        Log.i ( "TAGG", "clicked text view" );
    }

}
