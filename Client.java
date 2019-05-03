import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){

        SSLContext context = null;
        KeyManagerFactory kmf = null;
        TrustManagerFactory tmf = null;
        KeyStore ksKeys = null;
        KeyStore ksTrust = null;

        SSLSocketFactory socketFactory = null;
        SSLSocket s = null;
        String[] supported = null;

        PrintWriter pwrite = null;
        Scanner sc =  null;


        try {

            kmf = KeyManagerFactory.getInstance("SunX509");
            tmf = TrustManagerFactory.getInstance("SunX509");

            char [] password = {'1','2','3','4','5','6'};
            ksKeys = KeyStore.getInstance("JKS");
            ksKeys.load(new FileInputStream("ClientKeyStore.jks"),password);
            kmf.init(ksKeys,password);

            ksTrust = KeyStore.getInstance("JKS");
            ksTrust.load(new FileInputStream("ClientTrustStore.jks"),password);
            tmf.init(ksTrust);

            context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(),tmf.getTrustManagers(),null);



            socketFactory = context.getSocketFactory();
            s = (SSLSocket) socketFactory.createSocket(InetAddress.getLocalHost(),2500);
            supported = s.getSupportedCipherSuites();
            s.setEnabledCipherSuites(supported);

            s.startHandshake();
            System.out.println("HandShake Completed");

            sc = new Scanner(System.in);
            String str = " ";
            pwrite = new PrintWriter(s.getOutputStream());


            while(!((str=sc.nextLine()).equals("quit"))){
                pwrite.println(str);
                pwrite.flush();

            }

            Thread.sleep(5000);
            pwrite.close();
            s.close();



        } catch (IOException | NoSuchAlgorithmException | KeyStoreException
                | CertificateException | UnrecoverableKeyException
                | KeyManagementException | InterruptedException
                |NullPointerException  e) {
            e.printStackTrace();
        }

    }
}