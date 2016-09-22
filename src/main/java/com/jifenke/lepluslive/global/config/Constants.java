package com.jifenke.lepluslive.global.config;

/**
 * Application constants.
 */
public final class Constants {

    // Spring profile for development, production and "fast", see http://jhipster.github.io/profiles.html
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SPRING_PROFILE_FAST = "fast";
    // Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
    public static final String SPRING_PROFILE_CLOUD = "cloud";
    // Spring profile used when deploying to Heroku
    public static final String SPRING_PROFILE_HEROKU = "heroku";

    public static final String SYSTEM_ACCOUNT = "system";


    public static final String APPID = "wxe2190d22ce025e4f";
    public static final String TEST_APPID = "wx0d04fde568a3832f";

    public static final String WEI_XIN_ROOT_URL = "http://www.lepluslife.com";
    public static final String WEI_XIN_TEST_ROOT_URL = "http://www.lepluslife.com/subTest";

    public static final String LE_PAY_MCH_ID = "1363466902";
    public static final String LE_PAY_APP_ID = "wx16edfa0dda02edd5";



    private Constants() {
    }
}
