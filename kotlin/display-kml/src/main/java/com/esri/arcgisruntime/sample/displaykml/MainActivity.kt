/*
 * Copyright 2020 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.arcgisruntime.sample.displaykml

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.layers.KmlLayer
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.loadable.Loadable
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.ogc.kml.KmlDataset
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG: String = MainActivity::class.java.getSimpleName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create a map with the dark gray canvas basemap
        val map = ArcGISMap(Basemap.Type.DARK_GRAY_CANVAS_VECTOR, 39.0, -98.0, 4);
        // set the map to the map view
        mapView.map = map
    }

    /**
     * Clear all operational layers and add the kml layer to the map as an operational layer.
     *
     * @param kmlLayer to add to the map
     */
    fun display(kmlLayer: KmlLayer) {
        val operationalLayers = mapView.map.operationalLayers
        operationalLayers.clear()
        operationalLayers.add(kmlLayer)
    }

    /**
     * Display a kml layer from a URL.
     */
    fun changeSourceToURL() {
        // create a kml data set from a URL
        val kmlDataset = KmlDataset(getString(R.string.noaa_weather_kml_url))
        // create a kml layer created from the kml data set
        val kmlLayer = KmlLayer(kmlDataset)
        display(kmlLayer)
        // report layers if failed to load
        reportErrors(kmlLayer, "Failed to load kml layer from URL: ")
    }

    /**
     * Display a kml layer from a portal item.
     */
    fun changeSourceToPortalItem() {
        // create a portal to ArcGIS Online
        val portal = Portal(getString(R.string.arcgis_online_url))
        // create a portal item from a kml item id
        val portalItem = PortalItem(portal, getString(R.string.kml_item_id))

        // a KML layer created from an ArcGIS Online portal item
        val kmlLayer = KmlLayer(portalItem)
        display(kmlLayer)
        // report layers if failed to load
        reportErrors(kmlLayer, "Failed to load kml layer from portal item: ")
    }

    /**
     * Display a kml layer from external storage.
     */
    fun changeSourceToFileExternalStorage() {
        // get the data set stored locally in device external storage
        val file = getExternalFilesDir(null)?.path + getString(R.string.kml_path)
        val kmlDataset = KmlDataset(getExternalFilesDir(null)?.path + getString(R.string.kml_path))
        // create a KML layer from the locally stored data set
        val kmlLayer = KmlLayer(kmlDataset)
        display(kmlLayer)
        // report layers if failed to load
        reportErrors(kmlLayer, "Failed to load kml data set from external storage: " + file)
    }

    /**
     * Reports any loading errors of the kml datasets
     *
     * @param kmlData any loadable containing a kml data set e.g. KmlDataset or KmlLayer
     * @param string the error message to display
     */
    fun reportErrors(kmlData: Loadable, string: String) {
        // report errors if failed to load
        kmlData.addDoneLoadingListener {
            if (kmlData.loadStatus != LoadStatus.LOADED) {
                val error = string + kmlData.loadError.message
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                Log.e(TAG, error)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.kml_sources, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.kmlFromUrl -> changeSourceToURL()
            R.id.kmlFromPortal -> changeSourceToPortalItem()
            R.id.kmlFromExternalStorage -> changeSourceToFileExternalStorage()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.dispose()
    }
}