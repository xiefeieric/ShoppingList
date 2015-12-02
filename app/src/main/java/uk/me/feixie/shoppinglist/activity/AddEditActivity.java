package uk.me.feixie.shoppinglist.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import uk.me.feixie.shoppinglist.R;
import uk.me.feixie.shoppinglist.utils.UIUtils;

public class AddEditActivity extends AppCompatActivity {

    private Dialog mDialog;
    private int id;
    private TextInputLayout tiName;
    private TextInputLayout tiCategory;
    private TextInputLayout tiPrice;
    private TextInputLayout barcode;
    private Spinner spCategory;
    private String[] mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        initToolbar();
        initView();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_edit);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("");
        //find and show custom back home button
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_done_white_24dp);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_text) {
            mDialog = new AppCompatDialog(this);
            mDialog.setContentView(R.layout.item_alert_dialog);
            tiName = (TextInputLayout) mDialog.findViewById(R.id.tiName);
            tiPrice = (TextInputLayout) mDialog.findViewById(R.id.tiPrice);
            spCategory = (Spinner) mDialog.findViewById(R.id.spCategory);
            mCategory = new String[]{"Default","Baby", "Baking", "Beverages","Bread/Bakery","Canned Goods","Cereal/Breakfast","Condiments",
                    "Dairy","Deli","Frozen Foods","Fruits/Vegetables","Meats","Miscellaneous","Non-Food Items","Pasta/Rice","Snacks","Spices"};
            SpinnerAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, mCategory);
            spCategory.setAdapter(adapter);
            checkItemName();
            if (tiName.getEditText()!=null) {
                tiName.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        checkItemName();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        checkItemName();
                    }
                });
            }

            ((ViewGroup) mDialog.getWindow().getDecorView())
                    .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                    this, android.R.anim.slide_in_left));

            mDialog.show();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

//        if (mToggle.onOptionsItemSelected(item)) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void checkItemName() {
        if (TextUtils.isEmpty(tiName.getEditText().getText().toString())) {
            tiName.setError("Item name can not be empty");
            tiName.setErrorEnabled(true);
        } else {
            tiName.setErrorEnabled(false);
        }
    }

    public void dialogCancel(View view) {
        mDialog.dismiss();
    }

    public void dialogOk(View view) {
        UIUtils.showToast(this,"ok");
        mDialog.dismiss();
    }

}
