package com.natan.chatbot.Controllers;

import com.mashape.unirest.http.exceptions.*;
import com.natan.chatbot.Models.*;
import com.natan.chatbot.Models.Bot.*;
import com.natan.chatbot.Services.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/bot")
public class BotController {

    @Autowired
    AmazonService amazonService;

    @Autowired
    KspService kspService;

    @GetMapping("/amazon")
    public ResponseEntity<?> getProducts(@RequestParam String keyword) throws IOException {
        return ResponseEntity.ok(amazonService.searchProducts(keyword));
    }

    @GetMapping("/ksp")
    public ResponseEntity<?> getKspProducts(@RequestParam String keyword)  {
        try {
            Map<Integer, KspItem> items = kspService.searchProductInKSP(keyword);
            if (items == null){
                return ResponseEntity.badRequest().body("sorry, not items were found");
            }
            return ResponseEntity.ok(items);
        } catch (UnirestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PostMapping("")
    public ResponseEntity<?> getBotResponse(@RequestBody BotQuery query) throws IOException {
        HashMap<String, String> params = query.getQueryResult().getParameters();
        Map<Integer, KspItem> kspItemMap = new HashMap<>();
        if (params.containsKey("product")) {
            try {
                kspItemMap = kspService.searchProductInKSP(params.get("product"));
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(BotResponse.of(kspItemMap), HttpStatus.OK);
    }



}
