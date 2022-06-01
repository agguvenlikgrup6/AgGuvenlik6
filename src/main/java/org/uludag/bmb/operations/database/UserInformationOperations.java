package org.uludag.bmb.operations.database;

import java.util.List;

import org.uludag.bmb.beans.database.sharing.UserInformation;
import org.uludag.bmb.factory.query.QueryFactory;

public class UserInformationOperations extends DatabaseOperations {
    public void insert(String email, String publicKey) {
        executeCloudQuery(QueryFactory.UserInformation("insert"), email, email, publicKey);
    }

    public List<UserInformation> getAll() {
        return executeCloudQuery(QueryFactory.UserInformation("getAll"));
    }

    public UserInformation getByEmail(String eMail){
        List<UserInformation> userInformations = executeCloudQuery(QueryFactory.UserInformation("getByEmail"), eMail);
        if(userInformations.size() != 0){
            return userInformations.get(0);
        }else {
            return null;
        }
    }
}
