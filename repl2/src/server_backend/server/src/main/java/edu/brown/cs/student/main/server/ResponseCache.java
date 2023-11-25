//package edu.brown.cs.student.main.server;
//
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import edu.brown.cs.student.main.server.broadband.BroadbandHandler;
//import java.util.concurrent.ExecutionException;
//
//public class ResponseCache<K> {
//
//  private LoadingCache<K, String> cache;
//  private BroadbandHandler handler;
//
//  public void makeCache(Integer cacheSize, BroadbandHandler handler) {;
//    this.handler = handler;
//    CacheLoader<K, String> loader = new CacheLoader<>() {
//      @Override
//      public String load(K key) throws Exception {
//        System.out.println("Called load for:" + key);
//        return retrieveAPIValue(key);
//      }
//    };
//    cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(loader);
//
//  }
//
//  public String retrieveAPIValue(K target) throws ExecutionException {
//    String percent = handler.getBroadbandPercent();
//    cache.put(target, percent);
//    return percent;
//  }
//
//  public LoadingCache<K, String> getCache() {
//    return cache;
//  }
//
//}

