package io.github.apm29.core.utils

import android.os.Handler
import androidx.core.os.postDelayed
import java.util.regex.Pattern


/**
 * 定义script的正则表达式
 */
private const val REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>"
/**
 * 定义style的正则表达式
 */
private const val REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>"
/**
 * 定义HTML标签的正则表达式
 */
private const val REGEX_HTML = "<[^>]+>"
/**
 * 定义空格回车换行符
 */
private const val REGEX_SPACE = "\\s*|\t|\r|\n"

fun delHTMLTag(htmlStr: String): String {
    var copy = htmlStr
    // 过滤script标签
    val pScript = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE)
    val mScript = pScript.matcher(copy)
    copy = mScript.replaceAll("")
    // 过滤style标签
    val pStyle = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE)
    val mStyle = pStyle.matcher(copy)
    copy = mStyle.replaceAll("")
    // 过滤html标签
    val pHtml = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE)
    val mHtml = pHtml.matcher(copy)
    copy = mHtml.replaceAll("")
    // 过滤空格回车标签
    val pSpace = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE)
    val mSpace = pSpace.matcher(copy)
    copy = mSpace.replaceAll("")
    return copy.trim { it <= ' ' } // 返回文本字符串
}

fun String.deleteHtml(): String {
    return delHTMLTag(this)
}


fun Handler.postDelay(delay: Long, runnable: () -> Unit) {
    postDelayed({ runnable() }, delay)
}