package com.innomalist.taxi.rider.events;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.innomalist.taxi.common.events.BaseResultEvent;
import com.innomalist.taxi.common.models.Address;
import com.innomalist.taxi.common.models.CRUD;
import com.innomalist.taxi.common.utils.LatLngDeserializer;
import com.innomalist.taxi.common.utils.ServerResponse;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class CRUDAddressResultEvent extends BaseResultEvent {
    public CRUD crud;
    public List<Address> addresses;
    public CRUDAddressResultEvent() {
        super(ServerResponse.REQUEST_TIMEOUT);
    }
    public CRUDAddressResultEvent(int code, CRUD crud) {
        super(code);
        this.crud = crud;
    }
    public CRUDAddressResultEvent(int code, CRUD crud, JSONArray addresses) {
        super(code);
        this.crud = crud;
        Type type = new TypeToken<List<Address>>() {}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LatLng.class, new LatLngDeserializer());

        Gson customGson = gsonBuilder.create();
        this.addresses = customGson.fromJson(addresses.toString(),type);
    }
}
