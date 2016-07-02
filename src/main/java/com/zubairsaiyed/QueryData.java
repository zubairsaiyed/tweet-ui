package com.zubairsaiyed;

import java.util.Date;

import org.apache.log4j.Logger;

public class QueryData {

	final static Logger logger = Logger.getLogger(QueryData.class);
	private Double val;
	private Date lastRead;

	public QueryData(Double val) {
		this.val = val;
		this.lastRead = new Date();
	}

	public Double getVal() {
		lastRead = new Date();
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	public Date getLastRead() {
		return lastRead;
	}
	
}
