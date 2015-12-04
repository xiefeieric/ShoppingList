package uk.me.feixie.shoppinglist.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.shoppinglist.R;
import uk.me.feixie.shoppinglist.model.Item;
import uk.me.feixie.shoppinglist.utils.Constants;
import uk.me.feixie.shoppinglist.utils.DividerItemDecoration;
import uk.me.feixie.shoppinglist.utils.UIUtils;

public class AddEditActivity extends AppCompatActivity {

    private Dialog mDialog;
    private int id;
    private TextInputLayout tiName;
    private TextInputLayout tiPrice;
    private TextInputLayout barcode;
    private Spinner spCategory;
    private RecyclerView rvAddEdit;
    private List<Item> mItemList;
    private MyRVAdapter mAdapter;
    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        mItemList = new ArrayList<>();
        initToolbar();
        initView();
        registerReceiver(mBroadcastReceiver,new IntentFilter("uk.me.feixie.addIntent"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
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
        rvAddEdit = (RecyclerView) findViewById(R.id.rvAddEdit);
        rvAddEdit.setLayoutManager(new LinearLayoutManager(this));
        rvAddEdit.setHasFixedSize(true);
        rvAddEdit.addItemDecoration(new DividerItemDecoration(this,R.drawable.divider));
        mAdapter = new MyRVAdapter();
        rvAddEdit.setAdapter(mAdapter);
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

//            tiPrice = (TextInputLayout) mDialog.findViewById(R.id.tiPrice);

            checkItemName();
            if (tiName.getEditText()!=null) {

                ArrayAdapter autoAdapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, Constants.ITEMS);
                AutoCompleteTextView text = (AutoCompleteTextView) tiName.getEditText();
                text.setAdapter(autoAdapter);

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

            spCategory = (Spinner) mDialog.findViewById(R.id.spCategory);
            SpinnerAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line, Constants.Category);
            spCategory.setAdapter(adapter);



            ((ViewGroup) mDialog.getWindow().getDecorView())
                    .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                    this, android.R.anim.slide_in_left));

            mDialog.show();
            return true;
        }

        if (id == R.id.action_voice) {
            try {
                displaySpeechRecognizer();
            } catch (ActivityNotFoundException e) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://market.android.com/details?id=com.google.android.googlequicksearchbox"));
                startActivity(browserIntent);
            }
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
        Item mItem = new Item();
        String name = tiName.getEditText().getText().toString();
        mItem.setName(name);
        String category = spCategory.getSelectedItem().toString();
        mItem.setCategory(category);
//        String price = tiPrice.getEditText().getText().toString();
//        if (!TextUtils.isEmpty(price)) {
//            mItem.setPrice(price);
//        }
        Intent intent = new Intent("uk.me.feixie.addIntent");
        intent.putExtra("item",mItem);
        sendBroadcast(intent);
        mDialog.dismiss();
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            if (!TextUtils.isEmpty(spokenText)) {
                Item item = new Item();
                item.setName(spokenText);
                mItemList.add(item);
                mAdapter.notifyItemInserted(0);
                rvAddEdit.scrollToPosition(0);
            }
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase("uk.me.feixie.addIntent")) {
                Item item = (Item) intent.getSerializableExtra("item");
                mItemList.add(item);
                mAdapter.notifyItemInserted(0);
                rvAddEdit.scrollToPosition(0);
            }

        }
    };


    class MyRVAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = View.inflate(parent.getContext(), R.layout.item_addedit_rv,null);
            MyViewHolder myViewHolder = new MyViewHolder(itemView);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Item item = mItemList.get(mItemList.size()-1-position);
            String name = item.getName();
            holder.tvName.setText(name);
            String price = item.getPrice();
            if (!TextUtils.isEmpty(price)) {
                holder.tvPrice.setText(price);
            }
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName,tvPrice;
        public RelativeLayout rlItemRv;
        public boolean mMStrike = false;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            rlItemRv = (RelativeLayout) itemView.findViewById(R.id.rl_item_rv);

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    UIUtils.showToast(v.getContext(), "Clicked: "+getAdapterPosition());
                    if (mMStrike) {
                        mMStrike = false;
                        tvName.getPaint().setStrikeThruText(mMStrike);
                        tvName.setTextColor(v.getResources().getColor(android.R.color.black));
                        tvName.invalidate();
                    } else {
                        mMStrike = true;
                        tvName.getPaint().setStrikeThruText(mMStrike);
                        tvName.setTextColor(v.getResources().getColor(android.R.color.darker_gray));
                        tvName.invalidate();
                    }

                }
            });
        }
    }

}
