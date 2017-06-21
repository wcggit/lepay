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

    public static final String ALIAPPID = "2016060701491886";

    public static final String TEST_APPID = "wx0d04fde568a3832f";

    public static final String WEI_XIN_ROOT_URL = "http://www.lepluslife.com";
    public static final String WEI_XIN_TEST_ROOT_URL = "http://www.lepluslife.com/subTest";

    public static final String LE_PAY_MCH_ID = "1363466902";
    public static final String LE_PAY_APP_ID = "wx16edfa0dda02edd5";

    public static final String MSG_SENDER = "214"; //银联商务分配的渠道号
    public static final String EVENT_NO = "666"; //银联商务活动号

    public static final String FUYOU_PAY_URL = "http://weixinpay.fuiou.com/wxPreCreate"; //富友公众号预支付请求地址
    public static final String FUYOU_ALiPAY_URL = "http://weixinpay.fuiou.com/preCreate"; //富友公众号预支付请求地址
    public static final String FUYOU_PRE_URL = "http://weixinpay.fuiou.com/preCreate"; //面对面收钱 生产二维码 用户主扫
    public static final String FUYOU_SM_URL = "http://weixinpay.fuiou.com/preCreate"; //面对面收钱 商户扫用户的支付宝或者微信的付款码
    public static final String FUYOU_QUERY_URL = "http://weixinpay.fuiou.com/commonQuery"; //富友查询订单状态地址
    public static final String FUYOU_INS_CD = "08M0063365"; //富友分配的机构号
    public static final String FUYOU_PRI_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKrADjicUSALdyt/LxqFkNTury0MF92O84xdlVfvGbTyXk+PeJ3N2/LYcIH4HxfHVtEibCBCu4gTsM5hpp6tpsBfBYwnwxY0peSEOZ9EQmz3+9gCThG12m1yNjLYeraU/Gx7hqPX4/doS8urtR0Asum+FV5W3kAgjcukIsGn0kLnAgMBAAECgYAcbXMwq526Bw6lGDygXsJZIQ/bIRtMEMOth9sYU79k58EZ39oF88L9sFky2jta+x4SHXgA+vs70YNrKMjTvDd5o5aTQZpB48TKpJ8c206Up/Gm50HwWjqJHgC1tOnIWRq8qF7AU3zfKjaishz1nAt58z4PEMW+TzWHGe4v9vxz4QJBAOhlyj6QrtIuZ4dvmD/B6itvahTLQKY/WfdXRqbf8kPemxkcWscQT7+bnK+DrfDZhtRo2i7q6POvun0dIxOn3JcCQQC8F3eTzfj4HlIil9RsJjpVlg15rhc7ydQwVZRh2wZR14GV8+yGFogHh1Ba02EB+xQn03T0zoCce5BbDh2H1oYxAkAhDJi+XQT/junaMNyN9J3An4+OdXk0Kz44FolNoftp+3ZDE+008fTlYtPdgfRyk/zAqEie83k9bngu4r3iRbTxAkAfIv9fj2xUnqhYI6w9jwJ/IozuhLxB4IJo0fHzVQ+xwqwoB64y8E3qeSL7NhzL+CV5Bk9JK1otDWNzP13yG7gxAkBDabCAaVQKnhaSQCkUOE3YUFBbSNfkLElctxla/mMoxSJWU/J5ZCXQxd2LnuZmRfK5Txg0rECynJYnvbGldICp";

    public static final String CARD_CHECK_KEY = "8001808eb38443d35671c7c6f8c7ddc0";
    public static final String CARD_CHECK_URL = "http://e.apix.cn/apixcredit/bankcardinfo/bankcardinfo?cardno=";


    private Constants() {
    }
}
