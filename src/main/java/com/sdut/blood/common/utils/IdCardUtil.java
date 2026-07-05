package com.sdut.blood.common.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * 身份证号工具类
 * 支持格式校验、年龄提取、性别提取，适配献血资格校验、档案录入校验需求
 */
public class IdCardUtil {

    /**
     * 18位中国大陆身份证正则表达式
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$"
    );

    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 校验身份证号格式是否合法
     *
     * @param idCard 身份证号
     * @return true-合法 false-不合法
     */
    public static boolean isValid(String idCard) {
        if (idCard == null || idCard.trim().length() != 18) {
            return false;
        }
        String cleanIdCard = idCard.trim().toUpperCase();
        return ID_CARD_PATTERN.matcher(cleanIdCard).matches()
                && parseBirthDate(cleanIdCard) != null
                && isValidCheckCode(cleanIdCard);
    }

    /**
     * 从身份证号提取出生日期
     *
     * @param idCard 身份证号
     * @return 出生日期（yyyy-MM-dd格式）
     */
    public static String getBirthday(String idCard) {
        if (!isValid(idCard)) {
            throw new IllegalArgumentException("身份证号格式错误");
        }
        idCard = idCard.trim();
        return idCard.substring(6, 10) + "-" + idCard.substring(10, 12) + "-" + idCard.substring(12, 14);
    }

    /**
     * 根据身份证号计算周岁年龄
     *
     * @param idCard 身份证号
     * @return 周岁年龄
     */
    public static int getAge(String idCard) {
        String birthday = getBirthday(idCard);
        LocalDate birthDate = LocalDate.parse(birthday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * 根据身份证号获取性别
     *
     * @param idCard 身份证号
     * @return 1-男性 2-女性
     */
    public static int getGender(String idCard) {
        if (!isValid(idCard)) {
            throw new IllegalArgumentException("身份证号格式错误");
        }
        idCard = idCard.trim();
        // 第17位：奇数为男，偶数为女
        char genderChar = idCard.charAt(16);
        int genderNum = Integer.parseInt(String.valueOf(genderChar));
        return genderNum % 2 == 1 ? 1 : 2;
    }

    private static LocalDate parseBirthDate(String idCard) {
        try {
            LocalDate birthDate = LocalDate.parse(idCard.substring(6, 14), DateTimeFormatter.BASIC_ISO_DATE);
            return birthDate.isAfter(LocalDate.now()) ? null : birthDate;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static boolean isValidCheckCode(String idCard) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Character.digit(idCard.charAt(i), 10) * WEIGHTS[i];
        }
        return CHECK_CODES[sum % 11] == idCard.charAt(17);
    }
}
