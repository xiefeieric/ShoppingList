package uk.me.feixie.shoppinglist.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.shoppinglist.R;
import uk.me.feixie.shoppinglist.model.Item;
import uk.me.feixie.shoppinglist.utils.Constants;
import uk.me.feixie.shoppinglist.utils.DividerItemDecoration;
import uk.me.feixie.shoppinglist.utils.NumberHelper;
import uk.me.feixie.shoppinglist.utils.UIUtils;

public class AddEditActivity extends AppCompatActivity {

    private Dialog mDialog;
    private int id;
    private TextInputLayout tiName;
    private TextView tvTotalPrice;
    private TextInputLayout tiQuantity;
    private TextInputLayout barcode;
    private Spinner spCategory;
    private RecyclerView rvAddEdit;
    private  List<Item> mItemList;
    private MyRVAdapter mAdapter;
    private static final int SPEECH_REQUEST_CODE = 0;
    private int categoryId;
    private double totalPrice;
    private  Dialog editDialog;
    private  TextInputLayout tiEditName;
    private  TextInputLayout tiEditQuantity;
    private  TextInputLayout tiEditPrice;
    private  TextInputLayout tiEditBarcode;
    private  Spinner spEditCategory;
    private  ImageView ivBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        mItemList = new ArrayList<>();
        initToolbar();
        initView();
        registerReceiver(mBroadcastReceiver, new IntentFilter("uk.me.feixie.addIntent"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        totalPrice = +totalPrice;
        tvTotalPrice.setText("Total Price: "+ totalPrice);
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
        rvAddEdit.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        mAdapter = new MyRVAdapter();
        rvAddEdit.setAdapter(mAdapter);

        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);

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
            tiQuantity = (TextInputLayout) mDialog.findViewById(R.id.tiQuantity);
//            tiPrice = (TextInputLayout) mDialog.findViewById(R.id.tiPrice);

            checkItemName();
            if (tiName.getEditText() != null) {

                ArrayAdapter autoAdapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Constants.ITEMS);
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
            SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Constants.CATEGORY);
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

//        if (id==R.id.action_barcode) {
//            scanQR();
//        }

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
        if (!TextUtils.isEmpty(name)) {
            mItem.setName(name);
            categoryId = spCategory.getSelectedItemPosition();
            mItem.setCategory(String.valueOf(categoryId));
            String quantity = tiQuantity.getEditText().getText().toString();
            if (!TextUtils.isEmpty(quantity)) {
                mItem.setQuantity(quantity);
            }
            Intent intent = new Intent("uk.me.feixie.addIntent");
            intent.putExtra("item", mItem);
            sendBroadcast(intent);
            mDialog.dismiss();
        } else {
            UIUtils.showToast(this,"Item name can not be empty!");
        }

    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void scanQR() {
        IntentIntegrator integrator = new IntentIntegrator(this);
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a BarCode");
//        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
//        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
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
                item.setCategory("0");
                mItemList.add(item);
                mAdapter.notifyItemInserted(0);
                rvAddEdit.scrollToPosition(0);
            }
        }

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            // handle scan result
            String contents = scanResult.getContents();
