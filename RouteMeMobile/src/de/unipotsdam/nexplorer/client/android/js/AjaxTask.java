package de.unipotsdam.nexplorer.client.android.js;

import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;

public class AjaxTask<T> extends AsyncTask<Void, Void, Object> {

	private String host;
	private RestTemplate template;
	private Options<T> options;

	public AjaxTask(String host, RestTemplate template, Options<T> options) {
		this.host = host;
		this.template = template;
		this.options = options;
	}

	@Override
	protected Object doInBackground(Void... params) {
		String url = host + options.url;

		try {
			if (options.type.equals("POST")) {
				return (T) template.postForObject(url, options.data, options.responseType);
			} else {
				return (T) template.getForObject(host, options.responseType);
			}
		} catch (Exception e) {
			return e;
		}
	}
}
