package com.leagueofcoding.api.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * SlugUtils - Generate SEO-friendly slugs from titles.
 *
 * @author dao-nguyenminh
 */
public class SlugUtils {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    /**
     * Generate slug from title.
     * <p>
     * Example: "Two Sum Problem!" → "two-sum-problem"
     */
    public static String generateSlug(String title) {
        String noWhitespace = WHITESPACE.matcher(title).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH)
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Generate unique slug by appending number if needed.
     */
    public static String generateUniqueSlug(String baseSlug, int attempt) {
        return attempt == 0 ? baseSlug : baseSlug + "-" + attempt;
    }

    private SlugUtils() {
        // Utility class
    }
}