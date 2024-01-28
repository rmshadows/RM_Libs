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
    }

    public static boolean isValidIPv4(String ipv4Address) {
        String ipv4Pattern = "^(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$";
        return ipv4Address.matches(ipv4Pattern);
    }

    public static boolean isValidIPv6(String ipv6Address) {
        String ipv6Pattern = "^\\[?([0-9a-fA-F]{0,4}:){1,7}([0-9a-fA-F]{0,4})?\\]?$";
        return ipv6Address.matches(ipv6Pattern);
    }

    public static boolean isValidIPv4AddressWithPort(String ipWithPort) {
        String ipWithPortPattern = "^(\\d{1,3}(?:\\.\\d{1,3}){3}):([0-9]{1,5})$";
        return ipWithPort.matches(ipWithPortPattern);
    }

    public static boolean isValidIPv6AddressWithPort(String ipWithPort) {
        String ipWithPortPattern = "^(?:\\[(.+?)\\])?(?::(\\d{1,5}))?$";
        return ipWithPort.matches(ipWithPortPattern);
    }


    public static String getIpType(String ip) {
        if (isValidIPv4(ip)) {
            return "IPv4";
        } else if (isValidIPv6(ip)) {
            return "IPv6";
        } else if (isValidIPv4AddressWithPort(ip)) {
            return "IPv4 with Port";
        } else if (isValidIPv6AddressWithPort(ip)) {
            return "IPv6 with Port";
        } else {
            return "Invalid IP";
        }
    }
}
