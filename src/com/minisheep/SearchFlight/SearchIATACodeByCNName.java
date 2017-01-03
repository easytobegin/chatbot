package com.minisheep.SearchFlight;

import java.util.ArrayList;
import java.util.List;

import com.minisheep.util.MysqlUtil;

public class SearchIATACodeByCNName {
	public List<String> searchIataCodebyCNname(String CityName){
		List<String> depCityAndarrCity = new ArrayList<String>();
		String cityCode = MysqlUtil.IataCodebyCNnameSearch(CityName);
		if(!cityCode.equals(""))  //通过此条过滤
		{
			depCityAndarrCity.add(cityCode);
		}
		return depCityAndarrCity;
	}
}
