package com.example.veasnahan.locatepublic;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.veasnahan.locatepublic.model.Image;
import com.example.veasnahan.locatepublic.utils.Tools;
import com.example.veasnahan.locatepublic.widget.SpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridTwoLine extends AppCompatActivity {

    private String single_choice_selected;
    private String[] RINGTONE1 = new String[]{"None", "Callisto", "Ganymede", "Luna"};
    private String[] RINGTONE = {"Abundance", "Anxiety", "Bruxism", "Discipline", "Drug Addiction"};
    List<String> str = new ArrayList<> ();
    String[] myArray = new String[10];
    //this adds an element to the list.

    List<Product> productList;
    List<Item> itemList;
    private View parent_view;
    private static Random r = new Random ();
    private RecyclerView recyclerView;
    private AdapterGridTwoLine mAdapter;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i ( "ITEM_PRDD", "onCreate" );
        String newString;
        if (savedInstanceState == null) {
            Bundle extras = getIntent ().getExtras ();
            if (extras == null) {
                newString = null;
            } else {
                newString = extras.getString ( "itemname" );
            }
        } else {
            newString = (String) savedInstanceState.getSerializable ( "STRING_I_NEED" );
        }
//        Log.i ( "ITEM_PRDD", newString );
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_grid_two_line );
        parent_view = findViewById ( android.R.id.content );

        initToolbar ();
        initComponent ( newString );
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow ();
            window.clearFlags ( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            window.addFlags ( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.setStatusBarColor ( this.getResources ().getColor ( R.color.colorPrimaryDark ) );
        }
    }

    @Override
    protected void onPause() {
        super.onPause ();
        finish ();
        Log.i ( "ITEM_PRDD", "onPause" );
    }

    @Override
    protected void onStart() {
        super.onStart ();
        Log.i ( "ITEM_PRDD", "onStart" );
    }

    @Override
    public void onResume(){
        super.onResume();
//        initToolbar ();
//        initComponent ( "MART" );
        Log.i ( "ITEM_PRDD", "onResume" );
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        toolbar.setNavigationIcon ( R.drawable.ic_arrow_back_black_24dp );
        setSupportActionBar ( toolbar );
        getSupportActionBar ().setTitle ( "List Name Brand" );
        getSupportActionBar ().setDisplayHomeAsUpEnabled ( true );
        Tools.setSystemBarColor ( this, R.color.grey_1000 );
    }

    private void initComponent1() {

        recyclerView = (RecyclerView) findViewById ( R.id.recyclerView );
        recyclerView.setLayoutManager ( new GridLayoutManager ( this, 2 ) );
        //recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );
        recyclerView.addItemDecoration ( new SpacingItemDecoration ( 2, Tools.dpToPx ( this, 3 ), true ) );
        recyclerView.setHasFixedSize ( true );


        //List<Image> items = DataGenerator.getImageDate(this);
        //items.addAll(DataGenerator.getImageDate(this));
        //items.addAll(DataGenerator.getImageDate(this));
        //items.addAll(DataGenerator.getImageDate(this));

        //productList = new ArrayList<> ();
        final String URL_DATA = getString ( R.string.ip_map ) + "products/getall";

        RequestQueue queue = Volley.newRequestQueue ( this );
        JsonObjectRequest jsonobj = new JsonObjectRequest ( Request.Method.GET, URL_DATA, null, new Response.Listener<JSONObject> () {
            @Override
            public void onResponse(JSONObject response) {

                if (response.has ( "products" )) {
                    try {
                        List<Image> items = new ArrayList<> ();
                        String products = response.getString ( "products" );
                        JSONArray products_array = response.getJSONArray ( "products" );
                        for (int i = 0; i < products_array.length (); i++) {
                            Image obj = new Image ();

                            JSONObject o = products_array.getJSONObject ( i );
                            //productList.add ( new Product ( o.getString ( "name" ),o.getString ( "eng" ), o.getString ( "khm" ), o.getString ( "url" ), o.getBoolean ( "bybrand" ) ) );
                            obj.image = o.getString ( "url" );
                            obj.name = o.getString ( "eng" );
                            obj.brief = o.getString ( "khm" );
                            items.add ( obj );
                        }
                        //Log.i ( "ITEM_PRD", products_array.toString () );

                        mAdapter = new AdapterGridTwoLine ( GridTwoLine.this, items );
                        recyclerView.setAdapter ( mAdapter );

                        // on item list clicked
                        mAdapter.setOnItemClickListener ( new AdapterGridTwoLine.OnItemClickListener () {
                            @Override
                            public void onItemClick(View view, Image obj, int position) {
                                Snackbar.make ( parent_view, obj.name + " clicked", Snackbar.LENGTH_SHORT ).show ();
                            }
                        } );
                        //creating recyclerview adapter
                        //        ProductAdapter adapter = new ProductAdapter ( GridTwoLine.this, productList );

                        //setting adapter to recyclerview
                        //        recyclerView.setAdapter ( adapter );
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


//        //set data and list adapter
//        mAdapter = new AdapterGridTwoLine(this, items);
//        recyclerView.setAdapter(mAdapter);
//
//        // on item list clicked
//        mAdapter.setOnItemClickListener(new AdapterGridTwoLine.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, Image obj, int position) {
//                Snackbar.make(parent_view, obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
//            }
//            });

    }

    private void initComponent(String Str) {

        recyclerView = (RecyclerView) findViewById ( R.id.recyclerView );
        recyclerView.setLayoutManager ( new GridLayoutManager ( this, 2 ) );
        recyclerView.addItemDecoration ( new SpacingItemDecoration ( 2, Tools.dpToPx ( this, 3 ), true ) );
        recyclerView.setHasFixedSize ( true );

        //productList = new ArrayList<> ();
        //itemList = new ArrayList<> ();
        //final String URL_DATA = getString ( R.string.ip_map ) + "products/getall";
        //final String URL_DATA = "http://DESKTOP-ELTFP3E:3000/items/bank";
        final String URL_DATA = getString ( R.string.ip_map ) + "items/" + Str;

        RequestQueue queue = Volley.newRequestQueue ( this );
        JsonObjectRequest jsonobj = new JsonObjectRequest ( Request.Method.GET, URL_DATA, null, new Response.Listener<JSONObject> () {
            @Override
            public void onResponse(JSONObject response) {
                Log.i ( "ITEM_PRD", response.toString () );
                if (response.has ( "brands" )) {

                    try {
                        List<Image> items = new ArrayList<> ();

                        JSONArray products_array = response.getJSONArray ( "brands" );
                        for (int i = 0; i < products_array.length (); i++) {
                            Image obj = new Image ();

                            JSONObject o = products_array.getJSONObject ( i );
                            obj.image = o.getString ( "url" );
                            obj.name = o.getString ( "brand" );
                            obj.type = o.getString ( "type" );

                            obj.brief = o.getString ( "khm" );
                            items.add ( obj );
                        }
                        Log.i ( "ITEM_PRD", products_array.toString () );
                        Log.i ( "ITEM_PRD", "------------------------------" );
                        Log.i ( "ITEM_PRD", items.toString () );
                        mAdapter = new AdapterGridTwoLine ( GridTwoLine.this, items );
                        recyclerView.setAdapter ( mAdapter );

                        // on item list clicked
                        mAdapter.setOnItemClickListener ( new AdapterGridTwoLine.OnItemClickListener () {
                            @Override
                            public void onItemClick(View view, Image obj, int position) {
                                //Snackbar.make(parent_view, obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
                                getSubType ( obj.name, obj.type );
                                //showRadioButtonDialog ();

                                Log.i ( "objj", obj.name );
                                Log.i ( "objj", obj.type );

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
                Log.i ( "TAGG", "error" );
            }
        } ) {

        };
        queue.add ( jsonobj );
    }

    private void getSubType(final String brand, String type) {

        final String URL_DATA = getString ( R.string.ip_map ) + "items/" + brand + "/" + type;

        RequestQueue queue = Volley.newRequestQueue ( this );
        JsonObjectRequest jsonobj = new JsonObjectRequest ( Request.Method.GET, URL_DATA, null, new Response.Listener<JSONObject> () {
            @Override
            public void onResponse(JSONObject response) {

                if (response.has ( "brands" )) {
                    str.clear ();
                    try {
                        Log.i ( "CCA", response.toString () );
                        JSONArray products_array = response.getJSONArray ( "brands" );
                        for (int i = 0; i < products_array.length (); i++) {
                            JSONObject o = products_array.getJSONObject ( i );
                            str.add ( o.getString ( "subtype" ) );
                        }

                        showSingleChoiceDialog (brand);


                        //RINGTONE = str.toArray(new String[str.size()]);
                        Log.i ( "CCA", str.toString () );


                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
                }
            }
        }, new Response.ErrorListener () {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i ( "TAXX", "error" );
            }
        } ) {

        };
        queue.add ( jsonobj );
    }

    private void showSingleChoiceDialog(final String brand) {

        myArray = str.toArray ( new String[str.size ()] );
        int size = myArray.length;

//        List<String> stringList = new ArrayList<> ();  // here is list
//        for (int i = 0; i < 5; i++) {
//            stringList.add ( "RadioButton " + (i + 1) );
//        }
        single_choice_selected = myArray[0];

        //single_choice_selected = str[0];
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setTitle ( "Choose Sub Type" );

        builder.setSingleChoiceItems ( myArray, 0, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                single_choice_selected = myArray[i];
            }
        } );
        builder.setPositiveButton ( R.string.OK, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i ( "showSingleChoiceDialog", brand );
                Log.i ( "showSingleChoiceDialog", single_choice_selected );

                Intent intent = new Intent ( GridTwoLine.this, ListMultiSelection.class );
                sharedPreferences = getSharedPreferences("Mysubdata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("brand", brand);
                editor.putString("subtype", single_choice_selected);
                editor.commit();
                startActivity ( intent );
                finish();
                //Snackbar.make ( parent_view, "selected : " + single_choice_selected, Snackbar.LENGTH_SHORT ).show ();
            }
        } );
        builder.setNegativeButton ( R.string.CANCEL, null );
        if (size == 1 ){
            Intent intent = new Intent ( GridTwoLine.this, ListMultiSelection.class );
            sharedPreferences = getSharedPreferences("Mysubdata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("brand", brand);
            editor.putString("subtype", "");
            editor.commit();
            startActivity ( intent );
        }else {
            builder.show ();
        }
    }

    private void showRadioButtonDialog() {

        // custom dialog
        final Dialog dialog = new Dialog ( this );
        dialog.requestWindowFeature ( Window.FEATURE_NO_TITLE );
        dialog.setContentView ( R.layout.radiobutton_dialog );
        dialog.setCancelable ( true );
        List<String> stringList = new ArrayList<> ();  // here is list
        for (int i = 0; i < 5; i++) {
            stringList.add ( "RadioButton " + (i + 1) );
        }
        RadioGroup rg = (RadioGroup) dialog.findViewById ( R.id.radio_group );

        for (int i = 0; i < stringList.size (); i++) {
            RadioButton rb = new RadioButton ( this ); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText ( stringList.get ( i ) );
            rg.addView ( rb );
        }

        dialog.show ();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater ().inflate ( R.menu.menu_search_setting, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId () == android.R.id.home) {
            finish ();
        } else {
            //Toast.makeText ( getApplicationContext (), item.getTitle (), Toast.LENGTH_SHORT ).show ();
        }
        return super.onOptionsItemSelected ( item );
    }

    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow ().setStatusBarColor ( ContextCompat.getColor ( getApplicationContext (), resourseColor ) );
        }

        ActionBar bar = getSupportActionBar ();

        bar.setBackgroundDrawable ( new ColorDrawable ( getResources ().getColor ( resourseColor ) ) );

    }
}
