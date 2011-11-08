package net.clov3r.elig.util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class ImageManager {

	private static final String LOG =ImageManager.class.getName();
	// Just using a hashmap for the cache. SoftReferences would
	private HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
	private File cacheDir;
	private ImageQueue imageQueue = new ImageQueue();
	private Thread imageLoaderThread = new Thread(new ImageQueueManager());
	private Context context;
	private boolean roundedCorner;

	public ImageManager(Context context) {
		imageLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		this.context = context;
		// Find the dir to save cached images
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, "data/elig/cache");
		} else{
			cacheDir = context.getCacheDir();
		}
		Log.d(LOG,cacheDir.toString());
		if (!cacheDir.exists()) {
			Log.d("LPFeed", cacheDir.toString() + " " + cacheDir.mkdirs());
		}

	}
	
	public void displayImage(String url, ImageView imageView,boolean roundedCorner) {
		this.roundedCorner=roundedCorner;
		displayImage(url,imageView);
	}


	public void displayImage(String url, ImageView imageView) {
		if (imageMap.containsKey(url)) {
			imageView.setImageBitmap(imageMap.get(url));

		} else {
			queueImage(url, imageView);
			imageView.setImageDrawable(null);
		}
	}

	private void queueImage(String url, ImageView imageView) {
		// This ImageView might have been used for other images, so we clear
		// the queue of old tasks before starting.

		imageQueue.Clean(imageView);
		ImageRef p = new ImageRef(url, imageView);

		synchronized (imageQueue.imageRefs) {
			imageQueue.imageRefs.push(p);
			imageQueue.imageRefs.notifyAll();
		}

		// Start thread if it's not started yet
		if (imageLoaderThread.getState() == Thread.State.NEW) {
			Log.d(LOG, "Going to get from the url " + url);

			imageLoaderThread.start();
		}
	}

	private Bitmap getBitmap(String url) {

		String filename = String.valueOf(url.hashCode());

		File f = new File(cacheDir, filename);

		// Is the bitmap in our cache?
		Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
		if (bitmap != null)
			return bitmap;

		// Nope, have to download it
		try {
			bitmap = BitmapFactory.decodeStream(new URL(url).openConnection()
					.getInputStream());

			// save bitmap to cache for later
			writeFile(bitmap, f);

			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private void writeFile(Bitmap bmp, File f) {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(f);
			
			bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
			}
		}
	}

	/** Classes **/

	private class ImageRef {
		public String url;
		public ImageView imageView;

		public ImageRef(String u, ImageView i) {
			url = u;
			imageView = i;
		}

	}

	// stores list of images to download
	private class ImageQueue {
		private Stack<ImageRef> imageRefs = new Stack<ImageRef>();

		// removes all instances of this ImageView
		public void Clean(ImageView view) {

			for (int i = 0; i < imageRefs.size();) {
				if (imageRefs.get(i).imageView == view)
					imageRefs.remove(i);
				else
					++i;
			}
		}
	}

	private class ImageQueueManager implements Runnable {
		public void run() {
			try {
				while (true) {
					// Thread waits until there are images in the
					// queue to be retrieved

					if (imageQueue.imageRefs.size() == 0) {
						synchronized (imageQueue.imageRefs) {
							imageQueue.imageRefs.wait();
						}
					}

					// When we have images to be loaded
					if (imageQueue.imageRefs.size() != 0) {
						ImageRef imageToLoad;

						synchronized (imageQueue.imageRefs) {
							imageToLoad = imageQueue.imageRefs.pop();
						}

						Bitmap bmp = getBitmap(imageToLoad.url);

						imageMap.put(imageToLoad.url, bmp);
						Object tag = imageToLoad.imageView.getTag();

						// Make sure we have the right view - thread safety
						// defender

						if (tag != null
								&& ((String) tag).equals(imageToLoad.url)) {
							Activity a = (Activity) imageToLoad.imageView
									.getContext();
							BitmapDisplayer bmpDisplayer = null;

							bmpDisplayer = new BitmapDisplayer(bmp,
									imageToLoad.imageView);

							a.runOnUiThread(bmpDisplayer);
						}
					}

					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
			}
		}
	}

	// Used to display bitmap in the UI thread
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		Drawable drawable;

		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}

		public BitmapDisplayer(Drawable drawable, ImageView i) {
			this.drawable = drawable;
			imageView = i;
		}

		public void run() {

			if (bitmap != null){
				
				imageView.setImageBitmap(bitmap);
			}
			else {
				if (drawable != null)
					imageView.setImageDrawable(drawable);
				else
					imageView.setImageDrawable(null);
			}

		}
	}
}
