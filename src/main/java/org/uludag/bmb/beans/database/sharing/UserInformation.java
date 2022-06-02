package org.uludag.bmb.beans.database.sharing;

public class UserInformation {
    private String email;
    private String publicKey;


    public UserInformation() {
    }

    public UserInformation(String email, String publicKey) {
        this.email = email;
        this.publicKey = publicKey;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
