import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args){

        int num = 0;

        SSLContext context = null;
        KeyManagerFactory kmf = null;
        TrustManagerFactory tmf = null;
        KeyStore ksKeys = null;
        KeyStore ksTrust = null;

        SSLServerSocketFactory factory = null;
        SSLServerSocket ss = null;
        Socket s = null;

        ExecutorService service = Executors.newFixedThreadPool(3);
        Runnable task;


        try {

            kmf = KeyManagerFactory.getInstance("SunX509");
            tmf = TrustManagerFactory.getInstance("SunX509");

            char [] password = {'1','2','3','4','5','6'};
            ksKeys = KeyStore.getInstance("JKS");
            ksKeys.load(new FileInputStream("ServerKeyStore.jks"),password);
            kmf.init(ksKeys,password);

            ksTrust = KeyStore.getInstance("JKS");
            ksTrust.load(new FileInputStream("ServerTrustStore.jks"),password);
            tmf.init(ksTrust);

            context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(),tmf.getTrustManagers(),null);

            factory = context.getServerSocketFactory();
            ss = (SSLServerSocket) factory.createServerSocket(2500);

            ss.setEnabledCipherSuites(ss.getSupportedCipherSuites());

            while (true){
                s = ss.accept();
                num++ ;
                begin(s,num,service);
            }


        } catch (IOException | NoSuchAlgorithmException | KeyStoreException
                | CertificateException | UnrecoverableKeyException
                | KeyManagementException e) {
            e.printStackTrace();
        }

    }


    public static void begin(Socket s, int num, ExecutorService service){

        Runnable task = new Runnable() {

            @Override
            public void run() {
                try {
                    String str = " ";
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    while (!((str = br.readLine()).equals("quit"))){
                        System.out.println("Client " + num  + ": " + str);
                    }


                    Thread.sleep(10000);
                    br.close();
                    s.close();

                }catch (IOException | InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

         service.submit(task);
    }
}