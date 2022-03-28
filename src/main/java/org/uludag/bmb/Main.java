package org.uludag.bmb;

import java.net.URISyntaxException;
import java.io.File;
import java.io.IOException;

import org.uludag.bmb.Company.FileSystem;
import org.uludag.bmb.Company.FileSystemRSA;
import org.uludag.bmb.Company.TryRSA;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try{
            //FileSystem tt = new FileSystem();
            //tt.dosyaSifreleme("deneme.txt", "aa.txt");
            //tt.dosyaSifreCozme("aa.txt","dd.txt");
            // FileSystemRSA tt_rsa = new FileSystemRSA();
            // //tt_rsa.dosyaSifreleme("deneme.txt", "aa.txt");
            // tt_rsa.dosyaSifreCozme("aa.txt", "bb.txt");
            TryRSA aab = new TryRSA();
            aab.initJustPri("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIS734sRjlbkHa4+Vus6zib/bJCav157+Z73hm5aKWBdoD146v39MUgmdziqFa56q7KbJSQa7uvF+iQV9w50GEvg5wvUgYZ25+KS7cUjqK3/H7cCBp6cjaRdOJzORh0k5LQ3f8iKJNQgH7sc6tNjXGn4NlgxexjR6RuNaJCrt+7lAgMBAAECgYAMPsDKtZ3qCjVqw7mFDfHCy0GavYv2DX3j2nX+bDbw+vIzeZpEQD1xqIrLIXXKmOqKGaH9iLaN7b+74ILgRHmRNHUgIehnu2neOXLGpybpsUidkNdH5kXRGD78XUXqfDaXpebSgeoMJ5RuWUIPzOrzemSDvinFnY3SHoH9hn62QQJBALmqMva/xn05jBTMTYxyjTe/Q3A+PrZ/McBH2RLYMX90Amgdim4vHheIggL22cfHHk5gZ1RX4ozFpTuLxwWc6j0CQQC3BGwsPHju275t1DshGHPuJicfbGBXSoP4ewchNTFASCMDBWlaAn7V1xPwL7eY7Rigb6MCrFMtBwWtEAUpRWnJAkBcQA/wgFssT1Kl5tlFRomaQGNOuu7IGKzsoAZgkaEOdeLYPo1QsAKgqMgIlwSgefQ59zaANuavEWlHC+2IgW31AkBn1iOFiZ3Xb2eAqrwNj/EeehkSVAvpGsb7cNnftm1GGmd67FQUlHvf2ZJfYMNbCZJCXHRxLNJYJrbKtN8oxlA5AkBsqm/A1q4QRMN54HCGGUKvRGj2F9J85d4MAVTVvzW5T8EzKYW25/FgOymOg/Qd6mXjRG7QQNsWRki2JbPcpRhF");
            String denc = aab.decrypt("Wt6sbUPj4sF/sAZDkLJ1gaGgmp0YllgnkGAWn3IL+eesvCxE025+Pf89TRySaemnjf272nB1cAOoQwsoNGF+vLQdhkFRU9B/EcSQvxRpQV0Dj9WX3v+GAdpuNCzeYLXG1cn+K+mBLORIxV6aWvzp92Y5B9L6rRrKYIPSgwdgNfc=");
            System.out.println(denc);
        }
        catch(Exception ignored){
            
        }
    }
}
