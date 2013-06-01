package com.angelhack.wheresapp.map;

public class InfoWindowData {
	public String coverImageUrl;
	public String title;
	public String developerName;
	public String tvSalesName;
	public String address;

	public InfoWindowData(String coverImageUrl, String firstLine,
			String secondLine, String thirdLine, String forthLine) {
		this.coverImageUrl = coverImageUrl;
		this.title = firstLine;
		this.developerName = secondLine;
		this.tvSalesName = thirdLine;
		this.address = forthLine;
	}
}
