package it.polito.mobile.androidassignment2.businessLogic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Joseph on 05/05/2015.
 */
class OfferManager {


    private static final String BASE_URI = "offers";

    protected static Offer getOfferById(int id) throws RestApiException, IOException {
        Offer s = null;

        String resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+id,null);

        try {
            return new Offer(new JSONObject(resp).getJSONObject("offer"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error OfferManager");
        }

    }


    /**
     *
     * @param newOffer a Offer object representing the student to be inserted.
     * @return a Offer object representing the inserted object.
     * @throws Exception Network related exceptions can be thrown
     */
    protected static Offer insertNewOffer(Offer newOffer) throws RestApiException, IOException {

        if( newOffer == null ) throw new RestApiException(422,"insertNewOffer : input offer is null");

        if( newOffer.getCompanyId() == null ||
                newOffer.getKindOfContract() == null ||
                newOffer.getDescriptionOfWork() == null ||
                newOffer.getDurationMonths() == null ){
            throw new RestApiException(422, "insertNewOffer : Some field are null in the offer");
        }

        String resp = RESTManager.send(RESTManager.POST, BASE_URI,newOffer.toFormParams());

        try {
            return new Offer(new JSONObject(resp).getJSONObject("offer"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error OfferManager in insertNewOffer");
        }

    }


    protected static List<Offer> getOfferMatchingCriteria( Offer criteria ) throws RestApiException, IOException {

        Map<String,String> params = null;
        Integer companyId = null;

        if( criteria != null ){

            if( criteria.getCompanyId() != null ){
                companyId = criteria.getCompanyId();

                if( criteria.getKindOfContract() == null  &&
                        criteria.getDescriptionOfWork() == null &&
                        criteria.getDurationMonths() == null)
                    params = null;
                else{
                    params = criteria.toFormParams();
                }
            }
        }

        String resp;

        if( companyId != null){
            resp = RESTManager.send(RESTManager.GET, "companies/" + companyId.toString() , params);
        }else{
            resp = RESTManager.send(RESTManager.GET, BASE_URI, params);
        }

        Log.v("OfferManager", "resp = " + resp);


        try{

            JSONArray offersJson = (new JSONObject(resp)).getJSONArray("offers");

            List<Offer> offers = new ArrayList<Offer>();



            for( int i = 0; i < offersJson.length(); i++){

                JSONObject offerJson = offersJson.getJSONObject(i);

                offers.add( new Offer(offerJson) );
            }

            return offers;


        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error OfferManager in getOfferMatchingCriteria");
        }

    }



    protected static Offer updateOffer( Offer offerToUpdate ) throws IOException, RestApiException {

        if( offerToUpdate == null ) throw new RestApiException(422,"updateOffer : input offer is null");

        if( offerToUpdate.getId() == null ){
            throw new RestApiException(422, "updateOffer : id is not set in the imputed offer");
        }

        if( offerToUpdate.getCompanyName() != null ) {
            throw new RestApiException(422, "updateOffer : cannot update company name.");
        }

        String resp = RESTManager.send(RESTManager.PUT, BASE_URI+"/"+offerToUpdate.getId(),
                offerToUpdate.toFormParams());

        try {
            return new Offer(new JSONObject(resp).getJSONObject("offer"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error OfferManager in updateOffer");
        }

    }


    //to test
    protected static Integer deleteOffer( int id ) throws IOException, RestApiException {

        RESTManager.send(RESTManager.DELETE, BASE_URI+"/"+id,null);

        return 0;

    }

}
