package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class GsonUtil {

  private static Comparator<String> getComparator() {
    Comparator<String> c =
        new Comparator<String>() {
          public int compare(String o1, String o2) {
            return o1.compareTo(o2);
          }
        };

    return c;
  }

  public static void sort(JsonElement e) {
    if (e.isJsonNull()) {
      return;
    }

    if (e.isJsonPrimitive()) {
      return;
    }

    if (e.isJsonArray()) {
      JsonArray a = e.getAsJsonArray();
      for (Iterator<JsonElement> it = a.iterator(); it.hasNext(); ) {
        sort(it.next());
      }
      return;
    }

    if (e.isJsonObject()) {
      Map<String, JsonElement> tm = new TreeMap<String, JsonElement>(getComparator());
      for (Map.Entry<String, JsonElement> en : e.getAsJsonObject().entrySet()) {
        tm.put(en.getKey(), en.getValue());
      }

      for (Map.Entry<String, JsonElement> en : tm.entrySet()) {
        e.getAsJsonObject().remove(en.getKey());
        e.getAsJsonObject().add(en.getKey(), en.getValue());
        sort(en.getValue());
      }
      return;
    }
  }
}
