package de.lukeslog.localimageserver.localimageserver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import fi.iki.elonen.NanoHTTPD;

import static de.lukeslog.localimageserver.localimageserver.Utils.getListOfLocalFolderPathsToDepth;


/**
 * Created by lukas on 22.07.14.
 */
public class Server extends Service
{

    private WebServer server;
    File root = new File(Environment.getExternalStorageDirectory().getPath());

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("SERVER", "on create");

        try {
            SharedPreferences settings = getSharedPreferences(Constants.SETTINGS_NAME, 0);
            int foldernumber = settings.getInt("selectedLocalFolder", 0);
            ArrayList<String> localfolders = getListOfLocalFolderPathsToDepth(Constants.FOLDER_DEPTH);
            Log.d("SERVER", "start server with root "+localfolders.get(foldernumber));
            root = new File(localfolders.get(foldernumber));
            server = new WebServer(root, Constants.WEBSERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SERVER", "Web server initialized.");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("SERVER", "on Destroy");
        if (server != null)
        {
            try
            {
                server.stop();
            }
            catch(Exception e)
            {
                Log.e("SERVER", "problem shutting down the server...");
            }
        }
    }

    private class WebServer extends NanoHTTPD
    {

        public WebServer(File root, int port) throws IOException {
            super(port, root);
        }

        @Override
        public Response serve(String uri, String method, Properties header, Properties parms, Properties files)
        {
            try
            {
                Log.d("SERVER", uri);
                return this.serveFile(uri, header, new File(root.getAbsolutePath()), true);
            }
            catch (Exception e)
            {

            }
            return null;
        }
    }
}
