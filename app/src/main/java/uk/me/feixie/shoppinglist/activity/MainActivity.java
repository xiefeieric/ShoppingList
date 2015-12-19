package uk.me.feixie.shoppinglist.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import uk.me.feixie.shoppinglist.R;
import uk.me.feixie.shoppinglist.db.DBHelper;
import uk.me.feixie.shoppinglist.model.ShopList;
import uk.me.feixie.shoppinglist.model.User;
import uk.me.feixie.shoppinglist.utils.DateUtil;
import uk.me.feixie.shoppinglist.utils.DividerItemDecoration;
import uk.me.feixie.shoppinglist.utils.UIUtils;

public class MainActivity extends AppCompatActivity {

    private static final int LIST_SHOW = 0;
    private static final int LIST_NOT_SHOW = 1;
    public static final int SHOW_GROUP = 2;
    public static final int SHOW_GROUP_NOT = 3;

    private DrawerLayout dlMain;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private RecyclerView rvMain;
    private TextInputLayout mTiShopListTitle;
    private MyRecycleViewAdapter mAdapter;
    private DBHelper mDbHelper;
    private List<ShopList> mShopLists;
    private ListView leftDrawer;
    private List<User> userList;
    private MyListAdapter mMyListAdapter;
    private int leftDrawerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initViews();
        initFABtn();
//        initLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mLm.removeUpdates(mListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Thread() {
            @Override
            public void run() {

                if (leftDrawerPosition == 0) {
                    mShopLists = mDbHelper.queryList();
                    userList = mDbHelper.queryAllUser();
                    sortList(mShopLists);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            rvMain.scrollToPosition(0);
                            mMyListAdapter.notifyDataSetChanged();
                        }
                    });

                } else {
                    User user = userList.get(leftDrawerPosition);
                    mShopLists = mDbHelper.queryUserList(user.getId());
                    sortList(mShopLists);
                    userList = mDbHelper.queryAllUser();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            rvMain.scrollToPosition(0);
                            mMyListAdapter.notifyDataSetChanged();
                        }
                    });

                }
            }
        }.start();

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initViews() {
        mDbHelper = new DBHelper(this);
        //init drawer layout
        dlMain = (DrawerLayout) findViewById(R.id.dlMain);
        //init actionbar toggle button
        mToggle = new ActionBarDrawerToggle(this, dlMain, mToolbar, R.string.DrawerOpen, R.string.DrawerClose);
        //set drawer layout listener for toggle button
        dlMain.setDrawerListener(mToggle);
        //link toggle btton with drawer layout
        mToggle.syncState();

        rvMain = (RecyclerView) findViewById(R.id.rvMain);
        rvMain.setHasFixedSize(true);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        rvMain.addItemDecoration(new DividerItemDecoration(this));
        mAdapter = new MyRecycleViewAdapter();
        rvMain.setAdapter(mAdapter);

        new Thread() {
            @Override
            public void run() {
                mShopLists = mDbHelper.queryList();
                userList = mDbHelper.queryAllUser();
                sortList(mShopLists);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        mMyListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();

//        for (int i = 0; i < Constants.CATEGORY.length-1; i++) {
//            userList.add(Constants.CATEGORY[i]);
//        }
        leftDrawer = (ListView) findViewById(R.id.left_drawer);
        mMyListAdapter = new MyListAdapter();
        leftDrawer.setAdapter(mMyListAdapter);
        leftDrawer.setItemChecked(0, true);
        leftDrawer.setDivider(getResources().getDrawable(android.R.drawable.divider_horizontal_bright));
        leftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                leftDrawerPosition = position;

                if (position == 0) {
                    mShopLists = mDbHelper.queryList();
                    sortList(mShopLists);
                    mAdapter.notifyDataSetChanged();

                } else {
                    final User user = userList.get(position);
                    mShopLists = mDbHelper.queryUserList(user.getId());
                    sortList(mShopLists);
                    mAdapter.notifyDataSetChanged();

                    if (!TextUtils.isEmpty(user.getNotice())) {
                        Snackbar snackbar = Snackbar.make(view, user.getNotice(), Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("More", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(user.getNotice());
                                builder.setPositiveButton("Ok", null);
                                builder.show();
                            }
                        });
                        snackbar.show();
                    }

                }

                dlMain.closeDrawer(leftDrawer);
            }
        });

        //long click to delete item from list

        leftDrawer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                if (position!=0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Warning");
                    builder.setMessage("Are you sure you want to delete?");
                    builder.setNegativeButton("Cancel",null);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User user = userList.get(position);
                            List<ShopList> shopLists = mDbHelper.queryUserList(user.getId());
                            if (shopLists.size()==0) {
                                //                            System.out.println(user.toString());
                                user.setShow(SHOW_GROUP_NOT);
//                            System.out.println(user.toString());
                                mDbHelper.updateUser(user);
                                userList = mDbHelper.queryAllUser();
//                            System.out.println(userList.size());
                                mMyListAdapter.notifyDataSetChanged();
                            } else {
                                UIUtils.showToast(MainActivity.this,"Please change or delete related shopping list first!");
                            }

                        }
                    });
                    builder.show();
                }
                return true;
            }
        });

    }

    private void initFABtn() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.lblue));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                final AppCompatDialog dialog = new AppCompatDialog(MainActivity.this);
                dialog.setContentView(R.layout.item_dialog_main);

                mTiShopListTitle = (TextInputLayout) dialog.findViewById(R.id.tiShopListTitle);

                checkName();
                if (mTiShopListTitle.getEditText() != null) {
                    mTiShopListTitle.getEditText().addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            checkName();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }

                Button btnMainCancel = (Button) dialog.findViewById(R.id.btnMainCancel);
                btnMainCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btnMainOk = (Button) dialog.findViewById(R.id.btnMainOk);
                btnMainOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = mTiShopListTitle.getEditText().getText().toString();
                        if (!TextUtils.isEmpty(title)) {
                            final ShopList shopList = new ShopList();
                            shopList.setTitle(title);
                            //set uid based on selected left drawer item
//                            System.out.println("size:"+userList.size());
//                            System.out.println("position:"+leftDrawerPosition);
//                            System.out.println("userid:"+userList.get(leftDrawerPosition).getId());
                            shopList.setUid(userList.get(leftDrawerPosition).getId());
                            //0=show,1=hide
                            shopList.setShow(LIST_SHOW);
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String listDate = formatter.format(new Date());
                            shopList.setListDate(listDate);

                            new Thread() {
                                @Override
                                public void run() {
                                    DBHelper dbHelper = new DBHelper(MainActivity.this);
                                    dbHelper.addList(shopList);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }.start();

                            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                            intent.putExtra("shop_list", shopList);
                            startActivity(intent);
                            dialog.dismiss();
                        } else {
                            UIUtils.showToast(MainActivity.this, "Title can not be empty!");
                        }
                    }
                });

                dialog.show();
