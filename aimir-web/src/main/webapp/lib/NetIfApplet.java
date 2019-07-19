import java.applet.Applet;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class NetIfApplet extends Applet
{
  private static final long serialVersionUID = -1215057496798278554L;
  public static String sep = ":";
  public static String format = "%02X";
  private String macAddress = null;
  private String macAddresses = null;

  public static void main(String[] paramArrayOfString) throws SocketException {
    NetIfApplet localNetIfApplet = new NetIfApplet();
    localNetIfApplet.getMacAddress();
  }

  public void start() {
    getNicInfo();
  }

  public void getNicInfo() {
    try {
      InetAddress localInetAddress = InetAddress.getLocalHost();

      NetworkInterface localNetworkInterface = NetworkInterface.getByInetAddress(localInetAddress);
      byte[] arrayOfByte = localNetworkInterface.getHardwareAddress();
      String str = "";

      for (int i = 0; i < arrayOfByte.length; i++) {
        str = new StringBuilder().append(str).append(String.format("%02X%s", new Object[] { Byte.valueOf(arrayOfByte[i]), i < arrayOfByte.length - 1 ? sep : "" })).toString();
      }

      this.macAddress = str;
      this.macAddresses = getMacList();

      System.out.println(new StringBuilder().append("IP-Address: ").append(localInetAddress.toString()).toString());
      System.out.println(new StringBuilder().append("IP-HostName: ").append(localInetAddress.getHostName()).toString());
      System.out.println(new StringBuilder().append("IP-HostAddress: ").append(localInetAddress.getHostAddress()).toString());
      System.out.println(new StringBuilder().append("Mac-Address: ").append(this.macAddress).toString());
      System.out.println(new StringBuilder().append("Mac-Addresses: ").append(this.macAddresses).toString());
    }
    catch (SocketException localSocketException) {
      localSocketException.printStackTrace();
    } catch (UnknownHostException localUnknownHostException) {
      localUnknownHostException.printStackTrace();
    }
  }

  public static String getMacList()
  {
    try
    {
      String[] arrayOfString1 = getMacAddresses();

      String str1 = "";
      StringBuffer localStringBuffer = new StringBuffer();
      for (String str2 : arrayOfString1) {
        if (!str2.equals(""))
        {
          localStringBuffer.append(str1).append(str2);
          str1 = ",";
        }
      }
      return localStringBuffer.toString();
    } catch (Exception localException) {
      System.err.println(new StringBuilder().append("Exception:: ").append(localException.getMessage()).toString());
      localException.printStackTrace();
    }

    return "";
  }

  public String getMacAddressList() {
    return this.macAddresses;
  }

  public static String[] getMacAddresses()
  {
    try
    {
      Enumeration localEnumeration = NetworkInterface.getNetworkInterfaces();

      ArrayList localArrayList = new ArrayList();
      while (localEnumeration.hasMoreElements()) {
        String str = macToString((NetworkInterface)localEnumeration.nextElement());

        if (str != null) {
          localArrayList.add(str);
        }
      }
      return (String[])localArrayList.toArray(new String[0]);
    } catch (SocketException localSocketException) {
      System.err.println(new StringBuilder().append("SocketException:: ").append(localSocketException.getMessage()).toString());
      localSocketException.printStackTrace();
    } catch (Exception localException) {
      System.err.println(new StringBuilder().append("Exception:: ").append(localException.getMessage()).toString());
      localException.printStackTrace();
    }

    return new String[0];
  }

  public static String macToString(NetworkInterface paramNetworkInterface)
    throws SocketException
  {
    return macToString(paramNetworkInterface, sep, format);
  }

  public static String macToString(NetworkInterface paramNetworkInterface, String paramString1, String paramString2)
    throws SocketException
  {
    byte[] arrayOfByte1 = paramNetworkInterface.getHardwareAddress();

    if (arrayOfByte1 != null) {
      StringBuffer localStringBuffer = new StringBuffer("");
      String str = "";
      for (byte b : arrayOfByte1) {
        localStringBuffer.append(str).append(String.format(paramString2, new Object[] { Byte.valueOf(b) }));
        str = paramString1;
      }
      return localStringBuffer.toString();
    }

    return null;
  }

  public String getMacAddress() {
    return this.macAddress;
  }
}