package com.leagueofcoding.api.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * SlugUtils - Utility class cho generating URL-friendly slugs.
 *
 * @author dao-nguyenminh
 */
@Slf4j
@Component
public class SlugUtils {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    /**
     * Generate slug from title.
     */
    public static String generateSlug(String title) {
        log.debug("Generating slug for title: {}", title);

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        // Normalize to NFD to remove accents
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);

        // Remove non-ASCII characters
        String withoutAccents = normalized.replaceAll("[^\\p{ASCII}]", "");

        // Convert to lowercase and replace spaces with hyphens
        String slug = WHITESPACE.matcher(withoutAccents.toLowerCase())
                .replaceAll("-");

        // Remove non-word characters except hyphens
        slug = NON_LATIN.matcher(slug)
                .replaceAll("");

        // Remove leading/trailing hyphens
        slug = slug.replaceAll("^-|-$", "");

        log.debug("Generated slug: {}", slug);
        return slug;
    }

    /**
     * Generate unique slug with attempt number.
     */
    public static String generateUniqueSlug(String baseSlug, int attempt) {
        log.debug("Generating unique slug: {} (attempt: {})", baseSlug, attempt);

        if (attempt == 1) {
            return baseSlug;
        }

        return baseSlug + "-" + attempt;
    }
}