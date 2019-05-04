package com.innomalist.taxi.rider.activities.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.innomalist.taxi.common.activities.chargeAccount.ChargeAccountActivity;
import com.innomalist.taxi.common.activities.transactions.TransactionsActivity;
import com.innomalist.taxi.common.activities.travels.TravelsActivity;
import com.innomalist.taxi.common.events.GetStatusEvent;
import com.innomalist.taxi.common.events.GetStatusResultEvent;
import com.innomalist.taxi.common.events.NotificationPlayerId;
import com.innomalist.taxi.common.events.ProfileInfoChangedEvent;
import com.innomalist.taxi.common.location.MapHelper;
import com.innomalist.taxi.common.models.CRUD;
import com.innomalist.taxi.common.models.Driver;
import com.innomalist.taxi.common.models.DriverLocation;
import com.innomalist.taxi.common.models.Service;
import com.innomalist.taxi.common.models.Travel;
import com.innomalist.taxi.common.utils.AlertDialogBuilder;
import com.innomalist.taxi.common.utils.AlerterHelper;
import com.innomalist.taxi.common.utils.CommonUtils;
import com.innomalist.taxi.common.utils.DataBinder;
import com.innomalist.taxi.common.utils.LocationHelper;
import com.innomalist.taxi.common.utils.MyPreferenceManager;
import com.innomalist.taxi.common.utils.ServerResponse;
import com.innomalist.taxi.rider.R;
import com.innomalist.taxi.rider.activities.about.AboutActivity;
import com.innomalist.taxi.rider.activities.addresses.AddressesActivity;
import com.innomalist.taxi.rider.activities.coupon.CouponActivity;
import com.innomalist.taxi.rider.activities.looking.LookingActivity;
import com.innomalist.taxi.rider.activities.main.adapters.ServiceCategoryViewPagerAdapter;
import com.innomalist.taxi.rider.activities.main.dialogs.DriverAcceptedDialog;
import com.innomalist.taxi.rider.activities.main.fragments.ServiceCarousalFragment;
import com.innomalist.taxi.rider.activities.profile.ProfileActivity;
import com.innomalist.taxi.rider.activities.promotions.PromotionsActivity;
import com.innomalist.taxi.rider.activities.travel.TravelActivity;
import com.innomalist.taxi.rider.databinding.ActivityMainBinding;
import com.innomalist.taxi.rider.events.CRUDAddressRequestEvent;
import com.innomalist.taxi.rider.events.CRUDAddressResultEvent;
import com.innomalist.taxi.rider.events.CalculateFareRequestEvent;
import com.innomalist.taxi.rider.events.CalculateFareResultEvent;
import com.innomalist.taxi.rider.events.GetDriversLocationEvent;
import com.innomalist.taxi.rider.events.GetDriversLocationResultEvent;
import com.innomalist.taxi.rider.events.ServiceRequestErrorEvent;
import com.innomalist.taxi.rider.events.ServiceRequestEvent;
import com.innomalist.taxi.rider.events.ServiceRequestResultEvent;
import com.innomalist.taxi.rider.ui.RiderBaseActivity;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.geometry.SubpolylineHelper;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.InertiaMoveListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import static org.greenrobot.eventbus.ThreadMode.MAIN;

public class MainActivity extends RiderBaseActivity implements ServiceCarousalFragment.OnServicesCarousalFragmentListener, LocationListener, OSSubscriptionObserver {

    ActivityMainBinding binding;
    MyPreferenceManager SP;
    MapView mMap;
    PlacemarkMapObject pickupPoint;
    PlacemarkMapObject destinationPoint;
    MarkerMode markerMode = MarkerMode.origin;
    DriverAcceptedDialog driverAcceptedDialog;
    ArrayList<PlacemarkMapObject> driverMarkers;
    private static final int ACTIVITY_PROFILE = 11;
    private static final int ACTIVITY_WALLET = 12;
    private static final int ACTIVITY_PLACES = 13;
    private static final int ACTIVITY_TRAVEL = 14;
    private static final int ACTIVITY_VOICE_RECOGNITION = 15;
    private static final int ACTIVITY_LOOKING = 16;
    ServiceCategoryViewPagerAdapter serviceCategoryViewPagerAdapter;
    BottomSheetBehavior bottomSheetBehavior;
    LatLng currentLocation;
    public Service selectedService;
    PolylineMapObject polylineOriginDestination;
    Travel travel = new Travel();

