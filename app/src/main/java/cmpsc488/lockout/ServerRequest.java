package cmpsc488.lockout;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerRequest extends AsyncTask<Void, Integer, JSONObject> {

    private static final String JSON_FLAG_MESSAGE = "message";
    private static final String JSON_FLAG_RESULT = "result";
    private static final String JSON_FLAG_CODE = "code";
    private static final String JSON_FLAG_DATA = "data";

    private static final String JSON_RESULT_SUCCESS = "success";
    private static final String JSON_RESULT_FAILURE = "failure";
    private static final String JSON_RESULT_ERROR = "error";

    public static final String SERVER_HOST = "146.186.64.169";
    public static final int SERVER_PORT = 6918;
    public static final String SCHEME_HTTP = "https://";

    public class InvalidURIException extends Exception {}

    public class Parameter implements NameValuePair {

        String name;
        String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getValue() {
            return value;
        }

    }

    private String scheme;
    private String host;
    private Integer port;
    private String path;
    private Map<String, String> queryParams;

    public ServerRequest() {
        setScheme(SCHEME_HTTP);
        setHost(SERVER_HOST);
        setPort(SERVER_PORT);
    }

    public synchronized HttpClient getHTTPClient() {
        return new DefaultHttpClient();
    }

    public ServerRequest setScheme (String s) throws NullPointerException, IllegalArgumentException {

        if ( s == null) throw new NullPointerException();

        scheme = s;

        return this;
    }

    public ServerRequest setHost (String h) throws NullPointerException, IllegalArgumentException {

        if ( h == null) throw new NullPointerException();

        host = h;

        return this;
    }

    public ServerRequest setPort (int _port) throws IllegalArgumentException {

        if (_port < 0 || _port > 656635) throw new IllegalArgumentException();

        port = _port;

        return this;
    }

    public ServerRequest setParameter(String key, String value) throws IllegalArgumentException {

        if ((key == null) || (value == null)) throw new IllegalArgumentException();

        if (queryParams == null) {
            queryParams = new HashMap<String, String>();
        }

        queryParams.put(key, value);

        return this;
    }

    public ServerRequest setParameters(final List<NameValuePair> parameters) throws IllegalArgumentException {

        if (parameters == null) throw new IllegalArgumentException();

        if (queryParams == null) {
            queryParams = new HashMap<String, String>();
        }

        for (NameValuePair nvp : parameters) {
            queryParams.put(nvp.getName(), nvp.getValue());
        }

        return this;
    }

    public ServerRequest setPath (String _path) throws IllegalArgumentException {

        if (_path == null) throw new IllegalArgumentException();

        path = _path;

        return this;
    }

    protected String getQueryString() {

        if (queryParams == null) return null;

        StringBuilder retVal = new StringBuilder("?");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        for (String key : queryParams.keySet()) {

            params.add(new BasicNameValuePair(key, queryParams.get(key)));
        }

        return URLEncodedUtils.format(params, "utf-8");
    }

    private String buildURI() throws InvalidURIException {

        final StringBuilder URI = new StringBuilder();

        if (scheme != null) {
            URI.append(scheme);
        } else throw new InvalidURIException();

        if (host != null) {
            URI.append(host);
        } else throw new InvalidURIException();

        if (port != null) {
            URI.append(":").append(port);
        } // Default to 80

        if (path != null ){
            URI.append("/").append(path);
        } else throw new InvalidURIException();

        String queryString = getQueryString();

        if ( queryString != null ) {
            URI.append("?" + queryString);
        }

        Log.e("URI String ->", URI.toString());
        return URI.toString();
    }

    @Override
    protected JSONObject doInBackground(Void... params) {

        try {

            HttpGet httppost = new HttpGet(buildURI());
            HttpClient httpclient = getHTTPClient();
            HttpResponse response = httpclient.execute(httppost);

            // StatusLine stat = response.getStatusLine();
            int status = response.getStatusLine().getStatusCode();

            // Response received
            if (status == 200) {

                // Parse response into JSONObject and return
                return new JSONObject(EntityUtils.toString(response.getEntity()));

            } else {

                JSONObject error = new JSONObject();
                error.put(JSON_FLAG_RESULT, JSON_RESULT_ERROR);
                error.put(JSON_FLAG_MESSAGE, response.getStatusLine());
                error.put(JSON_FLAG_CODE, response.getStatusLine().getStatusCode());
                error.put(JSON_FLAG_DATA, null);
                return error;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
        You are expected to override the following methods
        They run on the UI thread.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void onSuccess(JSONObject data) {
        Log.e("ServerRequest Success", data.toString());
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void onFailure(String message, JSONObject data) {
        Log.e("ServerRequest Failure", message);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void onError(String message, Integer code, JSONObject data) {
        Log.e("ServerRequest Error", message);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        super.onPostExecute(jsonObject);

        try {
            if (jsonObject != null) {

                Log.e("val:", jsonObject.toString());

                String result = jsonObject.getString(JSON_FLAG_RESULT);

                if (result.equals(JSON_RESULT_SUCCESS)) {


                    JSONObject data = null;

                    try {

                        data = jsonObject.getJSONObject(JSON_FLAG_DATA);

                    } catch (JSONException JSONe) {
                        JSONe.printStackTrace();
                    }

                    onSuccess(data);

                } else if (result.equals(JSON_RESULT_FAILURE)) {

                    String message = jsonObject.getString(JSON_FLAG_MESSAGE);

                    JSONObject data = null;

                    try {

                        data = jsonObject.getJSONObject(JSON_FLAG_DATA);

                    } catch (JSONException JSONe) {}

                    onFailure(message, data);

                } else if (result.equals(JSON_RESULT_ERROR)) {

                    String message = jsonObject.getString(JSON_FLAG_MESSAGE);

                    JSONObject data = null;

                    try {

                        data = jsonObject.getJSONObject(JSON_FLAG_DATA);

                    } catch (JSONException JSONe) {}


                    Integer code = null;

                    try {

                        code = jsonObject.getInt(JSON_FLAG_CODE);

                    } catch (JSONException JSONe) {}

                    onError(message, code, data);

                } else {

                    onError("Server Returned Invalid Status", 0, null); //ERRORCODES.INVALID_STATUS == 0?

                }

            } else {
                onError("No obj returned", null, null);
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /*
    @Override
    protected void onCancelled(JSONObject jsonObject) {
        super.onCancelled(jsonObject);
    }
    */

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}