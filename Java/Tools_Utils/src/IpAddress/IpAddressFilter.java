package IpAddress;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpAddressFilter {
    public static void main(String[] args) {
        String ipAddress = "192.168.0.199";
        String ipv6Address = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        String ipAddressWithPort = "192.168.0.192:8080";
        String ipv6AddressWithPort = "[2001:db8::1]:8080";

        System.out.println("Is valid IPv4: " + getIpType(ipAddress));
        System.out.println("Is valid IPv6: " + getIpType(ipv6Address));
        System.out.println("Is valid IPv4 + Port: " + getIpType(ipAddressWithPort));
        System.out.println("Is valid IPv6 + Port: " + getIpType(ipv6AddressWithPort));

        System.out.println("Is valid IPv4 + Port: " + splitIpAndPort(ipAddressWithPort));
        System.out.println("Is valid IPv6 + Port: " + splitIpAndPort(ipv6AddressWithPort));
    }

    /**
     * 是否是ipv4
     * @param ipv4Address ipv4
     * @return
     */
    public static boolean isValidIPv4(String ipv4Address) {
        String ipv4Pattern = "^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$";
        return ipv4Address.matches(ipv4Pattern);
    }


    /**
     * 是否ipv6
     * @param ipv6Address 检测
     * @return
     */
    public static boolean isValidIPv6(String ipv6Address) {
        String ipv6Pattern = "^\\[?([0-9a-fA-F]{0,4}:){1,7}([0-9a-fA-F]{0,4})?\\]?$";
        return ipv6Address.matches(ipv6Pattern);
    }

    /**
     * 是否ipv4+port
     * @param ipWithPort
     * @return
     */
    public static boolean isValidIPv4AddressWithPort(String ipWithPort) {
        String ipWithPortPattern = "^(\\d{1,3}(?:\\.\\d{1,3}){3}):([0-9]{1,5})$";
        return ipWithPort.matches(ipWithPortPattern);
    }

    /**
     * 是否ipv6+port
     * @param ipWithPort
     * @return
     */
    public static boolean isValidIPv6AddressWithPort(String ipWithPort) {
        String ipWithPortPattern = "^(?:\\[(.+?)\\])?(?::(\\d{1,5}))?$";
        return ipWithPort.matches(ipWithPortPattern);
    }

    /**
     * 检测ip是哪一类 注意：ipv6需要中括号！
     * // "IPv4":1/"IPv6":2/"IPv4 with Port":3/"IPv6 with Port":4/"Invalid IP":5
     * @param ip 检测
     * @return
     */
    public static int getIpType(String ip) {
        if (isValidIPv4(ip)) {
            return 1;
        } else if (isValidIPv6(ip)) {
            return 2;
        } else if (isValidIPv4AddressWithPort(ip)) {
            return 3;
        } else if (isValidIPv6AddressWithPort(ip)) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 返回ip和端口
     * @param ipAddressWithPort
     * @return
     */
    public static Pair<String, String> splitIpAndPort(String ipAddressWithPort) {
        // 判断是 IPv4 还是 IPv6
        if (getIpType(ipAddressWithPort) == 4) {
            // IPv6 地址
            String[] parts = ipAddressWithPort.split("]:");
            return new Pair<>(parts[0].substring(1), parts[1]);
        } else if (getIpType(ipAddressWithPort) ==3 ) {
            // IPv4 地址
            String[] parts = ipAddressWithPort.split(":");
            return new Pair<>(parts[0], parts[1]);
        } else {
            return null;
        }
    }
}
