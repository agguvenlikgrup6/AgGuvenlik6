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
            TryRSA aa = new TryRSA();
            aa.initFromString("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDG8vaK6QvS5QSHWmgBrFg5LzID2IATMr/lawwKnngjxZr8bJWsO6OOET4bP5KGzOMMKsN52vfKD8SX7a2hIxEXDxRNuobRI3s+Qj8L96VsYg4v6+rYGZ2VXmqwcSXuRURy3ZDQkMpzIQh2zHw4XTZucGyC47JYvLNow1vyB3IhZQIDAQAB","MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMby9orpC9LlBIdaaAGsWDkvMgPYgBMyv+VrDAqeeCPFmvxslaw7o44RPhs/kobM4wwqw3na98oPxJftraEjERcPFE26htEjez5CPwv3pWxiDi/r6tgZnZVearBxJe5FRHLdkNCQynMhCHbMfDhdNm5wbILjsli8s2jDW/IHciFlAgMBAAECgYAPOKyYloRTPOFv/QCYdtgDJrNpbD7po8g0edBgiZ0Ag6W8Krn4L6dE/y26G6Q5ew3jXZbkyQDiAqXUcpixC5lK1fMkBYq7GW8LGY8zjLZE4rZYU5QYoBxyK7ooqJGwdRYDYxMEOyH7hflhdr2+xnIeykjjpHBbF274l2y9E5t3CwJBAOxHvvAdRAF0Qz4z3uVW0hhkf70LHi0ZvNqxw45/UO7PQaXTwPH/iLnfFfQB4xfTDL4y9Dn2XZ6DPnpuUetigssCQQDXjZ3TXj5sNj9fp9V02mVPbcgDSP3xzdplDijYwyuShqEIcM+w/dF/qiJw9gOJ6OnQiLa7tLQ4qDqi7ARhtPaPAkEAg3/imkwPXyfPjLzdnpHVV9IG7bVUVBFw893fUl01M9ORW7MgPQ7Uj23DEAxE0SuaefhtyBx/OgsdKJJhcTlgRQJATXhGWusqQDYW/MgTPZYohy2LjzKyoi876Mn4AD/U7yqXwbLZ0mDG5L0+955Tk0M0lZAtLLU7eIlt2ZQ91uf1HQJALN3J9H4F87fKqVd6Xo5PLXiR/RZvKNJRDsDUjvAe4OfKLpf7gOaEAv9VazDTbh7x+dA/9qgccmkVCGqTPT2C2w==");
            String enc = aa.encrypt("Hello World!");
            String denc = aa.decrypt(enc);
            //System.out.println(enc);
            System.out.println(denc);
        }
        catch(Exception ignored){
            
        }
    }
}
