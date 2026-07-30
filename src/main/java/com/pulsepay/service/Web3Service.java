package com.pulsepay.service;

import com.pulsepay.exception.custom.PayoutExecutionException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class Web3Service {

    //Spring will automatically inject the URL from application.services
    @Value("${pulsepay.web3.rpc-url}")
    private String rpcUrl;
    @Value("${pulsepay.web3.treasury.private.key}")
    private String treasuryPrivateKey;

    //field to gold the loaded wallet
    private Credentials credentials;

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

        this.credentials = Credentials.create(treasuryPrivateKey);
        System.out.println("Treasury vault loaded! Adress:" + credentials.getAddress());
    }

    //REWARD sending
    public String sendReward(String toAddress, double amountInEth){
        System.out.println("Initiating payout of " + amountInEth + " ETH to" + toAddress + "...");

        //The Transfer class handles gas calculation and signing automatically
        try {
            TransactionReceipt receipt = Transfer.sendFunds(
                    this.web3j,
                    this.credentials,
                    toAddress,
                    BigDecimal.valueOf(amountInEth),
                    Convert.Unit.ETHER //it converts decimal into raw blockchain WEI
            ).send();

            //return the hash to save it to database as a receipt
            return receipt.getTransactionHash();

        }catch (TransactionException e) {
            //specific blockchain failure(out of gas or reverted)
            throw new PayoutExecutionException("Blockchain rejected the transaction", e);
        }catch (IOException e){
            //network failure
            throw new PayoutExecutionException("Failed to connect to the blockchain network",e);
        }catch (Exception e){
            throw new PayoutExecutionException("Unexpected error during blockchain payout",e );
        }



    }
}
