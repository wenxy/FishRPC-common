package com.fish.rpc.util;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import io.netty.util.internal.StringUtil;

public class FishRPCConfig {
    public static final String SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY_ATTR = "com.fish.rpc.parallel.rejected.policy";
    public static final String SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR = "com.fish.rpc.parallel.queue";
    public static final int PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());
	public static Properties prop = new Properties();

	public static void initClient(String configPath) throws Exception{

		File configFile = null;

		if( !StringUtil.isNullOrEmpty(configPath) ){
			configFile = new File(configPath);
		}

		if( configFile == null || !configFile.exists() || !configFile.isFile() ){
			URL url =  Thread.currentThread().getContextClassLoader().getResource("");
			File defaultFile = new File(url.getFile());
			if(defaultFile.isDirectory()){
				File[] files = defaultFile.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						return name.equals("fishRPC-client.properties");
					}
				});

				configFile = (files!=null&&files.length>0)?files[0]:null;
			}
		}

		/*if(configFile==null || !configFile.exists()){
			throw new Exception("FishRPC client-config not found .");
		}*/
		init(configFile);
	}

	public static void initServer(String configPath) throws Exception{
		File configFile = null;

		if( !StringUtil.isNullOrEmpty(configPath) ){
			configFile = new File(configPath);
		}

		if( configFile == null || !configFile.exists() || !configFile.isFile() ){
			URL url =  Thread.currentThread().getContextClassLoader().getResource("");
			File defaultFile = new File(url.getFile());
			if(defaultFile.isDirectory()){
				File[] files = defaultFile.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						return name.equals("fishRPC-server.properties");
					}
				});

				configFile = (files!=null&&files.length>0)?files[0]:null;
			}
		}

		if(configFile==null || !configFile.exists()){
			throw new Exception("FishRPC server-config not found .");
		}

		init(configFile);
	}

	public static String getStringValue(String key,String defaultValue){
		return prop.getProperty(key, defaultValue);
	}
	public static int getIntValue(String key,int defaultValue){
		return Integer.parseInt(String.valueOf(prop.getProperty(key,""+defaultValue)));
	}
	public static long getLongValue(String key,long defaultValue){
		return Long.parseLong(String.valueOf(prop.getProperty(key, ""+defaultValue)));
	}

	public static boolean getBooleanValue(String key,boolean defaultValue){
		return Boolean.parseBoolean(String.valueOf(prop.getProperty(key, ""+defaultValue)));
	}

	public static boolean onDebug(){
		return getBooleanValue("fish.rpc.debug.mode",false);
	}

	public static void init(final File configFile) throws Exception{
		try {
			if( configFile == null ){
				prop.clear();
				return ;
			}
			InputStream is  = new FileInputStream(configFile);
			prop.load(is);
			// 重复配置异常
			Map<String,Object> keyMap = new HashMap<String,Object>();
			for(Object key : prop.keySet()){
				if(keyMap.containsKey((String)key)){
					throw new Exception("FishRPC配置key重复，文件="+configFile.getPath()+",key="+key);
				}else{
					keyMap.put((String)key, null);
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
