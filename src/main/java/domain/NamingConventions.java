package domain;

public enum NamingConventions {
    lowercase,
    UPPERCASE,
    UPPER_CASE,
    lower_case,
    camelCase,
    PascalCase,
    ANY;

    /**
     * 
     * @param str String version of the enum
     * @return Enum version of string. If the string is not recognized ANY is returned.
     */
    public static NamingConventions getConvention(String str) {
        if (str.equals("lowercase")) {
            return lowercase;
        }
        if (str.equals("UPPERCASE")) {
            return UPPERCASE;
        }
        if (str.equals("UPPER_CASE")) {
            return UPPER_CASE;
        }
        if (str.equals("lower_case")) {
            return lower_case;
        }
        if (str.equals("camelCase")) {
            return camelCase;
        }
        if (str.equals("PascalCase")) {
            return PascalCase;
        }
        return ANY;
    }
}
