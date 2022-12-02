package dev.lastknell.core.proxy;

import java.net.InetSocketAddress;

/**
 * Represents a proxy server with optional authentication.
 */
public class Proxy {

    /**
     * The address of the proxy server.
     */
    private final InetSocketAddress address;
    /**
     * The email address to use for authentication, if required.
     */
    private final String email;
    /**
     * The password to use for authentication, if required.
     */
    private final String pw;
    /**
     * Indicates whether the proxy requires authentication.
     */
    private final boolean requiresAuth;

    /**
     * Creates a new proxy with the given address.
     *
     * @param address the address of the proxy server
     */
    public Proxy(InetSocketAddress address) {
        this.address = address;
        this.email = null;
        this.pw = null;
        this.requiresAuth = false;
    }

    /**
     * Creates a new proxy with the given address and authentication details.
     *
     * @param address the address of the proxy server
     * @param email   the email address to use for authentication
     * @param pw      the password to use for authentication
     */
    public Proxy(InetSocketAddress address, String email, String pw) {
        this.address = address;
        this.email = email;
        this.pw = pw;
        this.requiresAuth = true;
    }

    /**
     * Returns the address of the proxy server.
     *
     * @return the address of the proxy server
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Returns the email address to use for authentication, if required.
     *
     * @return the email address to use for authentication, or {@code null} if not required
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password to use for authentication, if required.
     *
     * @return the password to use for authentication, or {@code null} if not required
     */
    public String getPassword() {
        return pw;
    }

    /**
     * Indicates whether the proxy requires authentication.
     *
     * @return {@code true} if the proxy requires authentication, {@code false} otherwise
     */
    public boolean requiresAuthentication() {
        return requiresAuth;
    }

    @Override
    public String toString() {
        return "Proxy{" +
                "address=" + address +
                ", email='" + email + '\'' +
                ", pw='" + pw + '\'' +
                ", requiresAuth=" + requiresAuth +
                '}';
    }
}