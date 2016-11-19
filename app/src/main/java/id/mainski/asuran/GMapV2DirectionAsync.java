//package id.mainski.asuran;
//
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//import org.w3c.dom.Document;
//
//import java.io.InputStream;
//import java.util.ArrayList;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//
///**
// * Created by muhammadabduh on 11/19/16.
// */
//
//public class GMapV2DirectionAsync extends AsyncTask<String, Void, Document> {
//
//    private final static String TAG = GMapV2DirectionAsync.class.getSimpleName();
//    private Handler handler;
//    private LatLng start, end;
//    private String mode;
//
//    Marker tempMarker = null;
//    GMapV2Direction md;
//
//    public GMapV2DirectionAsync(Handler handler, LatLng start, LatLng end, String mode) {
//        this.start = start;
//        this.end = end;
//        this.mode = mode;
//        this.handler = handler;
//
//        this.md = new GMapV2Direction();
//    }
//
//    @Override
//    protected Document doInBackground(String... params) {
//
//        String url = "http://maps.googleapis.com/maps/api/directions/xml?"
//                + "origin=" + start.latitude + "," + start.longitude
//                + "&destination=" + end.latitude + "," + end.longitude
//                + "&sensor=false&units=metric&mode=" + mode;
//        Log.d("url", url);
//        try {
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpContext localContext = new BasicHttpContext();
//            HttpPost httpPost = new HttpPost(url);
//            HttpResponse response = httpClient.execute(httpPost, localContext);
//            InputStream in = response.getEntity().getContent();
//            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//                    .newDocumentBuilder();
//            Document doc = builder.parse(in);
//
//            ArrayList<LatLng> directionPoint = md.getDirection(doc);
//
//            for (int i = 0; i < directionPoint.size(); i++) {
//                if(tempMarker != null){
//                    tempMarker.remove();
//                    tempMarker = startMarker = mMap.addMarker(new MarkerOptions().position(point).title("Snippet").draggable(true));
//                    mMap.addCircle(new CircleOptions()
//                            .center(point)
//                            .radius(100)
//                            .strokeWidth(1)
//                            .strokeColor(Color.RED)
//                            .fillColor(Color.argb(128, 123, 0, 0))
//                            .clickable(true));
//                }
//
//            }
//            return doc;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    @Override
//    protected void onPostExecute(Document result) {
//        if (result != null) {
//            Log.d(TAG, "---- GMapV2DirectionAsyncTask OK ----");
//            Message message = new Message();
//            message.obj = result;
//            handler.dispatchMessage(message);
//        } else {
//            Log.d(TAG, "---- GMapV2DirectionAsyncTask ERROR ----");
//        }
//    }
//
//    @Override
//    protected void onPreExecute() {
//    }
//
//    @Override
//    protected void onProgressUpdate(Void... values) {
//    }
//}