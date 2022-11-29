package com.natan.chatbot.Services;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.*;
import com.natan.chatbot.Models.*;
import okhttp3.*;
import org.json.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.regex.*;

@Service
public class AmazonService {
    public static final Pattern PRODUCT_PATTERN = Pattern.compile("" +
            "<span class=\\\"a-size-medium a-color-base a-text-normal\\\">([^<]+)<\\/span>.*<span class=\\\"a-icon-alt\\\">([^<]+)<\\/span>.*<span class=\\\"a-offscreen\\\">([^<]+)<\\/span>");

    @Autowired
    ObjectMapper objectMapper;

    public String searchProducts(String keyword) throws IOException {
        return parseProductHtml(getProductHtml(keyword));
    }



    private String getProductHtml(String keyword) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://www.amazon.com/s?k=" + keyword + "&crid=XAOP68PX6VE4&sprefix=ipo%2Caps%2C239&ref=nb_sb_noss_2")
                .method("GET", null)
                .addHeader("authority", "www.amazon.com")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("accept-language", "he,en-US;q=0.9,en;q=0.8,en-IL;q=0.7,he-IL;q=0.6")
                .addHeader("cache-control", "max-age=0")
                .addHeader("cookie", "session-id=136-2614712-6449503; ubid-main=130-2512820-6181541; regStatus=registered; i18n-prefs=ILS; sp-cdn=\"L5Z9:IL\"; session-id-time=2082787201l; aws-ubid-main=287-4655035-5651460; aws-target-data=%7B%22support%22%3A%221%22%7D; aws-target-visitor-id=1638702200826-406921.37_0; awsc-search-rs-ia=; AMCV_4A8581745834114C0A495E2B%40AdobeOrg=-2121179033%7CMCIDTS%7C19306%7CMCMID%7C64304279404436526443577211607705230546%7CMCAAMLH-1668593437%7C6%7CMCAAMB-1668593437%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1667995837s%7CNONE%7CMCAID%7CNONE%7CvVersion%7C5.3.0; mbox=session#4d84d9491e7644a6a94e58166e7cb8bd#1667990498|PC#4d84d9491e7644a6a94e58166e7cb8bd.37_0#1731233438; s_nr=1667988638647-New; s_lv=1667988638648; _mkto_trk=id:365-EFI-026&token:_mch-amazon.com-1667988638715-37261; aws-account-alias=995553441267; remember-account=true; awsc-uh-opt-in=; aws-userInfo=%7B%22arn%22%3A%22arn%3Aaws%3Aiam%3A%3A995553441267%3Auser%2Fnatan%22%2C%22alias%22%3A%22995553441267%22%2C%22username%22%3A%22natan%22%2C%22keybase%22%3A%22CpIlI13j5c9Nk%2FNXotMIhxA7U8Wd36zb%2BYE1Z0Q%2FVcA%5Cu003d%22%2C%22issuer%22%3A%22http%3A%2F%2Fsignin.aws.amazon.com%2Fsignin%22%2C%22signinType%22%3A%22PUBLIC%22%7D; aws-userInfo-signed=eyJ0eXAiOiJKV1MiLCJrZXlSZWdpb24iOiJhcC1ub3J0aGVhc3QtMSIsImFsZyI6IkVTMzg0Iiwia2lkIjoiMzkzYzIyNzQtZWEzMS00ZmYwLWIyMmEtMjg3NzEwOTdjNmUyIn0.eyJzdWIiOiI5OTU1NTM0NDEyNjciLCJzaWduaW5UeXBlIjoiUFVCTElDIiwiaXNzIjoiaHR0cDpcL1wvc2lnbmluLmF3cy5hbWF6b24uY29tXC9zaWduaW4iLCJrZXliYXNlIjoiQ3BJbEkxM2o1YzlOa1wvTlhvdE1JaHhBN1U4V2QzNnpiK1lFMVowUVwvVmNBPSIsImFybiI6ImFybjphd3M6aWFtOjo5OTU1NTM0NDEyNjc6dXNlclwvbmF0YW4iLCJ1c2VybmFtZSI6Im5hdGFuIn0.iDr6djjYK0VdPMYXg8csHnhAmtTfakVzl_WgnKpDYvslER3909a8mL1eCv0rN9rxmSdLEM_yEavcDhcizTAhWqOD2SQ6rX-81MXmjJqmOgYyxL0HOW015MG3uwnF_GR4; noflush_awsccs_sid=b4088f9dc3aafe40bb467fe88d13b7dca55983b9afa0098e7cbcc620cf6cbf16; AMCV_7742037254C95E840A4C98A6%40AdobeOrg=1585540135%7CMCIDTS%7C19324%7CMCMID%7C21052051843238930112402579362675501565%7CMCAAMLH-1670172520%7C6%7CMCAAMB-1670172520%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1669574921s%7CNONE%7CMCAID%7CNONE%7CvVersion%7C4.4.0; session-token=\"UeMoEsJVV0cEzFerRKqjj5n4rMiASksoieoPHOq9tqeFmcAABzCv+BmLfMlolcoZrtr0kkQSznhUTO3SnWuoqqD690LKHvRUWy2w5sPvDHz0Jbgbh40Xwe6ZGAThoIoYrU0veH8tsWviYpitI/Yy75e+hb8o++P6jZr7ySUxfXlsu53QRkEFoHQlU/TlEZapPCHK/FVQs6eSRZSbuf3cn79rzv3Zs+sbWjAL9qF0VT0=\"; skin=noskin; lc-main=en_US; csm-hit=adb:adblk_yes&t:1669712422398&tb:6KND3CN0CHVJS1AQ3G7E+s-49HQP93SFNJ8BEJQKNHW|1669712422398")
                .addHeader("device-memory", "8")
                .addHeader("downlink", "10")
                .addHeader("dpr", "1")
                .addHeader("ect", "4g")
                .addHeader("referer", "https://www.amazon.com/?language=en_US")
                .addHeader("rtt", "150")
                .addHeader("sec-ch-device-memory", "8")
                .addHeader("sec-ch-dpr", "1")
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"107\", \"Chromium\";v=\"107\", \"Not=A?Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-ch-viewport-width", "1109")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-user", "?1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .addHeader("viewport-width", "1109")
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    private String parseProductHtml(String html) {
        String res = "";
        Matcher matcher = PRODUCT_PATTERN.matcher(html);
        while (matcher.find()) {
            res += matcher.group(1) + " - " + matcher.group(2) + ", price: " + matcher.group(3) + "<br/>\n";
        }
        return res;
    }

}
