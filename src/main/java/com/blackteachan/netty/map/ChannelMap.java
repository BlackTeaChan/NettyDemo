package com.blackteachan.netty.map;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelHandlerContext;

/**
 * 通道Map类
 * @author blackteachan
 */
public class ChannelMap {

    /**
     * 通道Map
     */
    private static Map<ChannelHandlerContext,String> map = new ConcurrentHashMap<ChannelHandlerContext,String>();

    /**
     * 添加通道
     * @param ctx 通道
     * @param s 标识符
     */
    public static void add(ChannelHandlerContext ctx,String s){
        //防止重复
        remove(s);
        map.put(ctx, s);
    }

    /**
     * 根据通道获取标识符
     * @param ctx 通道
     * @return 标识符
     */
    public static String get(ChannelHandlerContext ctx){
        return map.get(ctx);
    }

    /**
     * 根据标识符获取通道
     * @param s 标识符
     * @return 通道
     */
    public static ChannelHandlerContext get(String s){
        for (Map.Entry<ChannelHandlerContext,String> entry:map.entrySet()){
            if (entry.getValue().equals(s)){
                return entry.getKey();
            }
        }
        return null;
    }
    /**
     * 获取通道数
     * @return 通道数
     */
    public static int getSize() {
        return map.size();
    }

    /**
     * 获取所有通道
     * @return 所有通道
     */
    public static Map<ChannelHandlerContext,String> getAll() {
        return map;
    }

    public static Set<Entry< ChannelHandlerContext,String>> getSet(){
        return map.entrySet();
    }

    /**
     * 移除通道
     * @param client 心跳包
     */
    public static void remove(String client){
        for (Map.Entry<ChannelHandlerContext,String> entry:map.entrySet()){
            if (entry.getValue()==client){
                map.remove(entry.getKey());
            }
        }
    }

    /**
     * 移除通道
     * @param ctx 通道
     */
    public static void remove(ChannelHandlerContext ctx){
        for (Map.Entry<ChannelHandlerContext,String> entry:map.entrySet()){
            if (entry.getKey()==ctx){
                map.remove(entry.getKey());
            }
        }
    }

    /**
     * 通道是否存在
     * @param ctx
     * @return 是否存在
     */
    public static boolean isExist(ChannelHandlerContext ctx){
        boolean exist= false;
        for (Map.Entry<ChannelHandlerContext,String> entry:map.entrySet()){
            if (entry.getKey()==ctx){
                exist= true;
            }
        }
        return exist;
    }
}
