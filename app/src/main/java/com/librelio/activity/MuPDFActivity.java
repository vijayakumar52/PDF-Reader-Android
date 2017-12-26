package com.librelio.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.adsonik.pdfreaderwithdictionary.R;
import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.MuPDFPageAdapter;
import com.artifex.mupdf.view.DocumentReaderView;
import com.artifex.mupdf.view.ReaderView;
import com.greysonparrelli.permiso.Permiso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SuppressLint("NewApi")
public class MuPDFActivity extends Activity implements OnQueryTextListener {
    private static final String DB_NAME = "EmployeeDatabase.db";

    private SearchView mSearchView;
    private SQLiteDatabase database;
    private static final String TAG = "MuPDFActivity";
    private MuPDFCore core;
    private MuPDFCore cores[];
    private ReaderView docView;
    private MuPDFPageAdapter mDocViewAdapter;
    String filePath = null;
    TextView mSearchText;
    String paths[] = {Environment.getExternalStorageDirectory().getAbsolutePath() + "/randompdf.pdf"};
    String path = null;
    SharedPreferences myPrefs;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permiso.getInstance().setActivity(this);
        mSearchText = new TextView(this);
        mSearchText.setPadding(10, 10, 10, 10);
        final android.app.ActionBar actionBar = getActionBar();
        myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(248, 174, 16)));

        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        final Intent intent = getIntent();
        final String action = intent.getAction();
        mSearchText.setText("Search");
        cores = new MuPDFCore[paths.length];

        if (Intent.ACTION_VIEW.equals(action)) {
            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                @Override
                public void onPermissionResult(Permiso.ResultSet resultSet) {
                    if (resultSet.areAllPermissionsGranted()) {
                        Uri uri = intent.getData();
                        String path = getFilePath(uri);
                        cores[0] = openFile(path);
                        String title = intent.getData().getLastPathSegment();
                        actionBar.setTitle(title);
                        boolean pass = false;
                        String val = myPrefs.getString("key", null);
                        if (val == null) {
                            String newnumber = title;
                            String newnumber1 = path;
                            myPrefs.edit().putString("key", newnumber).commit();
                            myPrefs.edit().putString("path", newnumber1).commit();
                        } else {
                            String[] valuee = val.split(";", -1);
                            for (int i = 0; i < valuee.length; i++) {
                                if (title.equals(valuee[i])) {
                                    pass = true;
                                }
                            }
                            if (!pass) {
                                String oldnumber = myPrefs.getString("key", null);
                                String oldnumber1 = myPrefs.getString("path", null);
                                String newnumber = oldnumber + ";" + title;
                                String newnumber1 = oldnumber1 + ";" + path;
                                myPrefs.edit().putString("key", newnumber).commit();
                                myPrefs.edit().putString("path", newnumber1).commit();

                            }
                        }
                        createUI(savedInstanceState);
                    } else {
                        Toast.makeText(MuPDFActivity.this, "App may crash without this permission!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                    callback.onRationaleProvided();
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        } else {
            Permiso.getInstance().requestPermissions(new Permiso.IOnPermissionResult() {
                @Override
                public void onPermissionResult(Permiso.ResultSet resultSet) {
                    if (resultSet.areAllPermissionsGranted()) {
                        Intent inn = getIntent();
                        filePath = inn.getStringExtra("returnvalue");
                        String filena = inn.getStringExtra("name");
                        cores[0] = openFile(filePath);
                        actionBar.setTitle(filena);
                        boolean pass = false;
                        String val = myPrefs.getString("key", null);
                        if (val == null) {
                            String newnumber = filena;
                            String newnumber1 = filePath;
                            myPrefs.edit().putString("key", newnumber).commit();
                            myPrefs.edit().putString("path", newnumber1).commit();
                        } else {
                            String[] valuee = val.split(";", -1);
                            for (int i = 0; i < valuee.length; i++) {
                                if (filena.equals(valuee[i])) {
                                    pass = true;
                                }
                            }
                            if (!pass) {
                                String oldnumber = myPrefs.getString("key", null);
                                String oldnumber1 = myPrefs.getString("path", null);
                                String newnumber = oldnumber + ";" + filena;
                                String newnumber1 = oldnumber1 + ";" + filePath;
                                myPrefs.edit().putString("key", newnumber).commit();
                                myPrefs.edit().putString("path", newnumber1).commit();

                            }
                        }

                        createUI(savedInstanceState);
                    } else {
                        Toast.makeText(MuPDFActivity.this, "App may crash without this permission!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onRationaleRequested(Permiso.IOnRationaleProvided callback, String... permissions) {
                    callback.onRationaleProvided();
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

    }

    private String getFilePath(Uri uri) {
        String scheme = uri.getScheme();
        if ("file".equals(scheme)) {
            return uri.getPath();
        } else if ("content".equals(scheme)) {
            try {
                InputStream attachment = getContentResolver().openInputStream(uri);
                if (attachment == null)
                    Log.e("GMAIL ATTACHMENT", "Mail attachment failed to resolve");
                else {

                    FileOutputStream tmp = new FileOutputStream(getCacheDir().getPath() + "/temp.myfile");
                    byte[] buffer = new byte[1024];
                    while (attachment.read(buffer) > 0)
                        tmp.write(buffer);
                    tmp.close();
                    attachment.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return getCacheDir().getPath() + "/temp.myfile";
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Permiso.getInstance().setActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //android.app.ActionBar actionBar = getActionBar();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main1, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint("Find meaning");
        setupSearchView(searchItem);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            case R.id.action_navigation:

                Builder builder = new Builder(MuPDFActivity.this);
                final EditText input = new EditText(MuPDFActivity.this);
                builder
                        .setTitle("Page Navigation")
                        .setMessage("Enter the page number between 0 to " + core.countPages())
                        .setView(input)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String value = input.getText().toString();
                                if (input.getText().toString().trim().length() == 0) {
                                    Toast.makeText(MuPDFActivity.this, "Please Enter the Page Number", Toast.LENGTH_SHORT).show();
                                } else {
                                    Integer val = Integer.parseInt(value);
                                    docView.setDisplayedViewIndex(val - 1);
                                }
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            }

                        });

                builder.show();
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);


                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView(MenuItem searchItem) {
        // TODO Auto-generated method stub

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
    }


    public boolean onQueryTextChange(String newText) {
        //Toast.makeText(this, "Query = " + newText, Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        //Toast.makeText(this, "Query = " + query + " : submitted", Toast.LENGTH_SHORT).show();
        String output = fetchData(query.toUpperCase());
        Intent inten = new Intent(MuPDFActivity.this, Dictionary.class);
        inten.putExtra("word", query);
        if (output != null) {
            inten.putExtra("definition", output);
        } else {
            inten.putExtra("definition", "Oops!!! Looks like the word is not availble.");
        }
        startActivity(inten);
        return false;
    }

    public boolean onClose() {
        //Toast.makeText(this, "closed", Toast.LENGTH_SHORT).show();
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }


    private void createUI(Bundle savedInstanceState) {
        docView = new DocumentReaderView(this) {
            @Override
            protected void onMoveToChild(View view, int i) {
                super.onMoveToChild(view, i);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            protected void onContextMenuClick() {

            }

            @Override
            protected void onBuy(String path) {

            }

        };

        mDocViewAdapter = new MuPDFPageAdapter(this, cores);
        docView.setAdapter(mDocViewAdapter);
        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(docView);
        layout.setBackgroundColor(Color.BLACK);
        setContentView(layout);
        Toast.makeText(this, "Swipe Left or Right", Toast.LENGTH_LONG).show();
    }


    private MuPDFCore openFile(String path) {
        try {
            core = new MuPDFCore(path);
        } catch (Exception e) {
            Log.e(TAG, "get core failed", e);
            return null;
        }
        return core;
    }

    private String fetchData(String search) {
        String name = null;
        //Cursor friendCursor = database.query(TABLE_NAME, new String[] {FRIEND_ID, FRIEND_NAME},null, null, null, null, FRIEND_NAME);
        Cursor friendCursor = database.rawQuery("select * from emp where word='" + search + "'", null);
        friendCursor.moveToFirst();
        if (!friendCursor.isAfterLast()) {
            do {
                name = friendCursor.getString(1);
            } while (friendCursor.moveToNext());
        }
        friendCursor.close();
        return name;
    }

}
