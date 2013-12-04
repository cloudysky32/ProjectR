package com.projectr.code.utils.balloon;

import java.lang.reflect.*;
import java.util.*;

import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.*;
import com.projectr.code.*;

public abstract class BalloonItemizedOverlay<Item> extends ItemizedOverlay<OverlayItem> {

	private MapView mapView;
	private BalloonOverlayView balloonView;
	private View clickRegion;
	private int viewOffset;
	final MapController mc;

	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker);
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
	}

	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}

	protected boolean onBalloonTap(int index) {
		return false;
	}
	
	@Override
	protected boolean onTap(int index) {

		boolean isRecycled;
		final int thisIndex;
		GeoPoint point;

		thisIndex = index;
		point = createItem(index).getPoint();

		if (balloonView == null) {
			balloonView = new BalloonOverlayView(mapView.getContext(), viewOffset);
			clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
			isRecycled = false;
		} else {
			isRecycled = true;
		}
		
		mapView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				balloonView.setVisibility(View.GONE);
				return false;
			}
		});
		
		balloonView.setVisibility(View.GONE);

		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}

		balloonView.setData(createItem(index));

		MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,	MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;

		setBalloonTouchListener(thisIndex);

		balloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}

		mc.animateTo(point);
		mc.setZoom(18);

		return true;
	}
	
	public int getVisivility(){
		return balloonView.getVisibility();
	}

	public void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
	}

	private void hideOtherBalloons(List<Overlay> overlays) {
		for (Overlay overlay : overlays) {
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}
	}

	private void setBalloonTouchListener(final int thisIndex) {
		try {
			Method m = this.getClass().getDeclaredMethod("onBalloonTap", int.class);

			clickRegion.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {

					View l = ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
					Drawable d = l.getBackground();

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						int[] states = { android.R.attr.state_pressed };
						if (d.setState(states)) {
							d.invalidateSelf();
						}
						return true;
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						int newStates[] = {};
						if (d.setState(newStates)) {
							d.invalidateSelf();
						}
						// call overridden method
						onBalloonTap(thisIndex);
						return true;
					} else {
						return false;
					}
				}
			});
		} catch (SecurityException e) {
			Log.e("BalloonItemizedOverlay",	"setBalloonTouchListener reflection SecurityException");
			return;
		} catch (NoSuchMethodException e) {
			// method not overridden - do nothing
			return;
		}
	}
}
