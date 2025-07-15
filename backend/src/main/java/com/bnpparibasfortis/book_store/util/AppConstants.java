package com.bnpparibasfortis.book_store.util;

public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String LOGIN_URL = "/api/auth/login";
    public static final String LOGOUT_URL = "/api/auth/logout";
    public static final String REGISTER_URL = "/api/auth/register";
    public static final String ADMIN = "ADMIN";


}

