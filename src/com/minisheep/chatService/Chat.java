package com.minisheep.chatService;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.tools.Tool;

import com.minisheep.Bean.Flight;
import com.minisheep.SearchFlight.SearchFlight;
import com.minisheep.util.ToolsUtil;

public class Chat {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void ChatWithBot(String question,String openId){
		Date now = new Date();
		String createTime = this.format.format(now);
		String answer = "";
		answer = Service.chat(openId, createTime, question);
		System.out.println(answer);
	}
	
	
	public static boolean responseFlightIdSearch(String flightname){  //回复根据航班号查询,数据库相关信息都在javabean了，要什么取什么
		SearchFlight search = new SearchFlight();
		boolean hasData = false;
		List<Flight> flights = new ArrayList<Flight>();
		flights = search.searchFlightname(flightname);
		for(Flight flight : flights){
			String status = "";
			if(flight.getFlightStatus().equals("ARR")){
				status = "已经达到";
			}else if(flight.getFlightStatus().equals("DEP")){
				status = "已起飞";
			}else{
				status = flight.getFlightStatus();
			}
			String finalStr = "航班号:" + flight.getCarrier()  + flight.getFlight() + "\n" + "预计起飞时间:" + flight.getScheduleTime() + "\n" + "实际起飞时间:" + flight.getActualTime() + "\n" +  "航班状态:" + status;
			String lastupdateTime = "最后一次更新时间为:" + flight.getLastUpdated();
			System.out.println(finalStr);
			System.out.println(lastupdateTime);
			System.out.println();
			hasData = true;
		}
		return hasData;
	}
	
	public static void main(String[] args){
		Service.createIndex();
		System.out.println("您好阿，我是智能机器人，请问有什么可以帮您?");
		String openId = "guest";
		Scanner in=new Scanner(System.in);
		String text = "";
		while((text = in.next()) != null){   //如何判断是静态问题还是动态问题?
			String category = "";  //按类别查询不同的数据库
			try {
				category = Service.cutWords(text);  //切词
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("分类为:" + category);
			System.out.println();
			String[] names = category.split("\\|");  //根据分词结果判断用户是否是要查询航班动态?
			
			boolean isFlightSearch = false;
			for(int i=0;i<names.length;i++){
				if(names[i].equals("飞") || names[i].equals("飞往") || names[i].equals("航班") || names[i].equals("航班动态") || names[i].equals("航班号")
						|| ToolsUtil.RegexFlightId(names[i]) == true){  //航班查询的关键词
					isFlightSearch = true;
					System.out.println("这个是航班动态的分类!");
					break;
				}
			}
			/*
			 * 可以设置优先级，先在哪个数据库找结果，没有再下一个数据库里面找，如下先在航班动态的数据库里面找,
			 * 也可以考虑先进去栏目，再搜索就在指定的栏目数据库里面查找
			 */
			boolean hasdata = false;
			if(isFlightSearch == true){  //如果没有结果就继续往下
				String flightIdName = "";
				for(int i=0;i<names.length;i++){
					if(ToolsUtil.RegexFlightId(names[i]) == true){
						flightIdName = ToolsUtil.lowerToupper(names[i]);
						//System.out.println("变成大写后的FlightId:" + flightIdName);
						hasdata = responseFlightIdSearch(flightIdName);  //航班号做回答,如果没有数据就继续往下查找别的数据库等
					}
				}
			}
			if(hasdata == false){
				Chat chat = new Chat();
				chat.ChatWithBot(text, openId);
			}
		}
	}
}
