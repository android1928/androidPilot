package com.innomalist.taxi.common.location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

import java.util.List;

public class MapHelper {
    public static void centerLatLngsInMap(MapView map, List<LatLng> locations, boolean animate){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng location : locations)
            builder.include(location);
        LatLngBounds bounds = builder.build();
        int padding = 100; // in pixels
        CameraPosition cu = map.getMap().cameraPosition(
                new BoundingBox(new Point(bounds.southwest.latitude, bounds.southwest.longitude), new Point(bounds.northeast.latitude, bounds.northeast.longitude)));

        map.getMap().move(new CameraPosition(new Point(cu.getTarget().getLatitude(), cu.getTarget().getLongitude()), 14f, 0f, 0f));
    }
}
