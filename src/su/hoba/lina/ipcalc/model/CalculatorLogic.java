package su.hoba.lina.ipcalc.model;

import java.util.Vector;

public class CalculatorLogic {

    public static class Calculation {
        public static final String NO_DATA = "-";
        public String title;
        public String value;

        public Calculation(String title, String value) {
            this.title = title;
            this.value = value;
        }
    }

    private static final int FIRST_OCTET_INDEX = 0;
    private static final int SECOND_OCTET_INDEX = 1;
    private static final int THIRD_OCTET_INDEX = 2;
    private static final int FOURTH_OCTET_INDEX = 3;

    public static Vector calculate(int[] octets, int cidr) {
        Vector calculations = new Vector();

        long ipAsLong = toBinary(octets);
        long subnetMask = toSubnetMask(cidr);
        long wildcardMask = ~subnetMask & 0xFFFFFFFFL;
        long majorIpAddress = getMajorIpAddress(ipAsLong, cidr);
        long broadcastAddress = majorIpAddress | wildcardMask;
        long maxHosts = getMaxPossibleHostCount(cidr);
        long usableHosts = getUsableHostCount(cidr);

        // 1. IP Address
        calculations.addElement(new Calculation("IP адрес", fromBinary(ipAsLong)));
        // 2. CIDR Prefix
        calculations.addElement(new Calculation("Префикс (CIDR)", "/" + cidr));
        // 3. Subnet Mask
        calculations.addElement(new Calculation("Маска подсети", fromBinary(subnetMask)));
        // 4. Wildcard Mask
        calculations.addElement(new Calculation("Обратная маска", fromBinary(wildcardMask)));
        // 5. Network IP Address
        calculations.addElement(new Calculation("Адрес сети", fromBinary(majorIpAddress)));
        // 6. Broadcast IP Address
        calculations.addElement(new Calculation("Широковещательный IP", fromBinary(broadcastAddress)));
        // 7. Max Possible Hosts
        calculations.addElement(new Calculation("Всего хостов", formatNumber(maxHosts)));
        // 8. Usable Hosts
        calculations.addElement(new Calculation("Рабочие хосты", formatNumber(usableHosts)));
        // 9. First Host
        calculations.addElement(new Calculation("Первый хост", getFirstUsableHost(cidr, majorIpAddress)));
        // 10. Last Host
        calculations.addElement(new Calculation("Последний хост", getLastUsableHost(cidr, majorIpAddress, usableHosts, broadcastAddress)));
        
        return calculations;
    }

    private static String fromBinary(long ip) {
        long first = (ip >> 24) & 0xFF;
        long second = (ip >> 16) & 0xFF;
        long third = (ip >> 8) & 0xFF;
        long fourth = ip & 0xFF;
        return first + "." + second + "." + third + "." + fourth;
    }

    private static long toBinary(int[] octets) {
        long output = octets[FIRST_OCTET_INDEX];
        output = (output << 8) + octets[SECOND_OCTET_INDEX];
        output = (output << 8) + octets[THIRD_OCTET_INDEX];
        output = (output << 8) + octets[FOURTH_OCTET_INDEX];
        return output & 0xFFFFFFFFL;
    }

    private static long toSubnetMask(int cidr) {
        if (cidr == 0) return 0;
        return (-1L << (32 - cidr)) & 0xFFFFFFFFL;
    }

    private static long getMajorIpAddress(long ip, int cidr) {
        if (cidr == 0) return 0;
        int offset = 32 - cidr;
        return (ip >> offset << offset) & 0xFFFFFFFFL;
    }

    private static long getUsableHostCount(int cidr) {
        if (cidr >= 31) return cidr == 31 ? 2 : 1; // RFC 3021 для /31, /32 обычно 1 хост
        long count = (1L << (32 - cidr)) - 2;
        return count < 0 ? 0 : count;
    }

    private static long getMaxPossibleHostCount(int cidr) {
        long count = 1L << (32 - cidr);
        return count < 1 ? 0 : count;
    }

    private static String getFirstUsableHost(int cidr, long majorIpAddress) {
        if (cidr > 32) return Calculation.NO_DATA;
        if (cidr == 32) return fromBinary(majorIpAddress);
        return fromBinary(majorIpAddress + 1);
    }

    private static String getLastUsableHost(int cidr, long majorIpAddress, long usableHosts, long broadcast) {
        if (cidr > 32) return Calculation.NO_DATA;
        if (cidr == 32) return fromBinary(majorIpAddress);
        if (cidr == 31) return fromBinary(majorIpAddress + 1);
        return fromBinary(majorIpAddress + usableHosts);
    }

    private static String formatNumber(long number) {
        // Простой аналог форматирования с пробелом в качестве разделителя тысяч
        String s = String.valueOf(number);
        StringBuffer sb = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (i > 0 && (len - i) % 3 == 0) {
                sb.append(' ');
            }
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
}
