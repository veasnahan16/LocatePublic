package com.example.veasnahan.locatepublic;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.veasnahan.locatepublic.data.DataGenerator;
import com.example.veasnahan.locatepublic.model.Inbox;
import com.example.veasnahan.locatepublic.utils.Tools;
import com.example.veasnahan.locatepublic.widget.LineItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.google.android.gms.location.LocationListener;

public class ListMultiSelection extends AppCompatActivity {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterListInbox mAdapter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private Toolbar toolbar;

    private LocationManager locationManager;
    private LocationListener listener;
    private String latlong;
    Integer intOKay = 0;

    public static final String DEFAULTSTR = "N/A";
    private SharedPreferences sharedPreferences;

    String getBrand;
    String getType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_list_multi_selection );
        parent_view = findViewById ( R.id.lyt_parent );
        ShareFunctions.showLog ();

        initToolbar ();
        //initComponent ();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow ();
            window.clearFlags ( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            window.addFlags ( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.setStatusBarColor ( this.getResources ().getColor ( R.color.colorPrimaryDark ) );
        }


        sharedPreferences = getSharedPreferences ( "Mysubdata", Context.MODE_PRIVATE );
        getBrand = sharedPreferences.getString ( "brand", DEFAULTSTR );
        getType = sharedPreferences.getString ( "subtype", DEFAULTSTR );


        locationManager = (LocationManager) getSystemService ( LOCATION_SERVICE );

        listener = new LocationListener () {
            @Override
            public void onLocationChanged(Location location) {
                if (location == null) {
                    Log.i ( "LOC", "no location" );
                    //textView.append ( "\n " + "no location" );
                } else {
                    //textView.append ( "\n " + location.getLongitude () + " " + location.getLatitude () );
                    latlong = location.getLatitude () + "," + location.getLongitude ();
                    //request

                    initComponent ( latlong, getType, getBrand );
                    Log.i ( "LOCC", "request to server" );
                    Log.i ( "LOCC", getBrand );
                    Log.i ( "LOCC", getType );
                    Log.i ( "LOCC", latlong );
                    Toast.makeText ( getApplicationContext (), latlong, Toast.LENGTH_LONG ).show ();
                    Toast.makeText ( getApplicationContext (), getBrand, Toast.LENGTH_LONG ).show ();
                    Toast.makeText ( getApplicationContext (), getType, Toast.LENGTH_LONG ).show ();
                    locationManager.removeUpdates ( listener );
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //AVAILABLE 2; OUT_OF_SERVICE 0; TEMPORARILY_UNAVAILABLE 1;
                if (i == 2) {
                    intOKay = 2;
                }
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent ( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                startActivity ( i );
            }
        };

        configure_button ();


    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById ( R.id.toolbar );
        toolbar.setNavigationIcon ( R.drawable.ic_arrow_back_black_24dp );
        setSupportActionBar ( toolbar );
        getSupportActionBar ().setTitle ( "Inbox" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );
        Tools.setSystemBarColor ( this, R.color.red_600 );
    }

    private void initComponent1() {
        recyclerView = (RecyclerView) findViewById ( R.id.recyclerView );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );
        recyclerView.addItemDecoration ( new LineItemDecoration ( this, LinearLayout.VERTICAL ) );
        recyclerView.setHasFixedSize ( true );

        List<Inbox> items = DataGenerator.getInboxData ( this );

        //set data and list adapter
        mAdapter = new AdapterListInbox ( this, items );
        recyclerView.setAdapter ( mAdapter );
        mAdapter.setOnClickListener ( new AdapterListInbox.OnClickListener () {
            @Override
            public void onItemClick(View view, Inbox obj, int pos) {
                if (mAdapter.getSelectedItemCount () > 0) {
                    enableActionMode ( pos );
                } else {
                    // read the inbox which removes bold from the row
                    Inbox inbox = mAdapter.getItem ( pos );
                    Toast.makeText ( getApplicationContext (), "Read: " + inbox.from, Toast.LENGTH_SHORT ).show ();
                }
            }

            @Override
            public void onItemLongClick(View view, Inbox obj, int pos) {
                enableActionMode ( pos );
            }
        } );

        actionModeCallback = new ActionModeCallback ();

    }

    private void initComponent(String latlong, String getType, String getBrand) {

        Map<String, String> params = new HashMap ();
        params.put ( "subtype", getType );
        params.put ( "brand", getBrand );
        params.put ( "coord", latlong );

        JSONObject parameters = new JSONObject ( params );


        Log.i ( "aa", "initComponent" );
        recyclerView = (RecyclerView) findViewById ( R.id.recyclerView );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );
        recyclerView.addItemDecoration ( new LineItemDecoration ( this, LinearLayout.VERTICAL ) );
        recyclerView.setHasFixedSize ( true );

        //List<Inbox> items = DataGenerator.getInboxData ( this );
        final String URL_DATA = getString ( R.string.ip_map ) + "items/coord";
        RequestQueue queue = Volley.newRequestQueue ( this );
        JsonObjectRequest jsonobj = new JsonObjectRequest ( Request.Method.POST, URL_DATA, parameters, new Response.Listener<JSONObject> () {

            @Override
            public void onResponse(JSONObject response) {
                Log.i ( "aa", "success" );
                Log.i ( "aa", response.toString () );
                if (response.has ( "brands" )) {
                    List<Inbox> items = new ArrayList<> ();

                    try {
                        JSONArray products_array = response.getJSONArray ( "brands" );
                        for (int i = 0; i < products_array.length (); i++) {
                            Inbox objj = new Inbox ();
                            JSONObject o = products_array.getJSONObject ( i );
                            objj.from = o.getString ( "eng" );
                            objj.email = o.getString ( "desc" );
                            objj.geo = o.getString ( "geo" );
                            objj.message = o.getString ( "distance" );
                            items.add ( objj );
                            Log.i ( "aaa", o.getString ( "eng" ) );
                        }
                        mAdapter = new AdapterListInbox ( ListMultiSelection.this, items );
                        recyclerView.setAdapter ( mAdapter );
                        mAdapter.setOnClickListener ( new AdapterListInbox.OnClickListener () {
                            @Override
                            public void onItemClick(View view, Inbox obj, int pos) {
                                Inbox inbox = mAdapter.getItem ( pos );
                                Toast.makeText ( getApplicationContext (), "Read: " + inbox.from, Toast.LENGTH_SHORT ).show ();
                                if (inbox.geo.equals ( "" ))
                                    showPasswordErrorDialog ( getString( R.string.latlonnull), getString( R.string.msg1) );
                                else {
                                    String geo = getString ( R.string.strGeoPar ) + inbox.geo;
                                    Intent intent = new Intent ( android.content.Intent.ACTION_VIEW, Uri.parse ( geo ) );
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );

                                    startActivity ( intent );
                                    finish ();
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, Inbox obj, int pos) {
                                Inbox inbox = mAdapter.getItem ( pos );
                                Log.i ( "inbox", inbox.toString () );
                                //Toast.makeText ( getApplicationContext (), "Read Long: " + inbox.toString (), Toast.LENGTH_SHORT ).show ();
                            }
                        } );
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }

                }
            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i ( "aa", error.toString () );
            }
        } );
        queue.add ( jsonobj );


        if (1 == 2) {
            //set data and list adapter
            //   mAdapter = new AdapterListInbox ( this, items );
            //    recyclerView.setAdapter ( mAdapter );
//        mAdapter.setOnClickListener ( new AdapterListInbox.OnClickListener () {
//            @Override
//            public void onItemClick(View view, Inbox obj, int pos) {
//                if (mAdapter.getSelectedItemCount () > 0) {
//                    enableActionMode ( pos );
//                } else {
//                    // read the inbox which removes bold from the row
//                    Inbox inbox = mAdapter.getItem ( pos );
//                    Toast.makeText ( getApplicationContext (), "Read: " + inbox.from, Toast.LENGTH_SHORT ).show ();
//                }
//            }
//
//            @Override
//            public void onItemLongClick(View view, Inbox obj, int pos) {
//                enableActionMode ( pos );
//            }
//        } );

            actionModeCallback = new ActionModeCallback ();
        }
    }

    private void showPasswordErrorDialog(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListMultiSelection.this);
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

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode ( actionModeCallback );
        }
        toggleSelection ( position );
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection ( position );
        int count = mAdapter.getSelectedItemCount ();

        if (count == 0) {
            actionMode.finish ();
        } else {
            actionMode.setTitle ( String.valueOf ( count ) );
            actionMode.invalidate ();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor ( ListMultiSelection.this, R.color.blue_grey_700 );
            mode.getMenuInflater ().inflate ( R.menu.menu_delete, menu );
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId ();
            if (id == R.id.action_delete) {
                deleteInboxes ();
                mode.finish ();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections ();
            actionMode = null;
            Tools.setSystemBarColor ( ListMultiSelection.this, R.color.red_600 );
        }
    }

    private void deleteInboxes() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems ();
        for (int i = selectedItemPositions.size () - 1; i >= 0; i--) {
            mAdapter.removeData ( selectedItemPositions.get ( i ) );
        }
        mAdapter.notifyDataSetChanged ();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater ().inflate ( R.menu.menu_search_setting, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId () == android.R.id.home) {
            finish ();
        } else {
            Toast.makeText ( getApplicationContext (), item.getTitle (), Toast.LENGTH_SHORT ).show ();
        }
        return super.onOptionsItemSelected ( item );
    }


    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( "Please Turn ON your GPS Connection" ).setCancelable ( false ).setPositiveButton ( "Yes", new DialogInterface.OnClickListener () {
            public void onClick(final DialogInterface dialog, final int id) {
                startActivity ( new Intent ( Settings.ACTION_LOCATION_SOURCE_SETTINGS ) );
            }
        } ).setNegativeButton ( "No", new DialogInterface.OnClickListener () {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel ();
            }
        } );
        final AlertDialog alert = builder.create ();
        alert.show ();
    }

    void configure_button() {
        Toast.makeText ( this, "configure_button", Toast.LENGTH_LONG ).show ();
        // first check for permissions
        if (ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.i ( "LOCC", "Build.VERSION_CODES.M>=M" );
                requestPermissions ( new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10 );
            } else {
                Log.i ( "LOCC", "Build.VERSION_CODES.M<M" );
            }
            Log.i ( "LOCC", "return?" );
            return;
        }

        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
        locationManager.requestLocationUpdates ( "gps", 5000, 0, listener );
        Log.i ( "LOCC", "LOCCed" );
       /* button.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                //textView.append ( "\n " + "no location" );
                locationManager.requestLocationUpdates ( "gps", 5000, 0, listener );
            }
        } );*/

        Looper myLooper = Looper.myLooper ();
        final Handler myHandler = new Handler ( myLooper );
        myHandler.postDelayed ( new Runnable () {
            public void run() {
                if (latlong != null) {
                    //request with latlong
                    Log.i ( "LOCC", "request already" );
                    Toast.makeText ( ListMultiSelection.this, "requested already", Toast.LENGTH_LONG ).show ();
                } else {
                    //request without latlong
                    locationManager.removeUpdates ( listener );
                    Log.i ( "LOCC", "request without latlong" );
                    initComponent ( "NULL", getType, getBrand );
                    Toast.makeText ( ListMultiSelection.this, "10s no latlong", Toast.LENGTH_LONG ).show ();
                }
                //locationManager.removeUpdates(listener);
                Log.i ( "LOCC", "10000s" );
            }
        }, 5000 );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button ();
                break;
            default:
                break;
        }
    }
}

