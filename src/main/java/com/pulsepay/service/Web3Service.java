package com.pulsepay.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;

@Service
public class Web3Service {

    //Spring will automatically inject the URL from application.services
    @Value("${pulsepay.web3.rpc-url}")
    private String rpcUrl;

    private Web3j web3j;

    // @PostConstruct tells Spring to run this method the exact second the app starts
    @PostConstruct
    public void init(){
        System.out.println("Connecting to Base Sepolia Network at " + rpcUrl);

        //establishing connection to the blockchain
        this.web3j = Web3j.build(new HttpService(rpcUrl));

        try {
            //check if we are connected
            BigInteger latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
            System.out.println("Successfully connected to Base! Current block: " + latestBlock);
        }catch (IOException e){
            System.err.println("Failed to connect to Base network: " + e.getMessage());
        }
    }
}
