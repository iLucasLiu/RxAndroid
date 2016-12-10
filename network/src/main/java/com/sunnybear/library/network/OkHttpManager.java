package com.sunnybear.library.network;

import android.content.Context;

import com.sunnybear.library.util.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * OkHttp管理
 * Created by guchenkai on 2016/5/18.
 */
public class OkHttpManager {
    private static Context mContext;
    private static int CONNECT_TIMEOUT_MILLIS;//连接时间超时
    private static int WRITE_TIMEOUT_MILLIS;//写入时间超时
    private static int READ_TIMEOUT_MILLIS;//读取时间超时

//    private static String CERTIFICATE_NAME;//证书名

    private static int[] mCertificates;//证书路径{R.raw.XXX}
    private static String[] mHostUrls;//HostUrl集合

    private volatile static OkHttpManager instance;
    private volatile List<Interceptor> mInterceptors;//应用拦截器组
    private volatile List<Interceptor> mNetworkInterceptors;//网络拦截器组

    private int mCacheSize;
    private String mCacheDirectoryPath;

//    private SSLSocketFactory mSSLSocketFactory;

    private OkHttpManager(int connectTimeout) {
        mContext = NetworkConfiguration.getContext();
        mCacheDirectoryPath = NetworkConfiguration.getNetworkCacheDirectoryPath();
        mCacheSize = NetworkConfiguration.getNetworkCacheSize();
        mInterceptors = new LinkedList<>();
        mNetworkInterceptors = new LinkedList<>();

        CONNECT_TIMEOUT_MILLIS = connectTimeout == -1 ? NetworkConfiguration.CONNECT_TIMEOUT_MILLIS : connectTimeout;
        WRITE_TIMEOUT_MILLIS = connectTimeout == -1 ? NetworkConfiguration.WRITE_TIMEOUT_MILLIS : connectTimeout;
        READ_TIMEOUT_MILLIS = connectTimeout == -1 ? NetworkConfiguration.READ_TIMEOUT_MILLIS : connectTimeout;

//        String certificateName = StringUtils.isEmpty(CERTIFICATE_NAME) ? "" : CERTIFICATE_NAME;
//        CERTIFICATE_NAME = certificateName;
        mCertificates = NetworkConfiguration.getCertificates();
    }

    /**
     * 单例实例
     *
     * @return OkHttpManager实例
     */
    public static OkHttpManager getInstance(int connectTimeout) {
        if (instance == null)
            synchronized (OkHttpManager.class) {
                if (instance == null)
                    instance = new OkHttpManager(connectTimeout);
            }
        return instance;
    }

    /**
     * 单例实例
     *
     * @return OkHttpManager实例
     */
    public static OkHttpManager getInstance() {
        return getInstance(-1);
    }

    /**
     * 添加应用拦截器
     *
     * @param interceptor 拦截器
     * @return OkHttpManager
     */
    public OkHttpManager addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    /**
     * 添加应用拦截器
     *
     * @param interceptors 拦截器组
     */
    public OkHttpManager addInterceptors(List<Interceptor> interceptors) {
        mInterceptors.addAll(interceptors);
        return this;
    }

    /**
     * 添加网络拦截器
     *
     * @param interceptor 拦截器
     * @return OkHttpManager
     */
    public OkHttpManager addNetworkInterceptor(Interceptor interceptor) {
        mNetworkInterceptors.add(interceptor);
        return this;
    }

    /**
     * 添加网络拦截器
     *
     * @param interceptors 拦截器组
     */
    public OkHttpManager addNetworkInterceptors(List<Interceptor> interceptors) {
        mNetworkInterceptors.addAll(interceptors);
        return this;
    }

    /**
     * 获得OkHttp客户端
     *
     * @return OkHttp客户端
     */
    private OkHttpClient generateOkHttpClient(List<Interceptor> interceptors, List<Interceptor> networkInterceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        builder.readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        setCache(builder);
        if (interceptors != null && interceptors.size() > 0)
            builder.interceptors().addAll(interceptors);
        if (networkInterceptors != null && networkInterceptors.size() > 0)
            builder.networkInterceptors().addAll(networkInterceptors);
        /*添加安全证书*/
        if (mCertificates != null && mCertificates.length > 0)
            builder.socketFactory(getSSLSocketFactory(mContext, mCertificates));
        if (mHostUrls != null && mHostUrls.length > 0)
            builder.hostnameVerifier(getHostnameVerifier(mHostUrls));
        /*if (!StringUtils.isEmpty(CERTIFICATE_NAME)) {
            try {
                InputStream inputStream = ResourcesUtils.getAssets(NetworkConfiguration.getContext()).open(CERTIFICATE_NAME);
                X509TrustManager trustManager = trustManagerForCertificates(inputStream);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                mSSLSocketFactory = sslContext.getSocketFactory();
                builder.sslSocketFactory(mSSLSocketFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        return builder.build();
    }

    /**
     * 获得缓存器
     *
     * @param builder OkHttpClient建造器
     */
    private void setCache(OkHttpClient.Builder builder) {
        File cacheDirectory = new File(mCacheDirectoryPath);
        FileUtils.createMkdirs(cacheDirectory);
        builder.cache(new Cache(cacheDirectory, mCacheSize));
    }

    /**
     * 绑定证书
     *
     * @param context      context
     * @param certificates 证书路径{R.raw.XXX}
     */
    private SSLSocketFactory getSSLSocketFactory(Context context, int[] certificates) {
        if (context == null) throw new NullPointerException("context == null");
        SSLContext sslContext = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            for (int i = 0; i < certificates.length; i++) {
                InputStream certificate = context.getResources().openRawResource(certificates[i]);
                keyStore.setCertificateEntry(String.valueOf(i), certificateFactory.generateCertificate(certificate));
                if (certificate != null) certificate.close();
            }
            sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }

    /**
     * 构建HostnameVerifier
     *
     * @param hostUrls hostUrl集合
     */
    private HostnameVerifier getHostnameVerifier(String[] hostUrls) {
        return (hostname, sslSession) -> {
            boolean ret = false;
            for (String host : hostUrls) {
                if (host.equalsIgnoreCase(hostname)) ret = true;
            }
            return ret;
        };
    }

    /**
     * 构建OkHttpClient
     *
     * @return OkHttpClient
     */
    public OkHttpClient build() {
        return generateOkHttpClient(mInterceptors, mNetworkInterceptors);
    }

    /**
     * 以流的方式添加信任证书
     *
     * @param in 证书流
     * @return TrustManager
     */
    /*private X509TrustManager trustManagerForCertificates(InputStream in)
            throws CertificateException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> mCertificates = factory.generateCertificates(in);
        if (mCertificates.isEmpty())
            throw new IllegalArgumentException("expected non-empty set of trusted mCertificates");
        char[] password = "password".toCharArray();
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : mCertificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager))
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        return (X509TrustManager) trustManagers[0];
    }*/

    /**
     * 添加password
     *
     * @param password password字符串
     * @return KeyStore密码
     */
    /*private KeyStore newEmptyKeyStore(char[] password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null;
            keyStore.load(in, password);
            return keyStore;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new AssertionError(e);
        }
    }*/
}
