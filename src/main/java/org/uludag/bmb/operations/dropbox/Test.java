package org.uludag.bmb.operations.dropbox;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Test {
   public static void main(String[] args) throws DbxApiException, DbxException {
      Injector injector = Guice.createInjector(new Module());
      Client client = injector.getInstance(Client.class);

      System.out.println(client.hashCode());
      Client client2 = injector.getInstance(Client.class);
      System.out.println(client2.hashCode());
      
   }
}
