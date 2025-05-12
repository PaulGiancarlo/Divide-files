package com.pdiaz.astra.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para determinar tipos MIME comunes basados en la extensión del archivo
 */
public class MimeTypeUtils {

    private static final Map<String, String> mimeTypes = new HashMap<>();

    private MimeTypeUtils () {}

    static {
        // Docs
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("doc", "application/msword");
        mimeTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mimeTypes.put("xls", "application/vnd.ms-excel");
        mimeTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
        mimeTypes.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mimeTypes.put("odt", "application/vnd.oasis.opendocument.text");

        // Img
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("bmp", "image/bmp");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("webp", "image/webp");

        // Audio
        mimeTypes.put("mp3", "audio/mpeg");
        mimeTypes.put("wav", "audio/wav");
        mimeTypes.put("ogg", "audio/ogg");
        mimeTypes.put("flac", "audio/flac");

        // Video
        mimeTypes.put("mp4", "video/mp4");
        mimeTypes.put("avi", "video/x-msvideo");
        mimeTypes.put("mkv", "video/x-matroska");
        mimeTypes.put("webm", "video/webm");

        // Compressed
        mimeTypes.put("zip", "application/zip");
        mimeTypes.put("rar", "application/x-rar-compressed");
        mimeTypes.put("7z", "application/x-7z-compressed");
        mimeTypes.put("tar", "application/x-tar");
        mimeTypes.put("gz", "application/gzip");

        // exec
        mimeTypes.put("exe", "application/x-msdownload");
        mimeTypes.put("dll", "application/x-msdownload");
        mimeTypes.put("apk", "application/vnd.android.package-archive");

        // Others
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("xml", "application/xml");
    }

    /**
     * Get Mime
     *
     * @param extension the La extension of the file
     * @return The MIME type or application/octet-stream
     */
    public static String getMimeTypeForExtension(String extension) {
        if (extension == null) {
            return "application/octet-stream";
        }

        String lowerExt = extension.toLowerCase();
        return mimeTypes.getOrDefault(lowerExt, "application/octet-stream");
    }
}
