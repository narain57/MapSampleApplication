package com.android.mapsampleapplication.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mapsampleapplication.R;
import com.android.mapsampleapplication.model.Vehicle;
import com.android.mapsampleapplication.network.VehicleInteractorImpl;
import com.android.mapsampleapplication.presenter.VehiclePresenter;
import com.android.mapsampleapplication.presenter.VehiclePresenterImpl;
import com.android.mapsampleapplication.ui.adapter.RecyclerViewAdapter;
import com.android.mapsampleapplication.utils.Utils;
import com.android.mapsampleapplication.view.LockableRecyclerView;
import com.android.mapsampleapplication.view.MainView;
import com.android.mapsampleapplication.view.SlidingUpPanelLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapFragment extends Fragment implements MainView,
        SlidingUpPanelLayout.PanelSlideListener, RecyclerViewAdapter.ItemClickListener, OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private static final String ARG_LOCATION = "arg.location";

    private LockableRecyclerView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentView;

    private VehiclePresenter presenter;
    private ArrayList<Vehicle> vehicleList;
    private ProgressBar progressBar;

    private RecyclerViewAdapter mRecyclerViewAdapter;

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private View rootView;

    public MapFragment() {
    }

    public static MapFragment newInstance(LatLng location) {
        MapFragment f = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.map_fragment, container, false);

        mListView = rootView.findViewById(android.R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        presenter = new VehiclePresenterImpl(this, new VehicleInteractorImpl());

        mSlidingUpPanelLayout = rootView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        mTransparentView = rootView.findViewById(R.id.transparentView);

        collapseMap();

        mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlidingUpPanelLayout.onPanelDragged(0);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initProgressBar();
        presenter.fetchVehicleDetails();
    }

    @SuppressLint("MissingPermission")
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Utils.isLocationAllowed(getActivity())) {
                        mMap.setMyLocationEnabled(false);
                    } else {
                        Utils.requestStoragePermission(getActivity(), this);
                    }
                } else {
                    if (Utils.isLocationAllowed(getActivity())) {
                        mMap.setMyLocationEnabled(false);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.location_permission), Toast.LENGTH_LONG).show();
                    }
                }
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.setOnMarkerClickListener(this);
                for(Vehicle vehicle:vehicleList) {
                    addMarker(vehicle);

                }
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(Double.parseDouble(vehicleList.get(0).getCoordinate().getLatitude()),Double.parseDouble(vehicleList.get(0).getCoordinate().getLongitude())),11.0f)));

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                    }
                });
            }
    }

    private void showSnackBar() {
        CoordinatorLayout rootlayout = rootView.findViewById(R.id.rootLayout);

        Snackbar snackbar = Snackbar
                .make(rootlayout, getString(R.string.request_raised), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void addMarker(Vehicle vehicle){

        double latD = Double.parseDouble(vehicle.getCoordinate().getLatitude());
        double longtD = Double.parseDouble(vehicle.getCoordinate().getLongitude());

        MarkerOptions marker = new MarkerOptions().position(new LatLng(latD, longtD)).title(vehicle.getId()).snippet(vehicle.getType());
        mMap.addMarker(marker);

    }

    private void collapseMap() {
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.showSpace();
        }
        mTransparentView.setVisibility(View.GONE);
        mListView.setScrollingEnabled(true);
    }

    private void expandMap() {
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.hideSpace();
        }
        mTransparentView.setVisibility(View.INVISIBLE);
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
        mListView.setScrollingEnabled(false);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }


    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    private void initProgressBar() {
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);

        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.addView(progressBar);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        progressBar.setVisibility(View.INVISIBLE);

        getActivity().addContentView(relativeLayout, params);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setVehicleList(ArrayList<Vehicle> vehicles) {
        this.vehicleList = vehicles;
        initRecyclerView();

        mMapFragment = SupportMapFragment.newInstance();

        Utils.makeFragmentTransaction(getActivity(),R.id.mapContainer,mMapFragment);
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMapFragment.getMapAsync(this);
        }
    }

    private void initRecyclerView() {
        mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity(), vehicleList, this);
        mListView.setItemAnimator(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.fallback_anim_layout);
        mListView.setLayoutAnimation(controller);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mRecyclerViewAdapter);
    }

    @Override
    public void onFailure(Throwable t) {
        Toast.makeText(getActivity(),
                "Something went wrong... ",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onItemClicked(int position) {
        mSlidingUpPanelLayout.collapsePane();
        if(position > 0)
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(Double.parseDouble(vehicleList.get(position-1).getCoordinate().getLatitude()),Double.parseDouble(vehicleList.get(position-1).getCoordinate().getLongitude())),11.0f)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpMapIfNeeded();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Checking the request code of our request
        if (requestCode == Utils.LOCATION_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(false);
            } else {
                // user rejected the permission
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (!showRationale) {
                    // CHECKED "never ask again"
                    Toast.makeText(getActivity(), getString(R.string.location_permission), Toast.LENGTH_LONG).show();
                }
            }
        }
    }//end of onRequestPermissionsResult

    @Override
    public boolean onMarkerClick(Marker marker) {
        Utils.showAlertDialogWithOkAndCancelButton(getActivity(),getString(R.string.vehicle_request), String.format(getString(R.string.request_dialog),marker.getSnippet(),marker.getTitle()), getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSnackBar();
            }
        }, null, false);
        return false;
    }
}