package com.blackteachan.netty.utils;

/**
 * ModbusCRC16工具类
 * @author blackteachan
 *
 */
public class CRC16Util {

    /**
     * ModeBusRTUCRC16校验及java代码
     * @param msg
     * @return
     */
    public static String crc(String msg) {
        String[] info = msg.split(" ");
        int[] data = new int[info.length];
        for (int h = 0; h < info.length; h++) {
            data[h] = Integer.parseInt(info[h], 16);
        }
        int[] temdata = new int[data.length + 2];
        //unsigned char alen = *aStr – 2;   //CRC16只计算前两部分
        int xda, xdapoly;
        int i, j, xdabit;
        xda = 0xFFFF;
        xdapoly = 0xA001; // (X**16 + X**15 + X**2 + 1)
        for (i = 0; i < data.length; i++) {
            xda ^= data[i];
            for (j = 0; j < 8; j++) {
                xdabit = (int) (xda & 0x01);
                xda >>= 1;
                if (xdabit == 1) {
                    xda ^= xdapoly;
                }
            }
        }
        System.arraycopy(data, 0, temdata, 0, data.length);
        temdata[temdata.length - 2] = (int) (xda & 0xFF);
        temdata[temdata.length - 1] = (int) (xda >> 8);
        String crcInfo = getHexString(temdata);
        return insertSpace(crcInfo);
    }

    //获取16进制字符串
    public static String getHexString(int[] b) {
        String crcInfo = "";
        for (int p = 0; p < b.length; p++) {
            if (b[p] >= 16) {
                crcInfo += Integer.toHexString(b[p]).toString();
            } else {
                crcInfo = crcInfo + "0" + Integer.toHexString(b[p]).toString();
            }
        }
        return crcInfo.toUpperCase();
    }

    //加入空格
    public static String insertSpace(String marStr) {
        StringBuilder sb = new StringBuilder(marStr);
        for (int i = marStr.length() - 2; i > 0; i -= 2) {
            sb.insert(i, " ");
        }
        String marStrNew = sb.toString();
        return marStrNew;
    }
}
