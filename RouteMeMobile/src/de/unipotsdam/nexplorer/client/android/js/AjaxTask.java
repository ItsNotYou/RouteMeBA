package de.unipotsdam.nexplorer.client.android.js;

import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;

public class AjaxTask<T> extends AsyncTask<Void, Void, Void> {

	private String host;
	private RestTemplate template;
	private Options<T> options;

	public AjaxTask(String host, RestTemplate template, Options<T> options) {
		this.host = host;
		this.template = template;
		this.options = options;
	}

	@Override
	protected Void doInBackground(Void... params) {
		String url = host + options.url;

		try {
			T result = null;
			if (options.type.equals("POST")) {
				result = (T) template.postForObject(url, options.data, options.responseType);
			} else {
				result = (T) template.getForObject(host, options.responseType);
			}
			options.success(result);
		} catch (Exception e) {
			options.error(e);
		}

		return null;
	}
}
