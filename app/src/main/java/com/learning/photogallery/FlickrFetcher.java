package com.learning.photogallery;


import android.net.Uri;
import android.util.Log;

import com.learning.photogallery.gallery.GalleryItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FlickrFetcher {

    public static final String TAG = "FlickrFetcher";
    public static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "d71c15eae0df48451eb9114e718b952e";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_GET_SEARCH = "flickr.photos.search";
    private static final String PARAM_EXTRAS = "extras";
    private static final String EXTRA_SMALL_URL = "url_s";
    private static final String PARAM_TEXT = "text";
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_PAGE = "page";

    private static final String XML_PHOTO = "photo";
    private static final String XML_PHOTOS = "photos";

    private int mPages = 0;

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<GalleryItem> downloadGalleyItems(String url) {
        ArrayList<GalleryItem> items = new ArrayList<>();

        try {
            Log.i(TAG, "Current URL: "+url);
            String xmlString = getUrl(url);
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));

            parseItems(items, xmlPullParser);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to fetch items", e);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to parse items", e);

        } finally {
            return items;
        }
    }

    public ArrayList<GalleryItem> fetchItemsLast() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter(PARAM_METHOD, METHOD_GET_RECENT)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .build().toString();

        return downloadGalleyItems(url);
    }

    public ArrayList<GalleryItem> fetchItemsByPage(int page) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter(PARAM_METHOD, METHOD_GET_RECENT)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build().toString();

        return downloadGalleyItems(url);
    }

    public ArrayList<GalleryItem> search(String searchText) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter(PARAM_METHOD, METHOD_GET_SEARCH)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_TEXT, searchText)
                .build().toString();

        return downloadGalleyItems(url);
    }

    public ArrayList<GalleryItem> searchByPage(String searchText, int page) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter(PARAM_METHOD, METHOD_GET_SEARCH)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_TEXT, searchText)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build().toString();

        return downloadGalleyItems(url);
    }

    private void parseItems (ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException{
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_TAG
                    && XML_PHOTOS.equals(parser.getName())) {
                String allPages = parser.getAttributeValue(null, "pages");
                Log.i(TAG, "Parsed pages: "+allPages);
                mPages = Integer.parseInt(allPages);
                Log.i(TAG, "Get pages:"+mPages);
            }

            if (eventType == XmlPullParser.START_TAG
                    && XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);

                items.add(new GalleryItem(id, smallUrl, caption));
            }

            eventType = parser.next();
        }
    }

    public int getAllPages() {
        return mPages;
    }
}
