package com.innomalist.taxi.common;

import android.content.Context;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CloudPaymentsApi
{
    Context context;

    public CloudPaymentsApi(Context context)
    {
        this.context = context;
        System.setProperty("http.keepAlive", "false");
    }

    public ResultApi<String> paymentToken(String token, String amount, String nameUser) throws IOException
    {
        OkHttpClient client = new OkHttpClient();

        String ipV4 = UtilsUtils.getIPAddress(true);

        String json = "{\n" +
                "    \"Amount\":"+amount+",\n" +
                "    \"Currency\":\"KZT\",\n" +
                "    \"Description\":\"Пополнение счета\",\n" +
                "    \"IpAddress\":\""+ipV4+"\",\n" +
                "    \"Name\":\""+nameUser+"\",\n" +
                "    \"CardCryptogramPacket\":\""+token+"\"\n" +
                "}";

        String url = "https://api.cloudpayments.ru/payments/cards/charge";
        String codeAuth = Credentials.basic(context.getResources().getString(R.string.cardpaymnets_public_key), context.getResources().getString(R.string.cardpaymnets_secret_api));

        RequestBody body = RequestBody.create(MediaType.parse("application/json;"), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", codeAuth)
                .addHeader("Content-Type","application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful())
        {
            String bodyRes = response.body().string();

            try
            {
                JSONObject result = new JSONObject(bodyRes);

                if (result.getBoolean("Success"))
                {
                    JSONObject confirmation = result.getJSONObject("Model");

                    return new ResultApi<String>("TRUE", confirmation.getString("TransactionId"));
                }
                else
                {
                    if (result.getJSONObject("Model") != null)
                    {
                        JSONObject confirmation = result.getJSONObject("Model");

                        return new ResultApi<String>("3DS", confirmation.getString("PaReq") + "|||" + confirmation.getString("AcsUrl") + "|||" +
                                confirmation.getString("TransactionId"));
                    }
                    else
                    {
                        return new ResultApi<String>("FALSE", result.getString("Message"));
                    }
                }
            }
            catch (JSONException ex)
            {
                return new ResultApi<String>(String.valueOf(ex.getMessage()), null);
            }
        }
        else
        {
            return new ResultApi<String>(String.valueOf(response.code()), null);
        }
    }

    public ResultApi<String> post3DS(String md, String paRes) throws IOException
    {
        OkHttpClient client = new OkHttpClient();

        String ipV4 = UtilsUtils.getIPAddress(true);

        String json = "{\n" +
                "    \"TransactionId\":\""+md+"\",\n" +
                "    \"PaRes\":\""+paRes+"\",\n"+
                "}";

        String url = "https://api.cloudpayments.ru/payments/cards/post3ds";
        String codeAuth = Credentials.basic(context.getResources().getString(R.string.cardpaymnets_public_key), context.getResources().getString(R.string.cardpaymnets_secret_api));

        RequestBody body = RequestBody.create(MediaType.parse("application/json;"), json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", codeAuth)
                .addHeader("Content-Type","application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful())
        {
            String bodyRes = response.body().string();

            try
            {
                JSONObject result = new JSONObject(bodyRes);

                if (result.getBoolean("Success"))
                {
                    JSONObject confirmation = result.getJSONObject("Model");

                    return new ResultApi<String>("TRUE", confirmation.getString("TransactionId"));
                }
                else
                {
                    return new ResultApi<String>("FALSE", result.getString("Message"));
                }
            }
            catch (JSONException ex)
            {
                return new ResultApi<String>(String.valueOf(ex.getMessage()), null);
            }
        }
        else
        {
            return new ResultApi<String>(String.valueOf(response.code()), null);
        }
    }


    public class ResultApi<T>
    {
        public String message;
        public T result;

        public ResultApi(String message, T result)
        {
            this.message = message;
            this.result = result;
        }
    }
}
