package msmartds.in.utility;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

public class Mysingleton
{
  private static Context mCtx;
  private static Mysingleton mInstance;
  private RequestQueue requestQueue;
  
  private Mysingleton(Context paramContext)
  {
    mCtx = paramContext;
    this.requestQueue = getRequestQueue();
  }
  
  public static Mysingleton getInstance(Context paramContext)
  {
    try
    {
      if (mInstance == null) {
        mInstance = new Mysingleton(paramContext);
      }
      Mysingleton localMysingleton = mInstance;
      return localMysingleton;
    }
    finally {}
  }
  
  public <T> void addToRequsetque(Request<T> paramRequest)
  {
    this.requestQueue.add(paramRequest);
  }

  public RequestQueue getRequestQueue()
  {
    if (this.requestQueue == null) {
      this.requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new HurlStack(null, new HttpsCertificate(mCtx).newSslSocketFactory()));
      //this.requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
    }
    return this.requestQueue;
  }
}