package com.ckt.io.wifidirect.p2p;

import com.ckt.io.wifidirect.Constants;
import com.ckt.io.wifidirect.utils.LogUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by admin on 2016/8/8.
 */

public class WifiP2pServer extends Thread {

    private static final String TAG = "WifiP2pServer";

    private ServerSocket mServerSocket;
    private WifiTransferManager transferManager;

    public WifiP2pServer(WifiTransferManager manager) {
        transferManager = manager;
    }

    public void startListen() {
        this.start();
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                if (mServerSocket == null) {
                    mServerSocket = new ServerSocket(Constants.PORT);
                }
                Socket client = mServerSocket.accept();
                transferManager.receive(client);
            } catch (IOException e) {
                if (mServerSocket != null) {
                    try {
                        mServerSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                LogUtils.e(TAG, "server error");
                e.printStackTrace();
            }
        }
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
                mServerSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
