package de.lukeslog.localimageserver.localimageserver;

import android.os.Environment;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 22.07.14.
 */
public class Utils {

    public static String getIPv4Address()
    {
        return getIPAddress(true);
    }

    public static String getIPv6Adress()
    {
        return getIPAddress(false);
    }

    private static String getIPAddress(boolean useIPv4)
    {

        String addresses = "";
        try
        {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces)
            {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs)
                {
                    if (!addr.isLoopbackAddress())
                    {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4)
                        {
                            if (isIPv4)
                                addresses += sAddr + ", ";
                        }
                        else
                        {
                            if (!isIPv4)
                            {
                                // drop ip6 port suffix
                                int delim = sAddr.indexOf('%');
                                if(delim<0) addresses += sAddr + ", ";
                                else addresses += sAddr.substring(0, delim) + ", ";
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
        }
        if(addresses == null || addresses.length() <= 3) return "";
        return addresses.subSequence(0, addresses.length()-2).toString();
    }

    /**
     * Get the list of folders up to a certain depth in the folder structure as a String of absolute paths
     *
     * @param depth the depth up to which th algorthm is supposed to go
     * @return
     */
    public static ArrayList<String> getListOfLocalFolderPathsToDepth(int depth)
    {
        File filesystem = new File(Environment.getExternalStorageDirectory().getPath());
        ArrayList<String> localFolderList = new ArrayList<String>();
        createFolderList(filesystem, depth, localFolderList);
        return localFolderList;
    }

    private static void createFolderList(File f, int depth, ArrayList<String> localFolderList)
    {
        depth--;
        if(f.isDirectory() && depth>0)
        {
            File[] filelist = f.listFiles();
            String fn = f.getAbsolutePath();
            localFolderList.add(fn);
            for(int j=0; j<filelist.length; j++)
            {
                if(filelist[j].isDirectory())
                {
                    createFolderList(filelist[j], depth, localFolderList);
                }
            }
        }
    }
}
