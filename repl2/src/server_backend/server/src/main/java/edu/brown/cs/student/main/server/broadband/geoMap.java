package edu.brown.cs.student.main.server.broadband;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class geoMap {
  public String type;
  public Geometry geometry;
  public Properties propeties;
}

