package rawbot.authlib;

import java.lang.Thread;
import java.net.InetAddress;

public class DNSResolver extends Thread {

  public static boolean resolverUp(String host) {
    DNSResolver r = new DNSResolver(host);
    r.start();
    try {
      r.join(1500);
    } catch (java.lang.InterruptedException e) {}
    return r.get() != null;
  }

    private String domain;
    private InetAddress inetAddr;

    public DNSResolver(String domain) {
        this.domain = domain;
        this.inetAddr = null;
    }

    public void run() {
      try {
        InetAddress addr = InetAddress.getByName(domain);
        set(addr);
      } catch (java.net.UnknownHostException e) {}
    }

    public synchronized void set(InetAddress inetAddr) {
        this.inetAddr = inetAddr;
    }

    public synchronized InetAddress get() {
        return inetAddr;
    }
}
