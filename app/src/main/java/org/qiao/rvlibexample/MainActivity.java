package org.qiao.rvlibexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.common.collect.Lists;

import org.qiao.rvlib.navigation.OccupancyGrid;
import org.qiao.rvlib.sensor.LaserScan;
import org.qiao.rvlib.visualization.VisualizationView;
import org.qiao.rvlib.visualization.layer.LaserScanLayer;
import org.qiao.rvlib.visualization.layer.Layer;
import org.qiao.rvlib.visualization.layer.OccupancyGridLayer;
import org.qiao.rvlib.visualization.layer.ParticlesLayer;
import org.qiao.rvlib.visualization.layer.RobotLayer;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private VisualizationView visualizationView;

    private float x,y,theta;
    private RobotLayer robotLayer;
    private LaserScanLayer laserscanLayer;
    private OccupancyGridLayer occgridLayer;
    private ParticlesLayer particlesLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        robotLayer = new RobotLayer("base_footprint");
        laserscanLayer = new LaserScanLayer("scan");
        occgridLayer = new OccupancyGridLayer("grid_map");
        particlesLayer = new ParticlesLayer("particles");
        visualizationView = (VisualizationView) findViewById(R.id.visualization);
        visualizationView.onCreate(Lists.<Layer>newArrayList(occgridLayer, robotLayer, laserscanLayer, particlesLayer));
    }

    @Override
    protected void onResume() {
        super.onResume();
        visualizationView.startLayers();
        x = 10;
        y = 10;
        theta = 10;
        robotLayer.setRobotPose2D(x,y,theta);
    }

    @Override
    protected void onPause() {
        super.onPause();
        visualizationView.stopLayers();
    }

    public void onMoveBtnClick(View v) {
        x += 0.5;
        y += 0.5;
        theta += 0.1;
        robotLayer.setRobotPose2D(x,y,theta);
    }

    public void onLaserBtnClick(View v) {
        int length = 121;
        float angle_inc = 1 * (float)Math.PI/180.f;
        float angle_min = -60.0f * (float)Math.PI/180.f;
        float range_max = 3.f;
        float range_min = 0.3f;
        float[] scan = new float[length];
        float range_step = (range_max - range_min)/length;
        for (int i=0; i<length; i++) {
            scan[i] = (range_max - i * range_step);
        }
        LaserScan laserscan = new LaserScan(scan, range_max, range_min, angle_min, angle_inc);
        laserscanLayer.setLaserPose(x,y,theta);
        laserscanLayer.updateLaserScan(laserscan);
    }

    public void onReadMapFileBtnClick(View v) {
        try {
            String ext_storage_dir = "/sdcard";
            String file_path = ext_storage_dir + "/sim_map.yaml";
            OccupancyGrid occ_grid = OccupancyGrid.fromYamlFile(file_path);
            occgridLayer.updateOccupancyGrid(occ_grid);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void onAddParticleBtnClick(View v) {
        final int NUM_OF_PARTICLES = 50;
        ArrayList<float[]> particles = new ArrayList<>();
        for (int i=0; i<NUM_OF_PARTICLES; i++) {
            float[] p = new float[3];
            p[0] = (float)(Math.random() - 0.5) * 500;
            p[1] = (float)(Math.random() - 0.5) * 500;
            p[2] = (float)(Math.random() * Math.PI * 2);
            particles.add(p);
        }
        particlesLayer.updateParticles(particles);
    }

    public void onClearParticlesBtnClick(View v) {
        particlesLayer.clearParticles();
    }
}
