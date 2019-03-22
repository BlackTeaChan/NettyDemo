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
     * @param socketChannel
     * @param client
     */
    public static void add(ChannelHandlerContext socketChannel,String client){
        map.put(socketChannel,client);
    }

    /**
     * 根据通道获取心跳包
     * @param socketChannel
     * @return
     */
    public static String get(ChannelHandlerContext socketChannel){
        return map.get(socketChannel);
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
     * @param socketChannel 通道
     */
    public static void removeKey(ChannelHandlerContext socketChannel){
        for (Map.Entry<ChannelHandlerContext,String> entry:map.entrySet()){
            if (entry.getKey()==socketChannel){
                map.remove(entry.getKey());
            }
        }
    }

    /**
     * 通道是否存在
     * @param socketChannel
     * @return 是否存在
     */
    public static boolean isExist(ChannelHandlerContext socketChannel){
        boolean exist= false;
        for (Map.Entry<ChannelHandlerContext,String> entry:map.entrySet()){
            if (entry.getKey()==socketChannel){
                exist= true;
            }
        }
        return exist;
    }
}