//            System.out.println(contents);
            if (!TextUtils.isEmpty(contents)) {
                tiEditBarcode.getEditText().setText(contents);
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
            View itemView = View.inflate(parent.getContext(), R.layout.item_addedit_rv, null);
            MyViewHolder myViewHolder = new MyViewHolder(itemView);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Item item = mItemList.get(mItemList.size() - 1 - position);
            String name = item.getName();
            String quantity = item.getQuantity();
            if (TextUtils.isEmpty(quantity)) {
                holder.tvName.setText(name);
            } else {
                holder.tvName.setText(name + " (" + quantity + ")");
            }
            String price = item.getPrice();
            if (!TextUtils.isEmpty(price)) {
                holder.tvPrice.setText(price);
                holder.tvPrice.setVisibility(View.VISIBLE);

            } else {
                holder.tvPrice.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(quantity) && !TextUtils.isEmpty(price)) {
                totalPrice = totalPrice+(Double.parseDouble(price)*Double.parseDouble(quantity));
            }
//            System.out.println("TempPrice: "+ totalPrice);
            tvTotalPrice.setText("Total Price: "+ NumberHelper.round(totalPrice,2));
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tvName, tvPrice;
        public boolean mMStrike = false;


        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);

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

            tvName.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            editDialog = new AppCompatDialog(v.getContext());
            editDialog.setContentView(R.layout.item_edit_dialog);

            tiEditName = (TextInputLayout) editDialog.findViewById(R.id.tiEditName);
            position = getAdapterPosition();
            Item item = mItemList.get(mItemList.size() - 1 - getAdapterPosition());
            tiEditName.getEditText().setText(item.getName());
            tiEditName.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (TextUtils.isEmpty(tiEditName.getEditText().getText().toString())) {
                        tiEditName.setError("Item name can not be empty");
                        tiEditName.setErrorEnabled(true);
                    } else {
                        tiEditName.setErrorEnabled(false);
                    }
                }
            });

            tiEditQuantity = (TextInputLayout) editDialog.findViewById(R.id.tiEditQuantity);
            tiEditQuantity.getEditText().setText(item.getQuantity());

            tiEditPrice = (TextInputLayout) editDialog.findViewById(R.id.tiEditPrice);
            tiEditPrice.getEditText().setText(item.getPrice());

            tiEditBarcode = (TextInputLayout) editDialog.findViewById(R.id.tiEditBarcode);
            tiEditBarcode.getEditText().setText(item.getBarcode());
            ivBarcode = (ImageView) editDialog.findViewById(R.id.ivBarcode);
            ivBarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scanQR();
                }
            });

            spEditCategory = (Spinner) editDialog.findViewById(R.id.spEditCategory);
            SpinnerAdapter adapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_dropdown_item_1line, Constants.CATEGORY);
            spEditCategory.setAdapter(adapter);
            spEditCategory.setSelection(Integer.parseInt(item.getCategory()));

            ((ViewGroup) editDialog.getWindow().getDecorView())
                    .getChildAt(0).startAnimation(AnimationUtils.loadAnimation(
                    tiEditName.getContext(), android.R.anim.slide_in_left));
            editDialog.show();
            return true;
        }
    }

    private int position;

    public void dialogEditCancel(View view) {
        editDialog.dismiss();
    }

    public void dialogEditSave(View view) {

        String editName = tiEditName.getEditText().getText().toString();
        if (!TextUtils.isEmpty(editName)) {
            mItemList.get(mItemList.size() - 1 - position).setName(editName);

            String editQuantity = tiEditQuantity.getEditText().getText().toString();
            if (!TextUtils.isEmpty(editQuantity)) {
                mItemList.get(mItemList.size() - 1 - position).setQuantity(editQuantity);
            }

            String editPrice = tiEditPrice.getEditText().getText().toString();
            if (!TextUtils.isEmpty(editPrice)) {
                mItemList.get(mItemList.size() - 1 - position).setPrice(editPrice);
            }

            String barcode = tiEditBarcode.getEditText().getText().toString();
            if (!TextUtils.isEmpty(barcode)){
                mItemList.get(mItemList.size() - 1 - position).setBarcode(barcode);
            }

            categoryId = spEditCategory.getSelectedItemPosition();
            mItemList.get(mItemList.size() - 1 - position).setCategory(String.valueOf(spEditCategory.getSelectedItemPosition()));
            spEditCategory.setSelection(Integer.parseInt(mItemList.get(mItemList.size() - 1 - position).getCategory()));

            mAdapter.notifyItemChanged(position);

            editDialog.dismiss();
        } else {
            UIUtils.showToast(this,"Item name can not be empty!");
        }

    }

    public void dialogEditDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete the item?");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                UIUtils.showToast(AddEditActivity.this, "Delete: " + position);
//                System.out.println(mItemList.get(mItemList.size() - 1 - position).toString() + "----" + position);
                Item item = mItemList.get(mItemList.size() - 1 - position);
//                System.out.println("Delete: "+Double.parseDouble(item.getPrice())*Integer.parseInt(item.getQuantity()));
                if (!TextUtils.isEmpty(item.getQuantity()) && !TextUtils.isEmpty(item.getPrice())) {
                    totalPrice = totalPrice-(Double.parseDouble(item.getPrice())*Integer.parseInt(item.getQuantity()));
                    tvTotalPrice.setText("Total Price: "+ NumberHelper.round(totalPrice,2));
                }

                mItemList.remove(mItemList.size() - 1 - position);
                mAdapter.notifyItemRemoved(position);

                editDialog.dismiss();
            }
        });
        builder.show();
    }

}
