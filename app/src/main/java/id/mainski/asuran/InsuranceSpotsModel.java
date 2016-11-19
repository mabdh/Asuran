package id.mainski.asuran;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by muhammadabduh on 11/19/16.
 */

public class InsuranceSpotsModel {
    InsuranceType type;
    String id;
    LatLng markModel;
    Double circModel;

    public InsuranceSpotsModel(LatLng m, Double c, InsuranceType type, String id) {
        this.markModel = m;
        this.circModel = c;
        this.type = type;
        this.id = id;
    }

    public LatLng getMarkerPosition(){
        return this.markModel;
    }

    public Double getCircleRadius(){
        return this.circModel;
    }

    public InsuranceType getInsuranceType(){
        return this.type;
    }

    public String getId(){
        return this.id;
    }


    public void setInsuranceModel(LatLng m, Double c, InsuranceType type, String id){
        this.markModel = m;
        this.circModel = c;
        this.type = type;
        this.id = id;
    }
}
