package org.example;

/**
 * Formats numbers with thousand separators for display.
 */
class DisplayFormatter {

    /**
     * Adds thousand separators to a number string.
     * Example: "1234567.89" -> "1 234 567.89"
     */
    public String format(String number) {
        if (number == null || number.isEmpty() ||
                "0".equals(number) || "Error".equals(number) || "Undefined".equals(number)) {
            return number;
        }

        String[] parts = number.split("\\.", 2);
        String intPart = parts[0];

        // Handle negative sign
        boolean negative = intPart.startsWith("-");
        if (negative) intPart = intPart.substring(1);

        // Add spaces every 3 digits from right to left
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < intPart.length(); i++) {
            if (i > 0 && (intPart.length() - i) % 3 == 0) {
                formatted.append(' ');
            }
            formatted.append(intPart.charAt(i));
        }

        if (negative) formatted.insert(0, '-');
        if (parts.length == 2) formatted.append('.').append(parts[1]);

        return formatted.toString();
    }

    /**
     * Removes all spaces from formatted number.
     */
    public String unformat(String formatted) {
        return formatted.replace(" ", "");
    }
}

