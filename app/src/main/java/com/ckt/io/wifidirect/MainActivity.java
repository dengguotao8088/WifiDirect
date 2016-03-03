package com.ckt.io.wifidirect;

import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ckt.io.wifidirect.dialog.DeviceConnectDialog;
import com.ckt.io.wifidirect.fragment.ContentFragment;
import com.ckt.io.wifidirect.fragment.DeviceChooseFragment;
import com.ckt.io.wifidirect.fragment.FileExplorerFragment;
import com.ckt.io.wifidirect.p2p.WifiP2pHelper;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private DeviceConnectDialog deviceConnectDialog;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    protected Toolbar toolbar;

    private ContentFragment contentfragment;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pHelper wifiP2pHelper;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
                case WifiP2pHelper.WIFIP2P_DEVICE_LIST_CHANGED:
                    deviceConnectDialog.updateDeviceList(wifiP2pHelper.getDeviceList());
                    break;
                case WifiP2pHelper.WIFIP2P_DEVICE_CONNECTED_SUCCESS:
                    deviceConnectDialog.updateConnectedInfo(wifiP2pHelper.isServer());
                    break;
                case 0:
                    break;
                case 1:
                    break;
            }
        };
    };
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        wifiP2pHelper = new WifiP2pHelper(this, this.handler);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        contentfragment = (ContentFragment) getSupportFragmentManager().findFragmentById(R.id.id_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        // 實作 drawer toggle 並放入 toolbar
        toolbar = (Toolbar)findViewById(R.id.id_toolbar_layout);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        deviceConnectDialog = new DeviceConnectDialog(this, R.style.FullHeightDialog);
    }



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiP2pHelper, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiP2pHelper);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wifiP2pHelper.release();
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
        /*Log.d(WifiP2pHelper.TAG, "试图发送文件");
        ArrayList<File> fileList = new ArrayList<File>();
        fileList.add(new File("/storage/emulated/0/1.apk"));
        fileList.add(new File("/storage/emulated/0/2.zip"));
        wifiP2pHelper.sendFiles(fileList);*/
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FileExplorerFragment fileExplorerFragment = contentfragment.getFileExplorerFragment();
        if(fileExplorerFragment != null && fileExplorerFragment.back()) {
            return ;
        }
        super.onBackPressed();
    }

    public WifiP2pHelper getWifiP2pHelper() {
        return wifiP2pHelper;
    }
    public Handler getHandler(){
        return this.handler;
    }
    public DeviceConnectDialog getDeviceConnectDialog() {
        return this.deviceConnectDialog;
    }
}