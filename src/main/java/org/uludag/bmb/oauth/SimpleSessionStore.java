package org.uludag.bmb.oauth;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSessionStore {
    public static String extractQueryParam(String query, String param) {
        Map<String, List<String>> params = toParamsMap(query);

        if (!params.containsKey(param)) {
            return null;
        }

        List<String> values = params.get(param);
        if (values.size() > 1) {
            return null;
        }

        return values.get(0);
    }

    protected static Map<String, List<String>> toParamsMap(String query) {
        try {
            String [] pairs = query.split("&");
            Map<String, List<String>> params = new HashMap<String, List<String>>(pairs.length);

            for (String pair : pairs) {
                String [] keyValue = pair.split("=", 2);
                String key = keyValue[0];
                String value = keyValue.length == 2 ? keyValue[1] : "";

                List<String> others = params.get(key);
                if (others == null) {
                    others = new ArrayList<String>();
                    params.put(key, others);
                }

                others.add(URLDecoder.decode(value, "UTF-8"));
            }

            return params;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Map<String, String[]> params(String ... pairs) {
        if ((pairs.length % 2) != 0) {
            System.out.println("pairs must be a multiple of 2.");
        }

        Map<String, String[]> query = new HashMap<String, String[]>();
        for (int i = 0; i < pairs.length; i += 2) {
            query.put(pairs[i], new String [] { pairs[i + 1] });
        }
        return query;
    }
}