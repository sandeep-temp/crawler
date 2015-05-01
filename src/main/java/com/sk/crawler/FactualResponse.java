package com.sk.crawler;

import java.util.List;

public class FactualResponse {

	String status;
	Response response;
	
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	class Response {
		int total_row_count;
		List<Data> data;
		public int getTotal_row_count() {
			return total_row_count;
		}
		public void setTotal_row_count(int total_row_count) {
			this.total_row_count = total_row_count;
		}
		public List<Data> getData() {
			return data;
		}
		public void setData(List<Data> data) {
			this.data = data;
		}
		
	}
	
	class Data {
		String name;
		String locality;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLocality() {
			return locality;
		}
		public void setLocality(String locality) {
			this.locality = locality;
		}
		
	}
}
