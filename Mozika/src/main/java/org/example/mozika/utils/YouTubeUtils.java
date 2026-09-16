package org.example.mozika.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YouTubeUtils {

    private static final Pattern VIDEO_ID_PATTERN = Pattern.compile(
            "(?:youtube\\.com/(?:watch\\?v=|embed/|shorts/)|youtu\\.be/)([A-Za-z0-9_-]{11})"
    );

    private YouTubeUtils() {}

    public static boolean isYouTubeUrl(String url) {
        if (url == null || url.isBlank()) return false;
        return url.contains("youtube.com") || url.contains("youtu.be");
    }

    public static String extractVideoId(String url) {
        if (url == null) return null;
        Matcher m = VIDEO_ID_PATTERN.matcher(url);
        return m.find() ? m.group(1) : null;
    }

    public static String getEmbedUrl(String videoId) {
        return "https://www.youtube.com/embed/" + videoId + "?rel=0";
    }
}
