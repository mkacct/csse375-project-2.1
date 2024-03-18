package domain;

import java.util.function.Function;

public enum NamingConventions {
    lowercase((chars) -> {
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isDigit(chars[i]) && !Character.isLowerCase(chars[i])) {
                return false;
            }
        }
        return true;
    }),
    UPPERCASE((chars) -> {
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isDigit(chars[i]) && !Character.isUpperCase(chars[i])) {
                return false;
            }
        }
        return true;
    }),
    UPPER_CASE((chars) -> {
        for (int i = 0; i < chars.length; i++) {
            if (!(chars[i] == ('_')) && !Character.isDigit(chars[i]) && !Character.isUpperCase(chars[i])) {
                return false;
            }
        }
        return true;
    }),
    lower_case((chars) -> {
        for (int i = 0; i < chars.length; i++) {
            if (!(chars[i] == ('_')) && !Character.isDigit(chars[i]) && !Character.isLowerCase(chars[i])) {
                return false;
            }
        }
        return true;
    }),
    camelCase((chars) -> {
        if (!Character.isLowerCase(chars[0])) {
            return false;
        }
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isLetterOrDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }),
    PascalCase((chars) -> {
        if (!Character.isUpperCase(chars[0])) {
            return false;
        }
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isLetterOrDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }),
    ANY((chars) -> {return true;});

    private final Function<char[], Boolean> checker;

    private NamingConventions(Function<char[], Boolean> checker) {
        this.checker = checker;
    }

    /**
     * Check whether the given char array complies with the naming convention
     * @param chars name as a char array
     * @return true iff the name complies with the naming convention
     */
    public boolean check(char[] chars) {
        return checker.apply(chars);
    }

    /**
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
