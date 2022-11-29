package com.natan.chatbot.Models.Bot;

import com.natan.chatbot.Models.*;

import java.util.*;

public  class BotResponse {
        public Map<Integer, KspItem> getKspItemMap() {
            return kspItemMap;
        }

        Map<Integer, KspItem> kspItemMap;
        String source = "BOT";



        public String getSource() {
            return source;
        }

        public static BotResponse of(Map<Integer, KspItem> map) {
            BotResponse res = new BotResponse();
            res.kspItemMap = map;
            return res;
        }
    }