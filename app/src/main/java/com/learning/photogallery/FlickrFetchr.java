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

public class FlickrFetchr {

    public static final String TAG = "FlickrFetchr";
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
    private static final int FIRST_PAGE = 1;

    private int currentPage = FIRST_PAGE;
    private int pages = 0;

    public enum FetchingType {RECENT, SEARCH}

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

        if (currentPage == pages) return items;
        try {
            Log.i(TAG, "Current URL: "+url);
            String xmlString = getUrl(url);
//            Log.i(TAG, "Received xml: "+xmlString);
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

        }finally {
            return items;
        }
    }

    public ArrayList<GalleryItem> fetchItems() {
       if (currentPage == 1) {
           return fetchItemsLast();
       } else  {
           return fetchItemsByPage(currentPage);
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

    private void parseItems (ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException{
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (currentPage == 1
                    && eventType == XmlPullParser.START_TAG
                    && XML_PHOTOS.equals(parser.getName())) {
                pages = parsePages(parser);
            }

            if (eventType == XmlPullParser.START_TAG
                    && XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
//                Log.i(TAG, "id: "+id);
                String caption = parser.getAttributeValue(null, "title");
//                Log.i(TAG, "caption: "+caption);
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
//                Log.i(TAG, "smallUrl: "+smallUrl);

                items.add(new GalleryItem(id, smallUrl, caption));
            }

            eventType = parser.next();
        }

        Log.i(TAG, "Current page: "+getCurrentPage());
    }

    private int parsePages(XmlPullParser parser) {
        return Integer.parseInt(parser.getAttributeValue(null, "total"));
    }

    public void increaseCurrentPageAtOne() {
        if (currentPage != pages) currentPage++;
    }

    public void decreaseCurrentPageAtOne() {
        if (currentPage > 0) currentPage--;
    }

    public void resetPages() {
        currentPage = FIRST_PAGE;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
