package uk.me.feixie.shoppinglist.activity;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.shoppinglist.R;
import uk.me.feixie.shoppinglist.db.DBHelper;
import uk.me.feixie.shoppinglist.model.Item;
import uk.me.feixie.shoppinglist.model.ShopList;
import uk.me.feixie.shoppinglist.utils.Constants;
import uk.me.feixie.shoppinglist.utils.DividerItemDecoration;
import uk.me.feixie.shoppinglist.utils.NumberHelper;
import uk.me.feixie.shoppinglist.utils.UIUtils;

public class AddEditActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int ITEM_BOUGHT = 1;
    private static final int ITEM_NOT_BOUGHT = 2;

    private Dialog mDialog;
    private TextInputLayout tiName;
    private TextView tvTotalPrice;
    private TextInputLayout tiQuantity;
    private Spinner spCategory;
    private RecyclerView rvAddEdit;
    private List<Item> mItemList;
    private MyRVAdapter mAdapter;
    private int categoryId;
    private double totalPrice;
    private Dialog editDialog;
    private TextInputLayout tiEditName;
    private TextInputLayout tiEditQuantity;
    private TextInputLayout tiEditPrice;
    private TextInputLayout tiEditBarcode;
    private Spinner spEditCategory;
    private ImageView ivBarcode;
    private boolean mMStrike;
    private ShopList mShopList;
    private DBHelper mDbHelper;
    private int bought;

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
//        totalPrice = +totalPrice;
        tvTotalPrice.setText("Total Price: " + totalPrice);
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
        mShopList = (ShopList) getIntent().getSerializableExtra("shop_list");
        mDbHelper = new DBHelper(AddEditActivity.this);

        rvAddEdit = (RecyclerView) findViewById(R.id.rvAddEdit);
        rvAddEdit.setLayoutManager(new LinearLayoutManager(this));
        rvAddEdit.setHasFixedSize(true);
        rvAddEdit.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));
        mAdapter = new MyRVAdapter();
        rvAddEdit.setAdapter(mAdapter);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);

        if (mShopList.getId()<1) {
            new Thread(){
                @Override
                public void run() {
                    int id = mDbHelper.queryListId(mShopList);
                    mShopList.setId(id);
                    mItemList = mDbHelper.queryAllItems(mShopList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }.start();
        } else {
            showList();
            totalPrice = Double.parseDouble(mShopList.getMoney());
        }
    }

    private void showList() {
        new Thread(){
            @Override
            public void run() {
                mItemList = mDbHelper.queryAllItems(mShopList);
                for (Item item:mItemList) {
                    if (item.getBuyStatus()==ITEM_BOUGHT) {
                        bought++;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateShopList();
    }

    private void updateShopList() {
        mShopList.setMoney(String.valueOf(NumberHelper.round(totalPrice,2)));
        int size = mItemList.size();
        mShopList.setItemBought("("+bought+"/"+size+")");

        new Thread(){
            @Override
            public void run() {
                mDbHelper.updateList(mShopList);
            }
        }.start();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            updateShopList();
            finish();
        }

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
            mItem.setSlId(mShopList.getId());
            categoryId = spCategory.getSelectedItemPosition();
            mItem.setCategory(String.valueOf(categoryId));
            String quantity = tiQuantity.getEditText().getText().toString();
            if (!TextUtils.isEmpty(quantity)) {
                mItem.setQuantity(quantity);
            }
            mItem.setBuyStatus(ITEM_NOT_BOUGHT);
            Intent intent = new Intent("uk.me.feixie.addIntent");
            intent.putExtra("item", mItem);
            sendBroadcast(intent);

            mDialog.dismiss();

        } else {
            UIUtils.showToast(this, "Item name can not be empty!");
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
                final Item item = new Item();
                item.setName(spokenText);
                item.setCategory("0");
                item.setSlId(mShopList.getId());
                item.setBuyStatus(ITEM_NOT_BOUGHT);
                mItemList.add(item);
                mAdapter.notifyItemInserted(0);
                rvAddEdit.scrollToPosition(0);
                new Thread(){
                    @Override
                    public void run() {
                        mDbHelper.addItem(item);
                        mItemList = mDbHelper.queryAllItems(mShopList);
                    }
                }.start();
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
                final Item item = (Item) intent.getSerializableExtra("item");
                mItemList.add(item);
                mAdapter.notifyItemInserted(0);
                rvAddEdit.scrollToPosition(0);

                new Thread(){
                    @Override
                    public void run() {
                        mDbHelper.addItem(item);
                    }
                }.start();

                totalPrice = 0.0;
                for (Item eachItem:mItemList) {
                    if (!TextUtils.isEmpty(eachItem.getQuantity()) && !TextUtils.isEmpty(eachItem.getPrice())) {
                        totalPrice = totalPrice + Double.parseDouble(eachItem.getQuantity())*Double.parseDouble(eachItem.getPrice());
                    }
                }

                tvTotalPrice.setText("Total Price: "+totalPrice);
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

//            if (!TextUtils.isEmpty(quantity) && !TextUtils.isEmpty(price)) {
//                totalPrice = totalPrice + (Double.parseDouble(price) * Double.parseDouble(quantity));
//            }

            if (item.getBuyStatus() == ITEM_BOUGHT) {
                holder.tvName.getPaint().setStrikeThruText(true);
                holder.tvName.setTextColor(getResources().getColor(android.R.color.darker_gray));
                holder.tvName.invalidate();
            } else if (item.getBuyStatus() == ITEM_NOT_BOUGHT) {
                holder.tvName.getPaint().setStrikeThruText(false);
                holder.tvName.setTextColor(getResources().getColor(android.R.color.black));
                holder.tvName.invalidate();
            }

//            System.out.println("TempPrice: "+ totalPrice);
//            tvTotalPrice.setText("Total Price: " + NumberHelper.round(totalPrice, 2));
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public TextView tvName, tvPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    System.out.println("onClick");
                    Item item = mItemList.get(mItemList.size() - 1 - getAdapterPosition());
//                    UIUtils.showToast(v.getContext(), "Clicked: "+getAdapterPosition());

                    if (item.getBuyStatus() == ITEM_BOUGHT) {
                        mMStrike = false;
                        tvName.getPaint().setStrikeThruText(mMStrike);
                        tvName.setTextColor(v.getResources().getColor(android.R.color.black));
                        item.setBuyStatus(ITEM_NOT_BOUGHT);
                        tvName.invalidate();
                        mDbHelper.updateItem(item);
                        bought--;
                    } else if (item.getBuyStatus() == ITEM_NOT_BOUGHT) {
                        mMStrike = true;
                        tvName.getPaint().setStrikeThruText(mMStrike);
                        tvName.setTextColor(v.getResources().getColor(android.R.color.darker_gray));
                        tvName.invalidate();
                        item.setBuyStatus(ITEM_BOUGHT);
                        mDbHelper.updateItem(item);
                        bought++;
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
            //clicked position
            position = getAdapterPosition();
            //query itemlist from database
            mItemList = mDbHelper.queryAllItems(mShopList);
            mAdapter.notifyDataSetChanged();
            //clicked in reverse order
            Item item = mItemList.get(mItemList.size()-1-position);
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
            if (tiEditQuantity.getEditText()!=null) {
                tiEditQuantity.getEditText().setText(item.getQuantity());
            }

            tiEditPrice = (TextInputLayout) editDialog.findViewById(R.id.tiEditPrice);
            if (tiEditPrice.getEditText()!=null) {
                tiEditPrice.getEditText().setText(item.getPrice());
            }

            tiEditBarcode = (TextInputLayout) editDialog.findViewById(R.id.tiEditBarcode);
            if (tiEditBarcode.getEditText()!=null) {
                tiEditBarcode.getEditText().setText(item.getBarcode());
            }

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

        final Item item = mItemList.get(mItemList.size() - 1 - position);

        String editName = tiEditName.getEditText().getText().toString();
        if (!TextUtils.isEmpty(editName)) {
            item.setName(editName);

            String editQuantity = tiEditQuantity.getEditText().getText().toString();
            if (!TextUtils.isEmpty(editQuantity)) {
                item.setQuantity(editQuantity);
            }

            String editPrice = tiEditPrice.getEditText().getText().toString();
            if (!TextUtils.isEmpty(editPrice)) {
                item.setPrice(editPrice);
            }

            String barcode = tiEditBarcode.getEditText().getText().toString();
            if (!TextUtils.isEmpty(barcode)) {
                item.setBarcode(barcode);
            }

            categoryId = spEditCategory.getSelectedItemPosition();
            item.setCategory(String.valueOf(spEditCategory.getSelectedItemPosition()));
            spEditCategory.setSelection(Integer.parseInt(item.getCategory()));

            mAdapter.notifyItemChanged(position);

            new Thread(){
                @Override
                public void run() {
                    mDbHelper.updateItem(item);
                }
            }.start();

            totalPrice = 0.0;
            for (Item eachItem:mItemList) {
                if (!TextUtils.isEmpty(eachItem.getQuantity()) && !TextUtils.isEmpty(eachItem.getPrice())) {
                    totalPrice = totalPrice + Double.parseDouble(eachItem.getQuantity())*Double.parseDouble(eachItem.getPrice());
                }
            }

            tvTotalPrice.setText("Total Price: " + totalPrice);

            editDialog.dismiss();

        } else {
            UIUtils.showToast(this, "Item name can not be empty!");
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
                final Item item = mItemList.get(mItemList.size() - 1 - position);
//                System.out.println("Delete: "+Double.parseDouble(item.getPrice())*Integer.parseInt(item.getQuantity()));
                if (!TextUtils.isEmpty(item.getQuantity()) && !TextUtils.isEmpty(item.getPrice())) {
                    totalPrice = totalPrice - (Double.parseDouble(item.getPrice()) * Integer.parseInt(item.getQuantity()));
                    tvTotalPrice.setText("Total Price: " + NumberHelper.round(totalPrice, 2));
                }

                mItemList.remove(item);
                mAdapter.notifyItemRemoved(position);

                new Thread(){
                    @Override
                    public void run() {
                        mDbHelper.deleteItem(item);
                    }
                }.start();

                editDialog.dismiss();
            }
        });
        builder.show();
    }

}
