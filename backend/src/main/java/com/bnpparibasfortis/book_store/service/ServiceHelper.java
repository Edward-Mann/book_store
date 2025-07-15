package com.bnpparibasfortis.book_store.service;

/**
 * Utility class providing helper methods for service layer operations.
 * This class contains static utility methods that can be used across different services.
 */
public final class ServiceHelper {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated
     */
    private ServiceHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Capitalizes the first letter of each word in the given string.
     * Words are separated by whitespace characters. Each word's first character
     * is converted to uppercase, while the remaining characters are converted to lowercase.
     *
     * @param str the string to capitalize, may be null or empty
     * @return the capitalized string, or the original string if null or empty
     */
    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
