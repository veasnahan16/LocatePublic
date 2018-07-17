package com.example.veasnahan.locatepublic;

import android.os.Build;
import android.os.Bundle;
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
import java.util.List;

//import com.material.components.R;

public class ListMultiSelection extends AppCompatActivity {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterListInbox mAdapter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_list_multi_selection );
        parent_view = findViewById ( R.id.lyt_parent );
ShareFunctions.showLog ();
        initToolbar ();
        initComponent ();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow ();
            window.clearFlags ( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            window.addFlags ( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.setStatusBarColor ( this.getResources ().getColor ( R.color.colorPrimaryDark ) );
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById ( R.id.toolbar );
        toolbar.setNavigationIcon ( R.drawable.ic_menu );
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

    private void initComponent() {
        Log.i ( "aa", "initComponent" );
        recyclerView = (RecyclerView) findViewById ( R.id.recyclerView );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );
        recyclerView.addItemDecoration ( new LineItemDecoration ( this, LinearLayout.VERTICAL ) );
        recyclerView.setHasFixedSize ( true );

        //List<Inbox> items = DataGenerator.getInboxData ( this );
        final String URL_DATA = getString ( R.string.ip_map ) + "items/" + "bank";
        RequestQueue queue = Volley.newRequestQueue ( this );
        JsonObjectRequest jsonobj = new JsonObjectRequest ( Request.Method.GET, URL_DATA, null, new Response.Listener<JSONObject> () {

            @Override
            public void onResponse(JSONObject response) {
                Log.i ( "aa", "success" );
                if (response.has ( "brands" )) {
                    List<Inbox> items = new ArrayList<> ();

                    try {
                        JSONArray products_array = response.getJSONArray ( "brands" );
                        for (int i = 0; i < products_array.length (); i++) {
                            Inbox objj = new Inbox ();
                            JSONObject o = products_array.getJSONObject ( i );
                            objj.from = o.getString ( "eng" );
                            objj.email = o.getString ( "khm" );
                            objj.message = o.getString ( "geo" );
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
                            }

                            @Override
                            public void onItemLongClick(View view, Inbox obj, int pos) {
                                Inbox inbox = mAdapter.getItem ( pos );
                                Log.i("inbox",inbox.toString ());
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
                Log.i ( "aa", "error" );
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
        getMenuInflater ().inflate ( R.menu.menu_search_setting, menu );
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
}