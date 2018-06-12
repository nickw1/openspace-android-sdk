/**
 * OpenSpace Android SDK Licence Terms
 *
 * The OpenSpace Android SDK is protected by © Crown copyright – Ordnance Survey 2013.[https://github.com/OrdnanceSurvey]
 *
 * All rights reserved (subject to the BSD licence terms as follows):.
 *
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * Neither the name of Ordnance Survey nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 * Changes Nick Whitelegg (NW) 120618
 * - added bounds calculation methods, making use of getScale() in OSMap
 * - made class public
 */
package uk.co.ordnancesurvey.android.maps;


import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public final class MapView extends FrameLayout {
	private final GLMapRenderer mMapRenderer;
	private final OSMapPrivate mMap;

	MapView(Context context, AttributeSet set) {
		super(context, set);
		GLMapRenderer map = init(context, null);
		mMapRenderer = map;
		mMap = map;

	}
	MapView(Context context) {
		super(context);
		GLMapRenderer map = init(context, null);
		mMapRenderer = map;
		mMap = map;
	}
	
	MapView(Context context, OSMapOptions options) {
		super(context);
		GLMapRenderer map = init(context, options);
		mMapRenderer = map;
		mMap = map;		
	}
	
	private GLMapRenderer init(Context context, OSMapOptions options) {
		LayoutParams fill = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.FILL);

		final MapScrollController scrollController = new MapScrollController(context, new MapScrollController.DragListener() {
			@Override
			public Object onDragBegin(MotionEvent e) {
				// TODO Auto-generated method stub
				Log.v("Drag", "Start!");
				return mMap.longClick(e.getX(), e.getY());
			}

			@Override
			public void onDrag(MotionEvent e, Object dragObject) {
				mMap.drag(e.getX(), e.getY(), dragObject);
			}

			@Override
			public void onDragEnd(MotionEvent e, Object dragObject) {
				mMap.dragEnded(e.getX(), e.getY(), dragObject);
			}

			@Override
			public void onDragCancel(MotionEvent e, Object dragObject) {
				// TODO Auto-generated method stub
				Log.v("Drag", "Cancel!");
			}
		}, new MapScrollController.ScrollListener() {
			@Override
			public void onScrollScaleFling(MapScrollController detector) {
				// TODO: Should this be an interface method?
				mMapRenderer.requestRender();
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				return mMap.singleClick(e.getX(), e.getY());
			}
		});
		setOnTouchListener(scrollController);


		GLMapRenderer map = new GLMapRenderer(context, scrollController);
		map.setLayoutParams(fill);
		addView(map);


		if(options == null || options.getProducts() == null)
		{

            // If no map stack specified then use the default layers; these are available to Free and Pro
            // API key users.
			map.setMapLayers(MapLayer.getDefaultLayers());

		}
		else
		{

            // Otherwise use the map stack specified by the user
			map.setMapLayers(MapLayer.layersForProductCodes(options.getProducts()));

		}
		return map;
	}
	
	public OSMap getMap()
	{
		return mMap;
	}
	

	/**
	* Must be forwarded from the containing Activity/Fragment.
	* Saving/restoring instance state is not supported yet.
	*/
	public final void onCreate(Bundle savedInstanceState)
	{
	}
	/**
	* Must be forwarded from the containing Activity/Fragment.
	*/
	public final void onDestroy()
	{
		mMapRenderer.onDestroy();
	}
	/**
	* Must be forwarded from the containing Activity/Fragment.
	*/
	public final void onLowMemory()
	{
	}
	/**
	* Must be forwarded from the containing Activity/Fragment.
	*/
	public final void onPause()
	{
		mMapRenderer.onPause();
	}
	/**
	* Must be forwarded from the containing Activity/Fragment.
	*/
	public final void onResume()
	{
		mMapRenderer.onResume();
	}
	/**
	* Must be forwarded from the containing Activity/Fragment.
	* Saving/restoring instance state is not supported yet.
	 *
	*/
	public final void onSaveInstanceState(Bundle outState)
	{
	}

	// NW 120618 added
	public GridRect getBounds() {
		float scale = mMap.getScale();
		float widthM = getWidth()*scale, heightM = getHeight()*scale;
		GridPoint gp = mMap.getCenter();
		return new GridRect(gp.x - widthM/2, gp.y - heightM/2, gp.x + widthM/2, gp.y + widthM/2 );
	}

	// NW 120618 added
	// this will be the smallest lat/lon rectangle which contains the current view, due to the
	// differing projection it will not exactly represent what's on the screen (it will be bigger)
	public void getLonLatBounds(double[] lonLats) {
		GridRect grBounds = getBounds();
		MapProjection proj = MapProjection.getDefault();
		GridPoint[] points = new GridPoint[4];
		double[][] llPoints = new double[4][2];
		points[0] = new GridPoint(grBounds.minX, grBounds.minY);
		points[1] = new GridPoint(grBounds.maxX, grBounds.minY);
		points[2] = new GridPoint(grBounds.maxX, grBounds.maxY);
		points[3] = new GridPoint(grBounds.minX, grBounds.maxY);
		for(int i=0; i<4; i++) {
			proj.fromGridPoint(points[i], llPoints[i]);
		}
		lonLats[0] = llPoints[0][0] < llPoints[3][0] ? llPoints[0][0] : llPoints[3][0];
		lonLats[1] = llPoints[0][1] < llPoints[1][1] ? llPoints[0][1] : llPoints[1][1];
		lonLats[2] = llPoints[2][0] > llPoints[1][0] ? llPoints[2][0] : llPoints[1][0];
		lonLats[3] = llPoints[2][1] > llPoints[3][1] ? llPoints[2][1] : llPoints[3][1];
	}
}
