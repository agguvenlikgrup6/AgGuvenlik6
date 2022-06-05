package org.uludag.bmb.beans.dataproperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CustomRecievedFileListView {
    private final StringProperty senderEmail;
    private final StringProperty decryptedName;
    private final StringProperty encryptedName;


    public CustomRecievedFileListView(String senderEmail, String decryptedName, String encryptedName) {
        this.senderEmail = new SimpleStringProperty(this, "senderEmail", senderEmail);
        this.decryptedName = new SimpleStringProperty(this, "decryptedName", decryptedName);
        this.encryptedName = new SimpleStringProperty(this, "encryptedName", encryptedName);
    }

    public final StringProperty senderEmail() {
        return senderEmail;
    }

    public final String getSenderEmail() {
        return senderEmail.get();
    }

    public final void setSenderEmail(String senderEmail) {
        this.senderEmail.set(senderEmail);
    }

    public final StringProperty decryptedName() {
        return decryptedName;
    }

    public final String getDecryptedName() {
        return decryptedName.get();
    }

    public final void setDecryptedName(String decryptedName) {
        this.decryptedName.set(decryptedName);
    }

    public final StringProperty encryptedName() {
        return encryptedName;
    }

    public final String getEncryptedName() {
        return encryptedName.get();
    }

    public final void setEncryptedName(String encryptedName) {
        this.encryptedName.set(encryptedName);
    }
    
    
}
