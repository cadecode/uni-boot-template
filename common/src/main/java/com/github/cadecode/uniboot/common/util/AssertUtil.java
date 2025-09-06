package com.github.cadecode.uniboot.common.util;

import cn.hutool.core.util.ObjUtil;
import com.github.cadecode.uniboot.common.enums.ErrorCode;
import com.github.cadecode.uniboot.common.exception.GeneralException;

import java.util.function.Supplier;

/**
 * 断言工具类
 * <p>由 hutool {@code ObjUtil} 提供判断
 * <p>默认抛出的异常是 {@code GeneralException}
 *
 * @author Cade Li
 * @date 2022/9/24
 */
public class AssertUtil {

    /**
     * 判断是否为 true，如果是就抛出运行时异常
     *
     * @param expression        布尔表达式
     * @param exceptionSupplier 运行时异常提供者
     */
    public static void isTrue(boolean expression, Supplier<RuntimeException> exceptionSupplier) {
        if (expression) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 判断是否为 true，如果是就抛出运行时异常 GeneralException
     *
     * @param expression 布尔表达式
     * @param moreInfo   异常信息
     * @param params     字符串模板参数
     */
    public static void isTrue(boolean expression, String moreInfo, Object... params) {
        isTrue(expression, () -> GeneralException.of(moreInfo, params));
    }

    /**
     * 判断是否为 true，如果是就抛出运行时异常 GeneralException
     *
     * @param expression 布尔表达式
     * @param errorCode  错误码
     * @param moreInfo   异常信息
     * @param params     字符串模板参数
     */
    public static void isTrue(boolean expression, ErrorCode errorCode, String moreInfo, Object... params) {
        isTrue(expression, () -> GeneralException.of(errorCode, moreInfo, params));
    }

    /**
     * 判断是否为 true，如果是就抛出运行时异常 GeneralException，指定 caused
     *
     * @param expression 布尔表达式
     * @param errorCode  错误码
     * @param throwable  caused 异常
     * @param moreInfo   异常信息
     * @param params     字符串模板参数
     */
    public static void isTrue(boolean expression, ErrorCode errorCode, Throwable throwable, String moreInfo, Object... params) {
        isTrue(expression, () -> GeneralException.of(errorCode, throwable, moreInfo, params));
    }

    /**
     * 判断是否为 false，如果是就抛出运行时异常
     *
     * @param expression        布尔表达式
     * @param exceptionSupplier 运行时异常提供者
     */
    public static void isFalse(boolean expression, Supplier<RuntimeException> exceptionSupplier) {
        isTrue(!expression, exceptionSupplier);
    }

    /**
     * 判断是否为 false，如果是就抛出运行时异常 GeneralException
     *
     * @param expression 布尔表达式
     * @param moreInfo   异常信息
     * @param params     字符串模板参数
     */
    public static void isFalse(boolean expression, String moreInfo, Object... params) {
        isTrue(!expression, () -> GeneralException.of(moreInfo, params));
    }

    /**
     * 判断是否为 false，如果是就抛出运行时异常 GeneralException
     *
     * @param expression 布尔表达式
     * @param errorCode  错误码
     * @param moreInfo   异常信息
     * @param params     字符串模板参数
     */
    public static void isFalse(boolean expression, ErrorCode errorCode, String moreInfo, Object... params) {
        isTrue(!expression, () -> GeneralException.of(errorCode, moreInfo, params));
    }

    /**
     * 判断是否为 false，如果是就抛出运行时异常 GeneralException，指定 caused
     *
     * @param expression 布尔表达式
     * @param errorCode  错误码
     * @param throwable  caused 异常
     * @param moreInfo   异常信息
     * @param params     字符串模板参数
     */
    public static void isFalse(boolean expression, ErrorCode errorCode, Throwable throwable, String moreInfo, Object... params) {
        isTrue(!expression, () -> GeneralException.of(errorCode, throwable, moreInfo, params));
    }

    /**
     * 判断是否为 null，如果是就抛出运行时异常
     *
     * @param o                 对象
     * @param exceptionSupplier 运行时异常提供者
     */
    public static void isNull(Object o, Supplier<RuntimeException> exceptionSupplier) {
        isTrue(ObjUtil.isNull(o), exceptionSupplier);
    }

    /**
     * 判断是否为 null，如果是就抛出运行时异常 GeneralException
     *
     * @param o        对象
     * @param moreInfo 异常信息
     * @param params   字符串模板参数
     */
    public static void isNull(Object o, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNull(o), () -> GeneralException.of(moreInfo, params));
    }

