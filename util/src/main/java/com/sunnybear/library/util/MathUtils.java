package com.sunnybear.library.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * 数学工具
 * Created by guchenkai on 2015/12/17.
 */
public final class MathUtils {

    /**
     * 加法
     *
     * @param num1 被加数
     * @param num2 加数
     * @return 和
     */
    public static String add(String num1, String num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.add(b2).toString();
    }

    /**
     * 减法
     *
     * @param num1 被减数
     * @param num2 减数
     * @return 差
     */
    public static String subtract(String num1, String num2) {
        BigDecimal b1 = new BigDecimal(num1);
        BigDecimal b2 = new BigDecimal(num2);
        return b1.subtract(b2).toString();
    }

    /**
     * 乘法
     *
     * @param figure      数字
     * @param coefficient 因数
     * @return 乘积
     */
    public static String multiplication(String figure, int coefficient) {
        BigDecimal b1 = new BigDecimal(figure);
        BigDecimal b2 = new BigDecimal(coefficient);
        return b1.multiply(b2).toString();
    }

    /**
     * 乘法
     *
     * @param figure      数字
     * @param coefficient 因数
     * @return 乘积
     */
    public static String multiplication(String figure, String coefficient) {
        BigDecimal b1 = new BigDecimal(figure);
        BigDecimal b2 = new BigDecimal(coefficient);
        return b1.multiply(b2).toString();
    }

    /**
     * 比较两个数字大小
     *
     * @param num1 第一个数字
     * @param num2 第二个数字
     * @return 数字大小
     */
    public static boolean compare(String num1, String num2) {
        double number1 = Double.parseDouble(num1);
        double number2 = Double.parseDouble(num2);
        return Math.max(number1, number2) == number1;
    }

    /**
     * 计算百分比
     *
     * @param member      分子
     * @param denominator 分母
     * @param keep        小数点后保留位数
     */
    public static String percent(long member, long denominator, int keep) {
        if (member > denominator)
            throw new RuntimeException("分子不能大于分母");
        double result = 0.0;
        if (denominator == 0L)
            result = 0.0;
        else
            result = (double) member / (double) denominator;
        NumberFormat format = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        format.setMaximumFractionDigits(keep);
        return format.format(result * 100) + "%";
    }

    /**
     * 计算百分比
     *
     * @param member      分子
     * @param denominator 分母
     */
    public static int percent(long member, long denominator) {
        return (int) ((double) member / (double) denominator);
    }
}
