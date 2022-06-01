package org.uludag.bmb.beans.database.sharing;

public class UserInformation {
    private String eMail;
    private String publicKey;


    public UserInformation(String eMail, String publicKey) {
        this.eMail = eMail;
        this.publicKey = publicKey;
    }

    public String getEMail() {
        return this.eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
