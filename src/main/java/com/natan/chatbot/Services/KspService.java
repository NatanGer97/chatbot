package com.natan.chatbot.Services;

import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.*;
import com.natan.chatbot.Models.*;
import org.json.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

@Service
public class KspService {
    /**
     *
     * @param keyword search param
     * @return null -> in case no items were found or the found items as map of {index: item}
     * @throws UnirestException throw if
     */
    public Map<Integer, KspItem> searchProductInKSP(String keyword) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        JSONArray items;
        Map<Integer, KspItem> integerKspItemMap = new HashMap<>();
        HttpResponse<JsonNode> response = null;

        response = Unirest.get(String.format("https://ksp.co.il/m_action/api/category/?search=%s", keyword))
                .header("authority", "ksp.co.il")
                .header("accept", "*/*")
                .header("accept-language", "he,en-US;q=0.9,en;q=0.8,en-IL;q=0.7,he-IL;q=0.6")
                .header("cookie", "language=he; ID_computer=1028553633; cfontsize=0; remember_web_59ba36addc2b2f9401580f014c7f58ea4e30989d=eyJpdiI6IkxOS3ZQSjhZUmdCWm1cL1FwcXFkM01RPT0iLCJ2YWx1ZSI6IkVpTTA0cDZXd3lKZ3p4WG5CaVdkdVRcL1B4bFFFY1NST0lPUGVubnl0T0lmUmVmMEhIZXpZYnBibkhjQ2hUVTY3Zml4S1luNUpjTXQxTGRtcHgyM2txSXR2Yk9hOUE0T1BGU3h5M1hyQlRmUT0iLCJtYWMiOiJmMzI3NjU4NjA1YTgxOGQ5NjQ3ZjU4ZWYzMTZiNjkwNGMzMzZhODg0MDI0MmI0YWM5MDYyOGIzYTg3YTk1OTYzIn0%3D; AmexDiscount={\"pointsToBurn\":null,\"discountInShekels\":null}; store=shipment; remoteVer=7.01; street_id=522; city_id=6200; cart_form_inputs=[]; _gcl_au=1.1.735034737.1669287323; _gid=GA1.3.1381865304.1669724708; _ga=GA1.3.1819008285.1652601910; PHPSESSID=8bfrtrmh70jpljrg6hofmmmdi0; _ga_04VL5ZQ1FG=GS1.1.1669724707.27.1.1669725219.22.0.0; kspHistory=%5B%22phone%22%5D; _gat=1; store=shipment")
                .header("lang", "en")
                .header("referer", "https://ksp.co.il/web/cat/?search=watch")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"107\", \"Chromium\";v=\"107\", \"Not=A?Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("sec-fetch-dest", "empty")
                .header("sec-fetch-mode", "cors")
                .header("sec-fetch-site", "same-origin")
                .header("token", "429321a9754c5232cfceb31f0e960980")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .asJson();


        if (response.getStatus() == 200) {
            System.out.println(response);
            JSONObject kspResponseBody = Objects.requireNonNull(response).getBody().getObject();
            if (!kspResponseBody.getJSONObject("result").keySet().contains("items"))
                return null;

            items = kspResponseBody.getJSONObject("result").getJSONArray("items");

            integerKspItemMap = parseItem(items);

        }


        return integerKspItemMap;


    }

    private Map<Integer, KspItem> parseItem(JSONArray items) {
        List<String> kspItemsAsString = new ArrayList<>();
        Map<Integer, KspItem> stringStringMap = new HashMap<>();
        Field[] kspItemFields = KspItem.class.getDeclaredFields();

        for (int i = 0; i < items.length(); i++) {
            JSONObject currentJsonItem = items.getJSONObject(i);
            KspItem kspItem = new KspItem(currentJsonItem.getString("name"),
                    currentJsonItem.getString("img"),
                    Double.parseDouble(String.valueOf(currentJsonItem.get("price"))),
                    currentJsonItem.getString("brandName"),
                    currentJsonItem.getString("brandImg"));

            stringStringMap.put(i, kspItem);


        }
        return stringStringMap;


    }


}
