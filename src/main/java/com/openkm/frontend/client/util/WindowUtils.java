package com.openkm.frontend.client.util;

/**
 * @author jllort
 */
public class WindowUtils {

	public static Location getLocation() {
		Location result = new Location();
		result.setHash(getHash());
		result.setHost(getHost());
		result.setHostName(getHostName());
		result.setHref(getHref());
		result.setPath(getPath());
		result.setPort(getPort());
		result.setProtocol(getProtocol());
		result.setQueryString(getQueryString());
		return result;
	}

	private static native String getQueryString() /*-{
        return $wnd.location.search;
    }-*/;

	private static native String getProtocol() /*-{
        return $wnd.location.protocol;
    }-*/;

	private static native String getPort() /*-{
        return $wnd.location.port;
    }-*/;

	private static native String getPath() /*-{
        return $wnd.location.pathname;
    }-*/;

	private static native String getHref() /*-{
        return $wnd.location.href;
    }-*/;

	private static native String getHostName() /*-{
        return $wnd.location.hostname;
    }-*/;

	private static native String getHost() /*-{
        return $wnd.location.host;
    }-*/;

	private static native String getHash() /*-{
        return $wnd.location.hash;
    }-*/;
}
