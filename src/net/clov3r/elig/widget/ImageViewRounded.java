package net.clov3r.elig.widget;

import net.clov3r.elig.util.ImageHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewRounded extends ImageView {

	public ImageViewRounded(Context context) {
		super(context);
	}

	public ImageViewRounded(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageViewRounded(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		BitmapDrawable drawable = (BitmapDrawable) getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}

		Bitmap fullSizeBitmap = drawable.getBitmap();
		if (fullSizeBitmap != null) {
			Bitmap roundBitmap = ImageHelper
					.getRoundedCornerBitmap(fullSizeBitmap);
			canvas.drawBitmap(roundBitmap, (getMeasuredWidth() / 2)
					- (fullSizeBitmap.getWidth() / 2),
					(getMeasuredHeight() / 2)
							- (fullSizeBitmap.getHeight() / 2), null);
		}
	}

}
