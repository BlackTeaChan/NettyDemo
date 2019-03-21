package com.blackteachan.netty.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ModbusUtil {

    /**
     * 发送查询命令到DTU（自动加校验）
     * @param ctx 通道
     * @param cmd 命令(任意长度)，例"01 03 00 01 00 02"
     */
    public static void sendHexCommand(ChannelHandlerContext ctx, String cmd){
        //加校验
        String xda = CRC16Util.crc(cmd);
        String[] commands = xda.split(" ");
        int len = commands.length;

        byte[] bytesCommand = new byte[len];
        for(int i=0;i<len;i++){
            bytesCommand[i] = (byte)(Integer.parseInt(commands[i], 16) & 0xFF);
        }
        //服务器应答(发送一条查询命令)
        ByteBuf echo= Unpooled.copiedBuffer(bytesCommand);
        ctx.writeAndFlush(echo);
    }

    /**
     * 模拟量转数字量
     * @param Iv 模拟量
     * @param Isl 模拟量量程低限
     * @param Ish 模拟量量程高限
     * @return
     */
    public static double analog2digital(double Iv, double Isl, double Ish){
        return Iv / 65535 * Ish;
    }

    /**
     * 地址号十进制转十六进制字符串
     * <br>输入：257
     * <br>输出："01 01"
     * @param dec 十进制数
     * @return 十六进制
     */
    public static String dec2Hex(int dec){
        int h1 = dec/256;
        int h2 = dec - (h1*256);
        return String.format("%02x", h1)+" "+String.format("%02x", h2);
    }
    /**
     * 十六进制String转十进制int
     * <br>输入："0101"
     * <br>输出：257
     * @param hex 十六进制字符串
     * @return
     */
    public static int hex2Dec(String hex) {
        return Integer.parseInt(hex, 16);
    }

    /**
     * 把字节数组转换成16进制字符串
     * <br>输入：[-86, 1, 1]
     * <br>输出："AA0101"
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String bytes2Hex(byte[] bytes) {
        //此方法耗效率高
        StringBuffer sb = new StringBuffer(bytes.length);
        String str;
        for (int i = 0; i < bytes.length; i++) {
            str = Integer.toHexString(0xFF & bytes[i]);
            if (str.length() < 2)
                sb.append(0);
            sb.append(str.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 把字节数组转换成16进制字符串
     * <br>输入：[-86, 1, 1]
     * <br>输出：["AA", "01", "01"]
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    public static String[] bytes2Hexs(byte[] bytes) {
        //此方法耗效率高
        StringBuffer sb = new StringBuffer(bytes.length);
        String str;
        for (int i = 0; i < bytes.length; i++) {
            str = Integer.toHexString(0xFF & bytes[i]);
            if (str.length() < 2)
                sb.append(0);
            sb.append(str.toUpperCase());
        }
        return sb.toString().replaceAll("(.{2})", "$1 ").split(" ");
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * <br>输入：
     * <br>输出：
     * @param hexStr
     * @return
     */
    public static String hex2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * IEEE32格式，十六进制转Double
     * <br>输入："40 48 F5 C2"
     * <br>输出：3.14
     * @param hex 四字节十六进制字符串(AE 14 41 43)
     * @return
     */
    public static Double hex2Double(String hex) {
        String[] hexarray = hex.split( " " );
        // 高地位互换
        hex = hexarray[ 0 ] + hexarray[ 1 ] + hexarray[ 2 ] + hexarray[ 3 ];
        // System.out.println( hexstr );
        // 先将16进制数转成二进制数0 10000001 00000000000000000000000 <br>
        String binarystr = hex2Bin( hex );
        // 1位符号位(SIGN)=0 表示正数
        String sign = binarystr.substring( 0, 1 );
        // 8位指数位(EXPONENT)=10000001=129[10进制]
        String exponent = binarystr.substring( 1, 9 );
        int expint = Integer.parseInt( exponent, 2 );
        // 23位尾数位(MANTISSA)=00000000000000000000000
        String last = binarystr.substring( 9 );
        // 小数点移动位数
        int mobit = expint - 127;
        // 小数点右移18位后得10101001 01101001 110.00000
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 23; i++) {
            if (i == mobit)
                sb.append( "." );
            char b = last.charAt( i );
            sb.append( b );
        }
        String valstr = "1" + sb.toString();
        int s = valstr.indexOf( "." ) - 1;// 指数
        Double dval = 0d;
        for (int i = 0; i < valstr.length(); i++) {
            if (valstr.charAt( i ) == '.')
                continue;
            Double d = Math.pow( 2, s );
            int f = Integer.valueOf( valstr.charAt( i ) + "" );
            d = d * f;
            s = s - 1;
            dval = dval + d;
        }
        if (sign.equals( "1" ))
            dval = 0 - dval;
        return  dval;
    }

    /**
     * 十六进制字符串转二进制字符串
     * @param hexString 十六进制字符串
     * @return
     */
    public static String hex2Bin(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString( Integer.parseInt( hexString.substring( i, i + 1 ), 16 ) );
            bString += tmp.substring( tmp.length() - 4 );
        }
        return bString;
    }

    /**
     * 判断是否是心跳包
     * @param rec 接受到的报文
     * @return
     */
//    public static boolean isHeartBeat(String[] rec) {
//        int len = rec.length;
//        if(len > 1 && rec[0].equals(Constants.DTU_HEART_BEAT_FLAG_START) && rec[len-1].equals(Constants.DTU_HEART_BEAT_FLAG_END)) {
//            return true;
//        }else {
//            return false;
//        }
//    }
}