    private final CameraListener cameraListener = new CameraListener() {
        @Override
        public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {
            Log.d("LOG", "POSITION_HCANGESD");

            GetMarkerAddress task = new GetMarkerAddress();
            task.execute(map.getCameraPosition().getTarget().getLatitude(), map.getCameraPosition().getTarget().getLongitude());
            eventBus.post(new GetDriversLocationEvent(new LatLng(map.getCameraPosition().getTarget().getLatitude(),
                    map.getCameraPosition().getTarget().getLongitude())));
        }
    };

    @Override
    public void onServiceSelected(Service service) {
        selectedService = service;
        travel.setCostBest(service.getCost());
        binding.buttonRequest.setEnabled(true);
        binding.buttonRequest.setText(getString(R.string.confirm_service, service.getTitle()));
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() && stateChanges.getTo().getSubscribed())
            eventBus.post(new NotificationPlayerId(stateChanges.getTo().getUserId()));
    }

    private enum MarkerMode {
        origin,
        destination,
        serviceSelection
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MapKitFactory.setApiKey(getResources().getString(R.string.yandex_maps_key));
        MapKitFactory.initialize(this);

        super.setImmersive(true);
        super.onCreate(savedInstanceState);
        OneSignal.addSubscriptionObserver(this);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        currentLocation = LocationHelper.DoubleArrayToLatLng(getIntent().getDoubleArrayExtra("currentLocation"));
        binding.buttonConfirmPickup.setEnabled(false);
        binding.buttonConfirmPickup.setOnClickListener(view -> onButtonConfirmPickupClicked());
        binding.buttonConfirmDestination.setOnClickListener(view -> onButtonConfirmDestinationClicked());
        binding.buttonRequest.setOnClickListener(view -> onButtonConfirmServiceClicked());
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        binding.searchText.setSelected(true);
        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                binding.searchPlace.closeMenu(true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        binding.searchPlace.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                if (markerMode == MarkerMode.origin)
                    binding.drawerLayout.openDrawer(GravityCompat.START);
            }

            @Override
            public void onMenuClosed() {
                if (markerMode == MarkerMode.serviceSelection) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                if (markerMode == MarkerMode.destination || markerMode == MarkerMode.serviceSelection) {
                    goBackFromDestinationSelection();
                }
            }
        });
        binding.searchText.setOnClickListener(view -> findPlace(""));
        binding.searchPlace.setSearchFocusable(false);
        binding.searchPlace.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case (R.id.action_favorites):
                    eventBus.post(new CRUDAddressRequestEvent(CRUD.READ));
                    break;
                case (R.id.action_voice_rec):
                    displaySpeechRecognizer();
                    break;
                case (R.id.action_location):
                    mMap.getMap().move(new CameraPosition(new Point(currentLocation.latitude, currentLocation.longitude), 16f, 0f, 0f));
                    break;
            }
        });
        driverMarkers = new ArrayList<>();
        SP = MyPreferenceManager.getInstance(getApplicationContext());
        mMap = findViewById(R.id.map);
        mapReady();
        eventBus.post(new GetStatusEvent());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.app_name));
        }
        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            binding.drawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case (R.id.nav_item_favorites):
                    Intent intent = new Intent(MainActivity.this, AddressesActivity.class);
                    double[] array = LocationHelper.LatLngToDoubleArray(currentLocation);
                    intent.putExtra("currentLocation",array);
                    startActivity(intent);
                    break;
                case (R.id.nav_item_travels):
                    startActivity(new Intent(MainActivity.this, TravelsActivity.class));
                    break;
                case(R.id.nav_item_promotions):
                    startActivity(new Intent(MainActivity.this, PromotionsActivity.class));
                    break;
                case (R.id.nav_item_profile):
                    startActivityForResult(new Intent(MainActivity.this, ProfileActivity.class), ACTIVITY_PROFILE);
                    break;
                case (R.id.nav_item_charge_account):
                    startActivityForResult(new Intent(MainActivity.this, ChargeAccountActivity.class), ACTIVITY_WALLET);
                    break;
                case(R.id.nav_item_transactions):
                    startActivity(new Intent(MainActivity.this, TransactionsActivity.class));
                    break;
                case(R.id.nav_item_coupons):
                    startActivity(new Intent(MainActivity.this, CouponActivity.class));
                    break;
                case (R.id.nav_item_about):
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    break;
                case (R.id.nav_item_exit):
                    AlertDialogBuilder.show(MainActivity.this, getString(R.string.message_logout), AlertDialogBuilder.DialogButton.OK_CANCEL, result -> {
                        if (result == AlertDialogBuilder.DialogResult.OK)
                            logout();
                    });
                    break;
                default:
                    Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        });
        fillInfo();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMap.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMap.onStart();
        MapKitFactory.getInstance().onStart();
    }

    private void goBackFromDestinationSelection() {
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), (new TransitionSet()).addTransition(new Slide()).addTransition(new Fade()));
        binding.buttonConfirmDestination.setVisibility(View.GONE);
        binding.buttonConfirmPickup.setVisibility(View.VISIBLE);
        markerMode = MarkerMode.origin;

        mMap.getMap().move(new CameraPosition(new Point(pickupPoint.getGeometry().getLatitude(), pickupPoint.getGeometry().getLongitude()),
                16f, 0f, 0f));
       // mMap.animateCamera(CameraUpdateFactory.newLatLng(pickupPoint.getPosition()));
        mMap.getMap().getMapObjects().remove(pickupPoint);

        showPickupMarker();
    }

    private void logout() {
        SP.putString("rider_token", "");
        finish();
    }

    private void showCurvedPolyline(LatLng p1, LatLng p2, double k) {
        //Calculate distance and heading between two points
        double d = SphericalUtil.computeDistanceBetween(p1,p2);
        double h = SphericalUtil.computeHeading(p1, p2);

        //Midpoint position
        LatLng p = SphericalUtil.computeOffset(p1, d*0.5, h);

        //Apply some mathematics to calculate position of the circle center
        double x = (1-k*k)*d*0.5/(2*k);
        double r = (1+k*k)*d*0.5/(2*k);

        LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);

        //Calculate heading between circle center and two points
        double h1 = SphericalUtil.computeHeading(c, p1);
        double h2 = SphericalUtil.computeHeading(c, p2);

        //Calculate positions of points on circle border and add them to polyline options
        int numpoints = 100;
        double step = (h2 -h1) / numpoints;

        ArrayList<Point> points = new ArrayList<Point>();

        for (int i=0; i < numpoints; i++) {
            LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
            points.add(new Point(pi.latitude, pi.longitude));
        }

        //Draw polyline
        polylineOriginDestination = mMap.getMap().getMapObjects().addPolyline(new Polyline(points));
        polylineOriginDestination.setStrokeColor(getPrimaryColor());
        polylineOriginDestination.setStrokeWidth(10);
        polylineOriginDestination.setZIndex(100);
        polylineOriginDestination.setGeodesic(true);
        polylineOriginDestination.setGapLength(20);
        polylineOriginDestination.setDashLength(30);
    }

    public void findPlace(String preText) {
        try {
            // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, ACTIVITY_PLACES);
            /*AutocompleteFilter autocompleteFilter = (new AutocompleteFilter.Builder()).setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(autocompleteFilter)
                            .build(this);
            if (!TextUtils.isEmpty(preText)) {
                intent.putExtra("initial_query", preText);
            }
            startActivityForResult(intent, ACTIVITY_PLACES);*/
        } catch (Exception ignored){

        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage(getString(R.string.message_permission_denied))
                .setPermissions(Manifest.permission.RECORD_AUDIO)
                .check();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            try {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getString(R.string.default_language));

                MainActivity.this.startActivityForResult(intent, ACTIVITY_VOICE_RECOGNITION);
            } catch (ActivityNotFoundException e) {
                AlertDialogBuilder.show(MainActivity.this, getString(R.string.question_install_speech), getString(R.string.error), AlertDialogBuilder.DialogButton.OK_CANCEL, result -> {
                    if(result == AlertDialogBuilder.DialogResult.OK) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://market.android.com/details?id=com.google.android.voicesearch"));
                        startActivity(browserIntent);
                    }
                });
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {

        }

    };

    @Subscribe(threadMode = MAIN)
    public void onAddressesReceived(CRUDAddressResultEvent event) {
        if (event.crud != CRUD.READ || !isInForeground)
            return;
        if(event.addresses.size() < 1) {
            AlerterHelper.showWarning(MainActivity.this,getString(R.string.warning_no_favorite_place));
            return;
        }
        List<String> addressStrings = new ArrayList<>();
        for (com.innomalist.taxi.common.models.Address address :
                event.addresses) {
            addressStrings.add(address.getTitle());
        }
        new MaterialDialog.Builder(this)
                .title(R.string.drawer_favorite_locations)
                .items(addressStrings)
                .itemsCallback((dialog, view, which, text) -> {
                    if (event.addresses.get(which).getLocation() != null) {
                        mMap.getMap().move(new CameraPosition(new Point(event.addresses.get(which).getLocation().latitude,
                                event.addresses.get(which).getLocation().longitude), 16f, 0f, 0f)) ;
                    }
                    binding.searchText.setText(event.addresses.get(which).getAddress());

                })
                .show();
    }

    private void onButtonConfirmPickupClicked() {
        binding.buttonConfirmPickup.setEnabled(false);
        binding.buttonConfirmDestination.setEnabled(false);
        showDestinationMarker();
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), (new TransitionSet()).addTransition(new Slide()).addTransition(new Fade()));
        binding.buttonConfirmPickup.setVisibility(View.GONE);
        binding.buttonConfirmDestination.setVisibility(View.VISIBLE);
        markerMode = MarkerMode.destination;
        binding.searchPlace.openMenu(true);
        travel.setPickupPoint(new LatLng(mMap.getMap().getCameraPosition().getTarget().getLatitude(),
                mMap.getMap().getCameraPosition().getTarget().getLongitude()));

        pickupPoint = mMap.getMap().getMapObjects().
                addPlacemark(new Point(travel.getPickupPoint().latitude, travel.getPickupPoint().longitude),
                        ImageProvider.fromResource(getApplicationContext(), R.drawable.marker_pickup));

        mMap.getMap().move(new CameraPosition(
                new Point(mMap.getMap().getCameraPosition().getTarget().getLatitude() + 0.001, mMap.getMap().getCameraPosition().getTarget().getLongitude()), 16f ,0f, 0f));
    }

    private void onButtonConfirmDestinationClicked() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.buttonRequest.setEnabled(false);
        markerMode = MarkerMode.serviceSelection;
        binding.imageDestination.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        travel.setDestinationPoint(new LatLng(mMap.getMap().getCameraPosition().getTarget().getLatitude(),
                mMap.getMap().getCameraPosition().getTarget().getLongitude()));
        mMap.setPadding(0, binding.bottomSheet.getHeight() / 10, 0, binding.bottomSheet.getHeight());
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(travel.getPickupPoint());
        latLngs.add(travel.getDestinationPoint());
        mMap.getMap().getMapObjects().remove(pickupPoint);

        MapHelper.centerLatLngsInMap(mMap, latLngs, true);

        mMap.getMap().setZoomGesturesEnabled(false);
        mMap.getMap().setRotateGesturesEnabled(false);
        mMap.getMap().setScrollGesturesEnabled(false);
        mMap.getMap().setTiltGesturesEnabled(false);

        binding.mapLayout.postDelayed(() -> {
            Bitmap pickUpBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_pickup);
            pickupPoint = mMap.getMap().getMapObjects().addPlacemark(new Point(travel.getPickupPoint().latitude, travel.getPickupPoint().longitude),
                    ImageProvider.fromBitmap(pickUpBitmap));

            Bitmap dropBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_destination);

            destinationPoint = mMap.getMap().getMapObjects().addPlacemark(new Point(travel.getDestinationPoint().latitude, travel.getDestinationPoint().longitude),
                    ImageProvider.fromBitmap(dropBitmap));

            //showCurvedPolyline(travel.getPickupPoint(),travel.getDestinationPoint(),0.2);
            //((TrailSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setUpPath(latLngs, mMap, RouteOverlayView.AnimType.ARC);
            //Polyline polyline1 = mMap.addPolyline(new PolylineOptions().clickable(true).add(pickupLatLng,destinationLatLng).color(getPrimaryColor()).endCap(new RoundCap()).startCap(new RoundCap()));
        }, 1500);
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), (new TransitionSet()).addTransition(new Fade()));
        binding.buttonConfirmDestination.setVisibility(View.GONE);
        binding.searchPlace.setVisibility(View.GONE);
        eventBus.post(new CalculateFareRequestEvent(travel.getPickupPoint(), travel.getDestinationPoint()));
    }

    private void goBackFromServiceSelection() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if(polylineOriginDestination!= null)
            mMap.getMap().getMapObjects().remove(polylineOriginDestination);
        markerMode = MarkerMode.origin;
        showPickupMarker();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mMap.setPadding(0, 0, 0, 0);

        mMap.getMap().setZoomGesturesEnabled(true);
        mMap.getMap().setRotateGesturesEnabled(true);
        mMap.getMap().setScrollGesturesEnabled(true);
        mMap.getMap().setTiltGesturesEnabled(true);

        binding.buttonConfirmPickup.setEnabled(false);
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), (new TransitionSet()).addTransition(new Fade()));
        binding.buttonConfirmPickup.setVisibility(View.VISIBLE);
        binding.searchPlace.setVisibility(View.VISIBLE);
        binding.searchPlace.closeMenu(false);
        if(pickupPoint != null)
            mMap.getMap().getMapObjects().remove(pickupPoint);
        if(destinationPoint != null)
            mMap.getMap().getMapObjects().remove(destinationPoint);
    }

    private void onButtonConfirmServiceClicked() {
        eventBus.post(new ServiceRequestEvent(travel.getPickupPoint(), travel.getDestinationPoint(), travel.getPickupAddress(), travel.getDestinationAddress(), selectedService.getId()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceRequestResult(ServiceRequestResultEvent event) {
        if (event.hasError()) {
            event.showAlert(this);
            return;
        }
        startActivityForResult(new Intent(MainActivity.this, LookingActivity.class), ACTIVITY_LOOKING);
    }

    private void showPickupMarker(){
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), new Fade());
        if(binding.imageDestination.getVisibility() == View.VISIBLE)
            binding.imageDestination.setVisibility(View.GONE);
        binding.imagePickup.setVisibility(View.VISIBLE);
    }

    private void showDestinationMarker(){
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), new Fade());
        if(binding.imagePickup.getVisibility() == View.VISIBLE)
            binding.imagePickup.setVisibility(View.GONE);
        binding.imageDestination.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = MAIN)
    public void onCalculateFareReceived(CalculateFareResultEvent event) {
        if (event.hasError()) {
            if(event.message.equals("Cannot read property 'elements' of undefined")) {
                AlertDialogBuilder.show(MainActivity.this, "Your api key of server couldn't access Distance matrix api. Check server api key and make sure it's correct and has Distance Matrix api enabled and also billing is enabled for your google maps api.");
                return;
            }
            event.showAlert(this);
            goBackFromServiceSelection();
            return;
        }
        serviceCategoryViewPagerAdapter = new ServiceCategoryViewPagerAdapter(getSupportFragmentManager(), event.serviceCategories);
        binding.serviceTypesViewPager.setAdapter(serviceCategoryViewPagerAdapter);
        binding.tabCategories.setupWithViewPager(binding.serviceTypesViewPager);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    public void mapReady()
    {
        mMap.getMap().move(new CameraPosition(new Point(currentLocation.latitude, currentLocation.longitude), 16f, 0f, 0f));

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);

        mMap.getMap().addCameraListener(cameraListener);

            /*
        mMap.getMap().addInertiaMoveListener(new InertiaMoveListener() {
            @Override
            public void onStart(@NonNull Map map, @NonNull CameraPosition cameraPosition) {
                Log.d("MAP_LISTENERS", "START");
            }

            @Override
            public void onCancel(@NonNull Map map, @NonNull CameraPosition cameraPosition) {
                Log.d("MAP_LISTENERS", "CANCEL");
                GetMarkerAddress task = new GetMarkerAddress();
                task.execute(map.getCameraPosition().getTarget().getLatitude(), map.getCameraPosition().getTarget().getLongitude());
                eventBus.post(new GetDriversLocationEvent(new LatLng(map.getCameraPosition().getTarget().getLatitude(),
                        map.getCameraPosition().getTarget().getLongitude())));
            }

            @Override
            public void onFinish(@NonNull Map map, @NonNull CameraPosition cameraPosition)
            {
                Log.d("MAP_LISTENERS", "FINIS");
                GetMarkerAddress task = new GetMarkerAddress();
                task.execute(map.getCameraPosition().getTarget().getLatitude(), map.getCameraPosition().getTarget().getLongitude());
                eventBus.post(new GetDriversLocationEvent(new LatLng(map.getCameraPosition().getTarget().getLatitude(),
                        map.getCameraPosition().getTarget().getLongitude())));
            }
        });


        if (getResources().getBoolean(R.bool.isNightMode)) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night));
        }*/
    }

    @Override
    public void onBackPressed() {
        if (markerMode == MarkerMode.origin)
            AlertDialogBuilder.show(MainActivity.this, getString(R.string.message_exit), AlertDialogBuilder.DialogButton.OK_CANCEL, result -> {
                if (result == AlertDialogBuilder.DialogResult.OK)
                    MainActivity.this.finishAffinity();
            });
        if(markerMode == MarkerMode.destination)
            goBackFromDestinationSelection();
        if (markerMode == MarkerMode.serviceSelection)
            goBackFromServiceSelection();
    }

    private void fillInfo() {
        try {
            String name;
            if (CommonUtils.rider.status != null && CommonUtils.rider.status.equals("blocked")) {
                logout();
                return;
            }
            if ((CommonUtils.rider.getFirstName() == null || CommonUtils.rider.getFirstName().isEmpty()) && (CommonUtils.rider.getLastName() == null || CommonUtils.rider.getLastName().isEmpty()))
                name = String.valueOf(CommonUtils.rider.getMobileNumber());
            else
                name = CommonUtils.rider.getFirstName() + " " + CommonUtils.rider.getLastName();
            View header = binding.navigationView.getHeaderView(0);
            ((TextView) header.findViewById(R.id.navigation_header_name)).setText(name);
            ((TextView) header.findViewById(R.id.navigation_header_charge)).setText(getString(R.string.drawer_header_balance, CommonUtils.rider.getBalance()));
            ImageView imageView = header.findViewById(R.id.navigation_header_image);
            DataBinder.setMedia(imageView, CommonUtils.rider.getMedia());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = MAIN)
    public void onDriversLocationResult(GetDriversLocationResultEvent event) {
        if (event.response != ServerResponse.OK)
            return;
        for (PlacemarkMapObject marker : driverMarkers) {
            mMap.getMap().getMapObjects().remove(marker);
            driverMarkers.remove(marker);
        }
        for (DriverLocation driverLocation : event.driverLocations)

            /*
            mMap.getMap().getMapObjects().
                addPlacemark(new Point(travel.getPickupPoint().latitude, travel.getPickupPoint().longitude),
                        ImageProvider.fromResource(getApplicationContext(), R.drawable.marker_pickup));
             */

            driverMarkers.add(mMap.getMap().getMapObjects().addPlacemark(new Point(driverLocation.location.latitude, driverLocation.location.longitude),
                    ImageProvider.fromResource(getApplicationContext(), R.drawable.marker_taxi)));
    }

    @Subscribe(threadMode = MAIN)
    public void onRequestTaxiError(ServiceRequestErrorEvent event) {
        if (driverAcceptedDialog != null)
            driverAcceptedDialog.dismiss();
        event.showAlert(MainActivity.this);
    }

    @Subscribe(threadMode = MAIN, sticky = true)
    public void onProfileChanged(ProfileInfoChangedEvent event) {
        fillInfo();
    }


    @SuppressLint("StaticFieldLeak")
    private class GetMarkerAddress extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... floats) {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(floats[0], floats[1], 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                String address = "";
                if (addresses.get(0).getThoroughfare() != null)
                    address = addresses.get(0).getThoroughfare();
                if (addresses.get(0).getFeatureName() != null) {
                    if (address.equals(""))
                        address = addresses.get(0).getFeatureName();
                    else
                        address += ", " + addresses.get(0).getFeatureName();
                }
                return address;
            } else
                return getString(R.string.unknown_location);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            binding.searchText.setText(s);
            if (markerMode == MarkerMode.origin) {
                travel.setPickupAddress(s);
                binding.buttonConfirmPickup.setEnabled(true);
            } else {
                travel.setDestinationAddress(s);
                binding.buttonConfirmDestination.setEnabled(true);
            }
        }
    }

    @Subscribe(threadMode = MAIN)
    public void OnGetStatusResultReceived(GetStatusResultEvent event) {
        if(event.hasError())
            return;
        AlertDialogBuilder.show(MainActivity.this, getString(R.string.recovery_travel_message_rider), getString(R.string.message_default_title), AlertDialogBuilder.DialogButton.OK_CANCEL, result -> {
            if(result == AlertDialogBuilder.DialogResult.OK) {
                Intent intent = new Intent(MainActivity.this, TravelActivity.class);
                intent.putExtra("travel",event.travel.toJson());
                startActivityForResult(intent,ACTIVITY_TRAVEL);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ACTIVITY_PROFILE):
                if (resultCode == RESULT_OK)
                    AlerterHelper.showInfo(MainActivity.this, getString(R.string.info_edit_profile_success));
                fillInfo();
                break;

            case (ACTIVITY_WALLET):
                if (resultCode == RESULT_OK)
                    AlerterHelper.showInfo(MainActivity.this, getString(R.string.account_charge_success));
                break;

            case (ACTIVITY_PLACES):
                binding.searchPlace.clearSearchFocus();
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);

                    mMap.getMap().move(new CameraPosition(new Point(place.getLatLng().latitude, place.getLatLng().longitude), 16f, 0f, 0f));

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i("PLACES", status.getStatusMessage());
                }
                break;

            case(ACTIVITY_LOOKING):
                if(resultCode == RESULT_OK) {
                    Intent intent = new Intent(MainActivity.this, TravelActivity.class);
                    Gson gson = new Gson();
                    Driver driver = gson.fromJson(data.getStringExtra("driver"), Driver.class);
                    travel.setDriver(driver);
                    intent.putExtra("travel",travel.toJson());
                    startActivityForResult(intent, ACTIVITY_TRAVEL);
                } else {
                    goBackFromServiceSelection();
                }
                break;
            case (ACTIVITY_VOICE_RECOGNITION):
                if (resultCode == RESULT_OK) {
                    List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results.size() > 0)
                        findPlace(results.get(0));
                    else
                        AlerterHelper.showWarning(this, getString(R.string.warning_voice_recognizer_failed));
                }
                break;

            case (ACTIVITY_TRAVEL):
                goBackFromServiceSelection();
                break;
        }
    }
}
