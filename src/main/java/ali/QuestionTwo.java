package ali;

/**
 * 输入一个字符串，求出其中最长的回文子串。
 * 子串的含义是：在原串连续出现的字符串片段。
 * 回文的含义是：正着看和倒着看是相同的，如abba和abbebba。
 * 在判断是要求忽略所有的标点和空格，且忽略大小写，但输出时按原样输出（首尾不要输出多余的字符串）。
 * 输入字符串长度大于等于1小于等于5000，且单独占一行(如果有多组答案，输出第一组)
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 若该题作为纯算法题来解，我会采用如下算法，但是在实际生产环境中遇到类似问题，我有其他想法，请看QuestionTwoExtend.java
 */
public class QuestionTwo {
    public static void main(String[] args) {
        String str = "12033533128";
        //去首尾空格和标点符号且全部转为小写
        str = str.trim().replaceAll("[\\pP\\p{Punct}]","").replaceAll(" ","").toLowerCase();
        //给处理后的字符串首尾和中间插入特殊字符"#",因为该特殊字符上一步会被处理掉，所以插入后的回文字符串的长度就必为计数，减少了奇偶判断的时间和空间
        StringBuilder builder = new StringBuilder("#");
        int strLength = str.length();
        for(int i = 0; i < strLength ; i ++){
            builder.append(str.charAt(i)).append("#");
        }
        String handleStr = builder.toString();
        System.out.println(handleStr);
        //开始遍历找处理后的字符串中最长子回文串
        int handleStrLength = handleStr.length();
        //计数
        int tempCount;
        //记录最长回文子串的长度
        int maxPalindromeLength = 0;
        //记录最长回文串中心字符位置
        int maxPalindromeIndex = 0;
        //记录最长回文串中心字符,初始化为特殊字符$
        char maxPalindromeChar = '$';
        //当前回文子串的长度
        int tempPalindromeLength;
        for(int i = 0; i < handleStrLength; i ++){
            tempCount = 0;
            while ((i - tempCount) >= 0 && (i + tempCount) <= handleStrLength-1
                    && handleStr.charAt(i - tempCount) == handleStr.charAt(i + tempCount)){
                    //计算出当前回文子串的长度并将tempCount加1
                    tempPalindromeLength = 2 * tempCount + 1;
                    //如果这个长度是最长回文子串的长度，此处控制了如果有多组答案，只输出第一组
                    if(tempPalindromeLength > maxPalindromeLength){
                        //记录下最长回文串中心字符位置并更新最大长度
                        maxPalindromeIndex = i;
                        maxPalindromeChar = handleStr.charAt(i);
                        maxPalindromeLength = tempPalindromeLength;
                    }
                tempCount++;
            }
        }
        //到这里已经知道了最长回文串中心字符位置和最长回文子串的长度,但是这是对于处理后的字符串而言
        System.out.println(maxPalindromeLength+ "----" + maxPalindromeIndex + "-----" + maxPalindromeChar);
        //先去掉#后拿到中心回文字符和长度
        maxPalindromeLength = (maxPalindromeLength - 1)/2;
        //先拿到这个回文串中心字符数组，这里根据maxPalindromeChar是不是#来判断是奇数回文还是偶数回文，顺便拿到新的index
        //到这里就是到对于原字符串而言，有效字符长度为maxPalindromeLength
        //最大奇数回文子串的中心字符为第maxPalindromeIndex个middleChars[0]
        //最大偶数回文子串的中心字符为第maxPalindromeIndex个middleChars[0]和maxPalindromeIndex+1个middleChars[1]
        char[] middleChars = new char[2];
        if(maxPalindromeChar == '#'){
            //偶数回文子串
            middleChars[0] = handleStr.charAt(maxPalindromeIndex - 1);
            middleChars[1] = handleStr.charAt(maxPalindromeIndex + 1);
            maxPalindromeIndex = maxPalindromeIndex/2;
            //找到第maxPalindromeIndex个middleChars[0]在原字符串中的位置
            int index = 0;
            int count = 0;
            for(int i = 0; i < str.length(); i ++){
                if(str.charAt(i) == middleChars[0] && ++count == maxPalindromeIndex){
                    index = i;
                    break;
                }
            }
            //即找到首尾位置，首：index前（maxPalindromeLength/2）-1个字符位置  尾：index后（maxPalindromeLength/2）+1个字符位置
            //然后用substring截取


        }else {
            //奇数回文子串
            middleChars[0] = handleStr.charAt(maxPalindromeIndex);
            maxPalindromeIndex = (maxPalindromeIndex + 1)/2;
            //找到第maxPalindromeIndex个middleChars[0]在原字符串中的位置
            int index = 0;
            int count = 0;
            for(int i = 0; i < str.length(); i ++){
                if(str.charAt(i) == middleChars[0] && ++count == maxPalindromeIndex){
                    index = i;
                    break;
                }
            }
            //即找到首尾位置，首：index前（maxPalindromeLength - 1)/2个字符位置  尾：index后（maxPalindromeLength - 1)/2个字符位置
            //然后用substring截取

        }







    }
}
