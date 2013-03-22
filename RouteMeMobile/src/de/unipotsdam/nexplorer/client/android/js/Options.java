package de.unipotsdam.nexplorer.client.android.js;

public class Options<T> {

	protected String dataType;
	protected String url;
	protected boolean async;
	protected String data;
	protected long timeout;
	protected String type;
	protected Class<T> responseType;

	public Options(Class<T> responseType) {
		this.dataType = "";
		this.url = "";
		this.async = true;
		this.data = "";
		this.timeout = 1000;
		this.type = "GET";
		this.responseType = responseType;
		setData();
	}

	protected void setData() {
	}

	public boolean isAsync() {
		return async;
	}

	public void success() {
	}

	public void success(T data) {
		success();
	}

	public void error() {
	}

	public void error(Exception e) {
		error();
	}

	public String getDataType() {
		return dataType;
	}

	public String getUrl() {
		return url;
	}

	public String getData() {
		return data;
	}

	public long getTimeout() {
		return timeout;
	}

	public String getType() {
		return type;
	}

	public Class<T> getResponseType() {
		return responseType;
	}
}
