package it.fadeout.omirl.viewmodels;

import java.util.Date;

public class SectionViewModel {
	
	String code;
	String model;
	String basin;
	String river;
	String name;
	String municipality;
	double lat;
	double lon;
	int alt;
	String otherHtml;
	int color;
	
	String basinClass;
	String warningArea;
	Double basinArea;
	
	String imgPath;
	
	String subFolder;
	
	Date refDate;
	double value;
	
	private
	String updateDateTime;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getBasin() {
		return basin;
	}
	public void setBasin(String basin) {
		this.basin = basin;
	}
	public String getRiver() {
		return river;
	}
	public void setRiver(String river) {
		this.river = river;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public int getAlt() {
		return alt;
	}
	public void setAlt(int alt) {
		this.alt = alt;
	}
	public String getOtherHtml() {
		return otherHtml;
	}
	public void setOtherHtml(String otherHtml) {
		this.otherHtml = otherHtml;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public Date getRefDate() {
		return refDate;
	}
	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getBasinClass() {
		return basinClass;
	}
	public void setBasinClass(String basinClass) {
		this.basinClass = basinClass;
	}
	public String getWarningArea() {
		return warningArea;
	}
	public void setWarningArea(String warningArea) {
		this.warningArea = warningArea;
	}
	public Double getBasinArea() {
		return basinArea;
	}
	public void setBasinArea(Double basinArea) {
		this.basinArea = basinArea;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public String getSubFolder() {
		return subFolder;
	}
	public void setSubFolder(String subFolder) {
		this.subFolder = subFolder;
	}
	public String getUpdateDateTime() {
		return updateDateTime;
	}
	public void setUpdateDateTime(String updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

}
