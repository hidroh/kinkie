package com.angelhack.wheresapp.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * Utils class to facilitate images downloading from the web
 * 
 * @author Dang Chien
 *
 */
public class BitmapUtils {
	
	private static final String TAG = BitmapUtils.class.getSimpleName();

	private static final int DEFAULT_IMAGE_HEIGHT = 600;
	private static final int DEFAULT_IMAGE_WIDTH = 800;

    // TODO: for concurrent connections, DefaultHttpClient isn't great, consider other options
    // that still allow for sharing resources across bitmap fetches.

    public static interface OnFetchCompleteListener {
        public void onFetchComplete(Object cookie, Bitmap result);
    }

    /**
     * Only call this method from the main (UI) thread. The {@link OnFetchCompleteListener} callback
     * be invoked on the UI thread, but image fetching will be done in an {@link AsyncTask}.
     */
    public static void fetchImage(final Context context, final String url,
            final OnFetchCompleteListener callback) {
        fetchImage(context, url, null, null, callback);
    }

    /**
     * Only call this method from the main (UI) thread. The {@link OnFetchCompleteListener} callback
     * be invoked on the UI thread, but image fetching will be done in an {@link AsyncTask}.
     *
     * @param cookie An arbitrary object that will be passed to the callback.
     */
    private static void fetchImage(final Context context, final String url,
            final BitmapFactory.Options decodeOptions,
            final Object cookie, final OnFetchCompleteListener callback) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... args) {
                final String url = args[0];
                if (TextUtils.isEmpty(url)) {
                    return null;
                }

                // First compute the cache key and cache file path for this URL
                File cacheFile = getCachedImage(context, url);

                if (cacheFile != null && cacheFile.exists()) {
                    Bitmap cachedBitmap = BitmapFactory.decodeFile(
                            cacheFile.toString(), decodeOptions);
                    if (cachedBitmap != null) {
                        return cachedBitmap;
                    }
                }

                // If there is no cached image, download it from server.
                try {
                	final HttpParams params = new BasicHttpParams();
    				HttpConnectionParams.setConnectionTimeout(params, (int) (10 * DateUtils.SECOND_IN_MILLIS));
    				HttpConnectionParams.setSoTimeout(params, (int) (10 * DateUtils.SECOND_IN_MILLIS));
    				HttpConnectionParams.setSocketBufferSize(params, 8192);

                    // TODO: check for HTTP caching headers
                	final HttpClient httpClient = new DefaultHttpClient(params);
                    final HttpResponse resp = httpClient.execute(new HttpGet(url));
                    final HttpEntity entity = resp.getEntity();

                    final int statusCode = resp.getStatusLine().getStatusCode();
                    if (statusCode != HttpStatus.SC_OK || entity == null) {
                        return null;
                    }

                    final byte[] respBytes = EntityUtils.toByteArray(entity);

                    // Write response bytes to cache.
                    if (cacheFile != null) {
                        try {
                            cacheFile.getParentFile().mkdirs();
                            cacheFile.createNewFile();
                            FileOutputStream fos = new FileOutputStream(cacheFile);
                            fos.write(respBytes);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.w(TAG, "Error writing to bitmap cache: " + cacheFile.toString(), e);
                        } catch (IOException e) {
                            Log.w(TAG, "Error writing to bitmap cache: " + cacheFile.toString(), e);
                        }
                    }

                    // Decode the bytes and return the bitmap.
                    return BitmapFactory.decodeByteArray(respBytes, 0, respBytes.length,
                            decodeOptions);
                } catch (Exception e) {
                    Log.w(TAG, "Problem while loading image: " + e.toString(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                callback.onFetchComplete(cookie, result);
            }
        }.execute(url);
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

	private static File getCachedImage(final Context context, final String url) {
		File cacheFile = null;
		try {
		    MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		    mDigest.update(url.getBytes());
		    final String cacheKey = bytesToHexString(mDigest.digest());
		    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
		        cacheFile = new File(
		                Environment.getExternalStorageDirectory()
		                        + File.separator + "Android"
		                        + File.separator + "data"
		                        + File.separator + ((context == null) ? "temp" : context.getPackageName())
		                        + File.separator + "cache"
		                        + File.separator + "bitmap_" + cacheKey + ".tmp");
		    }
		} catch (NoSuchAlgorithmException e) {
			Log.w(TAG, "Oh well, SHA-1 not available (weird), don't cache bitmaps.");
		}
		return cacheFile;
	}
	
	/**
	 * Uses to modify the url to get V550 image
	 * 
	 * @param input V350 image url
	 * @return the url for v550 image
	 */
    public static String getV550Image(String v350) {
    	Pattern pattern = Pattern.compile("V350");
    	Matcher matcher = pattern.matcher(v350);
    	StringBuffer buffer = new StringBuffer();
    	while (matcher.find()) {
    		matcher.appendReplacement(buffer, "V550");
		}
    	matcher.appendTail(buffer);
    	return buffer.toString();
    }
    
	/**
	 * Uses to modify the url to get V120B image
	 * 
	 * @param v60B image url
	 * @return the url for V120B image
	 */
	public static String getV120BImage(String v60B) {
		Pattern pattern = Pattern.compile("V60B");
		Matcher matcher = pattern.matcher(v60B);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, "V120B");
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	/**
	 * Uses to modify the url to get V120 image
	 * 
	 * @param v60 image url
	 * @return the url for V120 image
	 */
	public static String getV120Image(String v60) {
		Pattern pattern = Pattern.compile("V60");
		Matcher matcher = pattern.matcher(v60);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, "V120");
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	/**
	 * Down sampling image to avoid out of memory error
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static Bitmap decodeAndDownSamplingImage(File file) throws IOException {
		// Options to take only the image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;

		// Read the input stream that contains the image information
		FileInputStream fis = new FileInputStream(file);
		BitmapFactory.decodeStream(fis, null, o);
		fis.close();

		Log.v(TAG, "Before scaling," + " width:" + o.outWidth + " height:" + o.outHeight
				+ " mimeType:" + o.outMimeType + " fileSize:" + file.length() / 1024 + " kB");
		
		// Compute the required down-sample rate (standard image size is 800x600)
		int scale = 1;
		if (o.outHeight > DEFAULT_IMAGE_HEIGHT || o.outWidth > DEFAULT_IMAGE_WIDTH) {
			scale = (int) Math.pow(2, (int) Math.round(Math.log(DEFAULT_IMAGE_WIDTH / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
		}

		// Options that decode the real image with the down-sampling rate
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		
		// Decode the down-sampled image
		FileInputStream fis2 = new FileInputStream(file);
		Bitmap b = BitmapFactory.decodeStream(fis2, null, o2);
		fis2.close();
		return b;
	}
}
