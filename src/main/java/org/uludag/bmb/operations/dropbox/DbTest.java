package org.uludag.bmb.operations.dropbox;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DbTest {
   public static void main(String[] args) throws DbxApiException, DbxException {
      Injector injector = Guice.createInjector(new DbModule());
      DbClient client = injector.getInstance(DbClient.class);

      System.out.println(client.hashCode());
      DbClient client2 = injector.getInstance(DbClient.class);
      System.out.println(client2.hashCode());
      
   }
}
