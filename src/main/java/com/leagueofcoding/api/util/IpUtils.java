package com.leagueofcoding.api.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IpUtils - Utility để extract client IP address.
 *
 * @author dao-nguyenminh
 */
public class IpUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * Get client IP address từ HTTP request.
     *
     * <p>Check các headers thường dùng khi có proxy/load balancer.
     *
     * @param request HTTP request
     * @return client IP address
     */
    public static String getClientIp(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For có thể chứa nhiều IPs: "client, proxy1, proxy2"
                // Lấy IP đầu tiên (client IP)
                int commaIndex = ip.indexOf(',');
                if (commaIndex > 0) {
                    ip = ip.substring(0, commaIndex).trim();
                }
                return ip;
            }
        }

        // Fallback to remote address
        return request.getRemoteAddr();
    }

    private IpUtils() {
        // Utility class - private constructor
    }
}