    /**
     * 判断是否为 null，如果是就抛出运行时异常 GeneralException
     *
     * @param o         对象
     * @param errorCode 错误码
     * @param moreInfo  异常信息
     * @param params    字符串模板参数
     */
    public static void isNull(Object o, ErrorCode errorCode, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNull(o), () -> GeneralException.of(errorCode, moreInfo, params));
    }

    /**
     * 判断是否为 null，如果是就抛出运行时异常 GeneralException，指定 caused
     *
     * @param o         布尔表达式
     * @param errorCode 对象
     * @param throwable caused 异常
     * @param moreInfo  异常信息
     * @param params    字符串模板参数
     */
    public static void isNull(Object o, ErrorCode errorCode, Throwable throwable, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNull(o), () -> GeneralException.of(errorCode, throwable, moreInfo, params));
    }

    /**
     * 判断是否不为 null，如果是就抛出运行时异常
     *
     * @param o                 对象
     * @param exceptionSupplier 运行时异常提供者
     */
    public static void isNotNull(Object o, Supplier<RuntimeException> exceptionSupplier) {
        isTrue(ObjUtil.isNotNull(o), exceptionSupplier);
    }

    /**
     * 判断是否不为 null，如果是就抛出运行时异常 GeneralException
     *
     * @param o        对象
     * @param moreInfo 异常信息
     * @param params   字符串模板参数
     */
    public static void isNotNull(Object o, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNotNull(o), () -> GeneralException.of(moreInfo, params));
    }

    /**
     * 判断是否不为 null，如果是就抛出运行时异常 GeneralException
     *
     * @param o         对象
     * @param errorCode 错误码
     * @param moreInfo  异常信息
     * @param params    字符串模板参数
     */
    public static void isNotNull(Object o, ErrorCode errorCode, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNotNull(o), () -> GeneralException.of(errorCode, moreInfo, params));
    }

    /**
     * 判断是否不为 null，如果是就抛出运行时异常 GeneralException，指定 caused
     *
     * @param o         布尔表达式
     * @param errorCode 对象
     * @param throwable caused 异常
     * @param moreInfo  异常信息
     * @param params    字符串模板参数
     */
    public static void isNotNull(Object o, ErrorCode errorCode, Throwable throwable, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNotNull(o), () -> GeneralException.of(errorCode, throwable, moreInfo, params));
    }

    /**
     * 判断是否为空，如果是就抛出运行时异常
     *
     * @param o                 对象
     * @param exceptionSupplier 运行时异常提供者
     */
    public static void isEmpty(Object o, Supplier<RuntimeException> exceptionSupplier) {
        isTrue(ObjUtil.isEmpty(o), exceptionSupplier);
    }

    /**
     * 判断是否为空，如果是就抛出运行时异常 GeneralException
     *
     * @param o        对象
     * @param moreInfo 异常信息
     * @param params   字符串模板参数
     */
    public static void isEmpty(Object o, String moreInfo, Object... params) {
        isTrue(ObjUtil.isEmpty(o), () -> GeneralException.of(moreInfo, params));
    }

    /**
     * 判断是否为空，如果是就抛出运行时异常 GeneralException
     *
     * @param o         对象
     * @param errorCode 错误码
     * @param moreInfo  异常信息
     * @param params    字符串模板参数
     */
    public static void isEmpty(Object o, ErrorCode errorCode, String moreInfo, Object... params) {
        isTrue(ObjUtil.isEmpty(o), () -> GeneralException.of(errorCode, moreInfo, params));
    }

    /**
     * 判断是否为空，如果是就抛出运行时异常 GeneralException，指定 caused
     *
     * @param o         布尔表达式
     * @param errorCode 对象
     * @param throwable caused 异常
     * @param moreInfo  异常信息
     * @param params    字符串模板参数
     */
    public static void isEmpty(Object o, ErrorCode errorCode, Throwable throwable, String moreInfo, Object... params) {
        isTrue(ObjUtil.isEmpty(o), () -> GeneralException.of(errorCode, throwable, moreInfo, params));
    }

    /**
     * 判断是否不为空，如果是就抛出运行时异常
     *
     * @param o                 对象
     * @param exceptionSupplier 运行时异常提供者
     */
    public static void isNotEmpty(Object o, Supplier<RuntimeException> exceptionSupplier) {
        isTrue(ObjUtil.isNotEmpty(o), exceptionSupplier);
    }

    /**
     * 判断是否不为空，如果是就抛出运行时异常 GeneralException
     *
     * @param o        对象
     * @param moreInfo 异常信息
     * @param params   字符串模板参数
     */
    public static void isNotEmpty(Object o, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNotEmpty(o), () -> GeneralException.of(moreInfo, params));
    }

    /**
     * 判断是否不为空，如果是就抛出运行时异常 GeneralException
     *
     * @param o         对象
     * @param errorCode 错误码
     * @param moreInfo  异常信息
     * @param params    字符串模板参数
     */
    public static void isNotEmpty(Object o, ErrorCode errorCode, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNotEmpty(o), () -> GeneralException.of(errorCode, moreInfo, params));
    }

    /**
     * 判断是否不为空，如果是就抛出运行时异常 GeneralException，指定 caused
     *
     * @param o         布尔表达式
     * @param errorCode 对象
     * @param throwable caused 异常
     * @param moreInfo  异常信息
     * @param params    字符串模板参数
     */
    public static void isNotEmpty(Object o, ErrorCode errorCode, Throwable throwable, String moreInfo, Object... params) {
        isTrue(ObjUtil.isNotEmpty(o), () -> GeneralException.of(errorCode, throwable, moreInfo, params));
    }

}
