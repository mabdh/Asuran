package id.mainski.asuran;

import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by muhammadabduh on 11/19/16.
 */

public class InsuranceSpotsUpdate extends AsyncTask<Void, Void, Void> {

    private GoogleMap mMap;
    private InsuranceSpotsModel insModel;


    public InsuranceSpotsUpdate(GoogleMap googleMap, InsuranceSpotsModel im) {
        this.mMap = googleMap;
        this.insModel = im;
    }


    @Override
    protected Void doInBackground(Void... voids) {

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mMap != null) {
            switch (insModel.type){
                case LIFE:
                    MainActivity.lifeMarkerHash.get(insModel.id).setPosition(insModel.getMarkerPosition());
                    MainActivity.lifeCircleHash.get(insModel.id).setRadius(insModel.getCircleRadius());
                    break;
                case CAR:
                    MainActivity.carMarkerHash.get(insModel.id).setPosition(insModel.getMarkerPosition());
                    MainActivity.carCircleHash.get(insModel.id).setRadius(insModel.getCircleRadius());
                    break;
                case HOME:
                    MainActivity.homeMarkerHash.get(insModel.id).setPosition(insModel.getMarkerPosition());
                    MainActivity.homeCircleHash.get(insModel.id).setRadius(insModel.getCircleRadius());
                    break;
            }
            Log.i(String.valueOf(Log.DEBUG), "MARKER UPDATED");
        } else {
            Log.e(String.valueOf(Log.DEBUG), "ERROR UPDATING THE MARKER");
        }

        super.onPostExecute(aVoid);
    }

}
