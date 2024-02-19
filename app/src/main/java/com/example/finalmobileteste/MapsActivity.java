package com.example.finalmobileteste;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.example.finalmobileteste.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView textTime, textDistance, textSpeed;
    private long startTime;
    private Location previousLocation;
    private double distanceInMeters;
    private float lastKnownCourse = 0.0f;
    private int speedUnit;
    private int tipoMapaSelecionado;
    private int orientacaoMapaSelecionada;
    private Circle userCircle;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final double PESO_PADRAO_KG = 70.0; // Peso padrão em quilogramas
    private static final double MET_CAMINHADA = 3.5; // Valor MET para caminhada
    private static final double CALORIAS_POR_KG_POR_KM = 0.035; // Calorias gastas por kg por km

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        startTime = System.currentTimeMillis();
        distanceInMeters = 0;
        textTime = findViewById(R.id.textTime);
        textDistance = findViewById(R.id.textDistance);
        textSpeed = findViewById(R.id.textSpeed);

        // Inicialize o SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map, mapFragment)
                    .commit();
        }

        mapFragment.getMapAsync(this);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                updateLocationOnMap(location);
                updateStats(location);
            }
        };

        // Verificar as permissões em tempo de execução (se necessário)
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }

        Intent intent = getIntent();
        speedUnit = intent.getIntExtra("velocidade_id", -1);
        tipoMapaSelecionado = intent.getIntExtra("tipo_mapa_id", -1);
        orientacaoMapaSelecionada = intent.getIntExtra("mapa_id", -1);


    }


    private void updateStats(Location location) {
        // Atualize o tempo decorrido
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        String formattedTime = formatElapsedTime(elapsedTime);
        textTime.setText("Tempo: " + formattedTime);

        // Atualize a distância percorrida
        if (location != null && previousLocation != null) {
            distanceInMeters += previousLocation.distanceTo(location);
            double distanceInKm = distanceInMeters / 1000.0;
            textDistance.setText("Distância: " + String.format("%.2f", distanceInKm) + " km");
        }

        previousLocation = location;

        // Atualize a velocidade instantânea
        if (location != null) {
            double speed = location.getSpeed(); // Velocidade em metros por segundo

            if (speedUnit == R.id.radioMs) {
                textSpeed.setText("Velocidade: " + String.format("%.2f", speed) + " m/s");
            } else {
                speed = speed * 3.6; // Converta para quilômetros por hora
                textSpeed.setText("Velocidade: " + String.format("%.2f", speed) + " km/h");
            }
        }

        if (location != null) {
            lastKnownCourse = location.getBearing();
        }


    }

    private String formatElapsedTime(long elapsedTime) {
        // Converte o tempo decorrido para um formato de horas, minutos e segundos
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long hours = elapsedTime / (1000 * 60 * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLatLng = new LatLng(-12.9485502243042, -38.41313171386719);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(defaultLatLng)
                .zoom(14)
                .build();
        mMap.setMyLocationEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Aplicar a escolha do tipo de mapa
        if (tipoMapaSelecionado == R.id.radioSatelite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);  // Mapa de satélite
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);  // Mapa vetorial
        }

        // Certifique-se de chamar o método updateMapOrientation aqui
        updateMapOrientation();
    }




    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        );
    }

    private void updateLocationOnMap(Location location) {
        if (mMap != null && location != null) {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Mover a câmera para a nova posição do usuário
            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Você está aqui"));

            // Mova a câmera para a nova posição do usuário
            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            // Se ainda não há um círculo, crie um
            if (userCircle == null) {
                userCircle = mMap.addCircle(new CircleOptions()
                        .center(currentLatLng)
                        .radius(5)
                        .strokeWidth(0)
                        .fillColor(ContextCompat.getColor(this, R.color.purple_700)));
            } else {
                // Se já existe um círculo, apenas mova-o para a nova posição
                userCircle.setCenter(currentLatLng);
            }
        }
    }

    private void updateMapOrientation() {
        if (mMap != null) {
            if (orientacaoMapaSelecionada == R.id.radioNenhuma) {
                // Sem orientação, deixe o Google Map decidir automaticamente
                mMap.getUiSettings().setMapToolbarEnabled(true);


            } else if (orientacaoMapaSelecionada == R.id.radioNorthUp) {
                // Orientação "North Up"


                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder()
                                .target(mMap.getCameraPosition().target)
                                .zoom(mMap.getCameraPosition().zoom)
                                .bearing(0)  // Alinhar ao norte
                                .tilt(0)
                                .build()));

            } else if (orientacaoMapaSelecionada == R.id.radioCourseUp) {
                // Orientação "Course Up"


                mMap.getUiSettings().setMapToolbarEnabled(false);
                // Use a última direção do curso conhecida


                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder()

                                .target(mMap.getCameraPosition().target)
                                .zoom(mMap.getCameraPosition().zoom)
                                .bearing(lastKnownCourse)
                                .tilt(0)
                                .build()));
            }
        }
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}

