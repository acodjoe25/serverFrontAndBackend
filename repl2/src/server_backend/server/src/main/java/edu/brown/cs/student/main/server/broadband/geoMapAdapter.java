package edu.brown.cs.student.main.server.broadband;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import java.io.IOException;

public class geoMapAdapter {

  private final Moshi moshi = new Moshi.Builder().build();
  private final JsonAdapter<geoMap> adapter = moshi.adapter(geoMap.class);

  @ToJson
  public String toJson(geoMap map) {
    return "";
  }

  @FromJson
  public geoMap fromJson(String map) throws IOException {
    return adapter.fromJson(map);
  }


}