//                startActivity(new Intent(MainActivity.this, AddEditActivity.class));
            }
        });
    }

    private void checkName() {
        if (TextUtils.isEmpty(mTiShopListTitle.getEditText().getText().toString())) {
            mTiShopListTitle.setErrorEnabled(true);
            mTiShopListTitle.setError("Title can not be empty!");
        } else {
            mTiShopListTitle.setErrorEnabled(false);
        }
    }

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
        if (id == R.id.action_total_cost) {
            double total = 0;
            for (ShopList shopList : mShopLists) {
                if (!TextUtils.isEmpty(shopList.getMoney()))
                    total = total + Double.parseDouble(shopList.getMoney());
            }

            Snackbar.make(rvMain, "Total cost is: " + total, Snackbar.LENGTH_LONG).show();

            return true;
        }

        if (id == R.id.action_add_group) {
            AlertDialog.Builder addUserDialog = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.add_user_group_dialog, null);
            addUserDialog.setView(view);
            final TextInputLayout tiAddUserGroup = (TextInputLayout) view.findViewById(R.id.tiAddUserGroup);
            final TextInputLayout tiAddUserNotice = (TextInputLayout) view.findViewById(R.id.tiAddUserNotice);
            if (TextUtils.isEmpty(tiAddUserGroup.getEditText().getText().toString())) {
                tiAddUserGroup.setErrorEnabled(true);
                tiAddUserGroup.setError("Name can not be empty!");
            } else {
                tiAddUserGroup.setErrorEnabled(false);
            }
            tiAddUserGroup.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(tiAddUserGroup.getEditText().getText().toString())) {
                        tiAddUserGroup.setErrorEnabled(true);
                        tiAddUserGroup.setError("Name can not be empty!");
                    } else {
                        tiAddUserGroup.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            addUserDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(tiAddUserGroup.getEditText().getText().toString())) {
                        final User user = new User();
                        user.setName(tiAddUserGroup.getEditText().getText().toString());
                        user.setNotice(tiAddUserNotice.getEditText().getText().toString());
                        user.setShow(SHOW_GROUP);
                        userList.add(user);
                        mMyListAdapter.notifyDataSetChanged();

                        new Thread() {
                            @Override
                            public void run() {
                                mDbHelper.addUser(user);
                                userList = mDbHelper.queryAllUser();
                            }
                        }.start();

                    } else {
                        UIUtils.showToast(MainActivity.this, "Name can not be empty!");
                    }
                }
            });

            addUserDialog.setNegativeButton("Cancel", null);
            addUserDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sortList(List<ShopList> shopList) {
        Collections.sort(shopList, new Comparator<ShopList>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(ShopList lhs, ShopList rhs) {
                Date date1 = DateUtil.stringToDate(lhs.getListDate());
                Date date2 = DateUtil.stringToDate(rhs.getListDate());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.getTime() < date2.getTime()) return 1;
                else if (date1.getTime() > date2.getTime()) return -1;
                else return 0;
            }

        });
    }


    private int pressed;

    @Override
    public void onBackPressed() {
        if (pressed % 2 == 0) {
            UIUtils.showToast(this, "One more click to quite the app");
        } else {
            super.onBackPressed();
        }
        pressed++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        pressed = 0;
    }


    class MyRecycleViewAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_main_rv, null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ShopList shopList = mShopLists.get(position);
            holder.tvTitle.setText(shopList.getTitle());
            holder.tvDate.setText(shopList.getListDate());
            holder.tvMoney.setText(shopList.getMoney());
            holder.tvItemBought.setText(shopList.getItemBought());
        }

        @Override
        public int getItemCount() {
            if (mShopLists != null) {
                return mShopLists.size();
            } else {
                return 0;
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDate;
        public TextView tvTitle;
        public TextView tvMoney;
        public TextView tvItemBought;
        public LinearLayout llRVMain;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvMoney = (TextView) itemView.findViewById(R.id.tvMoney);
            tvItemBought = (TextView) itemView.findViewById(R.id.tvItemBought);
            llRVMain = (LinearLayout) itemView.findViewById(R.id.llRVMain);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llRVMain.setLayoutParams(params);
            llRVMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    UIUtils.showToast(MainActivity.this, "CLICKED: "+getAdapterPosition());
                    ShopList shopList = mShopLists.get(getAdapterPosition());
                    Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                    intent.putExtra("shop_list", shopList);
                    startActivity(intent);
                }
            });

            llRVMain.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setItems(new String[]{"Delete", "View on Map", "Change Group"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ShopList shopList = mShopLists.get(getAdapterPosition());
                            switch (which) {
                                case 0:
                                    shopList.setShow(LIST_NOT_SHOW);
                                    mShopLists.remove(shopList);
                                    mAdapter.notifyItemRemoved(getAdapterPosition());
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            mDbHelper.updateList(shopList);
                                        }
                                    }.start();
                                    break;
                                case 1:
//                                    System.out.println(shopList.getLatitude()+"/"+shopList.getLongitude());
                                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                    intent.putExtra("latitude", shopList.getLatitude());
                                    intent.putExtra("longitude", shopList.getLongitude());
                                    startActivity(intent);
                                    break;

                                case 2:
                                    AlertDialog.Builder dialogChangeGroup = new AlertDialog.Builder(MainActivity.this);
                                    View view = View.inflate(MainActivity.this, R.layout.dialog_change_user_group, null);
                                    dialogChangeGroup.setView(view);
                                    final Spinner spinChangeGroup = (Spinner) view.findViewById(R.id.spinChangeGroup);
                                    spinChangeGroup.setAdapter(mMyListAdapter);
//                                    System.out.println("uid:"+shopList.getUid()+"id:"+shopList.getId());
                                    spinChangeGroup.setSelection(shopList.getUid() - 1);
                                    dialogChangeGroup.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            User user = (User) spinChangeGroup.getSelectedItem();
                                            shopList.setUid(user.getId());
                                            mDbHelper.updateList(shopList);

                                        }
                                    });
                                    dialogChangeGroup.setNegativeButton("Cancel", null);
                                    dialogChangeGroup.show();

                                    break;
                            }
                        }
                    });
                    builder.show();


                    return true;
                }
            });
        }
    }


    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (userList != null) {
                return userList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(MainActivity.this, R.layout.item_list_main, null);
                holder.tvUser = (TextView) convertView.findViewById(R.id.tvUser);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvUser.setText(userList.get(position).getName());


            return convertView;
        }
    }

    public class ViewHolder {
        public TextView tvUser;
    }
}
