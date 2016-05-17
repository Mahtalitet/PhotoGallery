package com.learning.photogallery.gallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryContent {

    public static final List<GalleryItem> ITEMS = new ArrayList<GalleryItem>();
    public static final Map<String, GalleryItem> ITEM_MAP = new HashMap<String, GalleryItem>();

    private static final int COUNT = 25;

    static {
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(GalleryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.mId, item);
    }

    private static GalleryItem createDummyItem(int position) {
        return new GalleryItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore mCaption information here.");
        }
        return builder.toString();
    }

}
