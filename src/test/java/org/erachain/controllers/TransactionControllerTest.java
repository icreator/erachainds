package org.erachain.controllers;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    private static final String APPLICATION_JSON_PRODUCER = "application/json;charset=utf-8";
    private static final String URL = "/transaction/proc";
    private MockHttpSession mockHttpSession;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }

    @Test
    public void validatePollTransaction() throws Exception {
        var trans_token = "3d7JB4WEzWBUxa73cC3exWGkceuwK38fnF9xJcwUwotsNaMqTNt1pMkotCkJ7yS9ZWQ6KyFBS2Sh4JXsQXzsmWQQA7vKnJoYTcCWWHvieWh4k45h6auzyNsN42et8xszZ4pJCgsVHmTUaJbJk8GzE69ktc5Xm4CZ4dHS779ZZ84Cwg8KuxU9hmSoYhdZbZeiWcJEoYayc14ziADFR9L58imX6wT9buQuNHKYa26CBz8sk5fh4vw1y1CTvzybFx78Ch2iaMWC8QWGZpb4xR15FtzodEmBiSWej98rfyWpA4mA41rmtzeFFvSBK5R9XKxWXDM2Y1UNgzbd6T3xZK3xUieKoMxkQSeRTfxGTZdcXyDgrFEPiUwDerdwoxbMMXGGT8RtbuydMjbSocASZPehbMb3wMxYPK6QS41vAWzWMCtPTV2CwoTSXM5fiEGVAcwK2U5EgrXEQb8LUs6hFw5FaEjfpGLSqdvU5rHdQ22ip2T2GKv8WSiz4zSVWfbQrCDXFFkpRZ5GYrgwxMXP7SxHY2ucpmYjXVx2oLKJWL3BgbKYfQXXKsoXi3tyhhEyr5HVjm5JyVuncSTphEH7veuRFxfKFXjKAqyD9nWhovNV5R5Cd8nANDfAXdZttmJ8YmaVtQnAf";
        mockMvc.perform(post(URL)
                .param("trans_token", trans_token))
                .andExpect(status().isOk());
    }

    @Test
    public void validateRSendTransaction() throws Exception {
        var trans_token = "2oiN5au7FYnbhFMq5uV2tR2JQY8qseJ6b3dQJfbWEfjwAtyALWDbYcYVn4MZZrp8zvACCsxR4rM622eWcGchxAFqMdNeD86hK9K25on5NFRGWTmDKJFDbUCRauj5Khau58s83m2byNuXFzr2Z5TsBpmHgPLQpBjoyVrjtUAHF1m1k5KbJJ2tozksXHNgWEq1ZVWLUYuDJ2eB3tpt7inmL4TyGroWNC4qL5rDsuF7wMqFTgQ21TsJsQhhm9LAXaU9FSAoJVDxs9pxaRXcMFJjpAFRx7F923Cmvm4SqHVMBQd1JW3JsGERaXLJCkf5MgJNwNvxRbU5KNigGWhwnxWQTyNaSXSJQpzSZ1wHJ7S2if3HYaJLqLT3DSrJNuBxVW6sX81685YWKwrFHbHAJUxVsjs7itTzhjwwcGocu3gEJJqQqpDubbi5khnPHzhwvQS9EFaEsAF9GLgXWdQ8Jappvg4E7vTPXoRZdCgDVXRmdkgu8zbf85q54KxuQxyuJMMHcC7oVP2DxCGZauHxTQPAwdZ2VGnpG3xUQUmhPk57jNpJn8jhw75odpXcuv42TEbZKTqVHhQPikUYJsbpMQjXwivEAEM5WTELBkQfxyDfmxsUqSiwsLekKSKsPW";
        mockMvc.perform(post(URL)
                .param("trans_token", trans_token))
                .andExpect(status().isOk());
    }

    @Test
    public void validateCertifyPubKeysTransaction() throws Exception {
        var trans_token = "5q9r657cq5WSrwCKF6V61RGSxMZYsQ1j1dak4tguvQC661cmwvXrTvtHSMm2a5CD2Qki3esYYFJJGBNZ6q2cPqCaJJH6Li8qU9hp58W9irfzkXQpw7UndnRKtmPrLytxHZpJshCMXPBB7cAHNnxYQwaqcs4SLu6qNtYTz42AhgD38xs86JDggBp3T6bBHoXEj3ucEYysHZo9REkQhBLF9Nxr9g2b";
        mockMvc.perform(post(URL)
                .param("trans_token", trans_token))
                .andExpect(status().isOk());
    }

    @Test
    public void validateIssuePersonTransaction() throws Exception {
        var trans_token = "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";
        mockMvc.perform(post(URL)
                .param("trans_token", trans_token))
                .andExpect(status().isOk());
    }

    @Test
    public void validateCreateOrderTransaction() throws Exception {
        var trans_token = "2PYZHiVxbie5MvsEhUEEnx2i9QxnujTM1Hwt3Ls42zX843zc8XxUg2A6ASJFvjwe5Z7HdJPaK482HoGMACL27L9o1ATa7LqQ2eTsDaAmJ2YBNzV4WuvzA54JzNeyFWgepcvJ8wAzA7kuwWZpw1kkK7jH8CpWBQ4NXwutNuQTnZTeQs94m8UENCNHiwHvYyi2LhpjiZJxaxd5";
        mockMvc.perform(post(URL)
                .param("trans_token", trans_token))
                .andExpect(status().isOk());
    }

    @Test
    public void validateCancelOrderTransaction() throws Exception {
        var trans_token = "RQVB9Zq4AoyJMBxqYPdcEbm7VdCeUA14BUviPb4LTQt5b4Qwqdo1pjUuaAckXDTStnrzZnBt1S5ag9bFheU9tw4e2kRYSTFKbgMUfhJTaKzMbsSQV7SkZ8TszDmMdsBjpWmEquXNi9NtMkcCE1cRyaoV3hbphffUn4mqdg3yXQ6VDLv7BZyWnrxHscYnUhkpHU2fk5CYgzedQdzyNV4TzthGj2mJsWTAvmksg8dQWp7UqTs7MwMdQAa";
        mockMvc.perform(post(URL)
                .param("trans_token", trans_token))
                .andExpect(status().isOk());
    }

    @Test
    public void validateHashesRecodrdTransaction() throws Exception {
        var trans_token = "FV6oPtAzo38oCDg9bmv4TBWaUN2p5bDVvx8GfwBaSAfrDSU6LF1FH1m7YbTPqXj7MxMmjk5aPtPiTPzWCc7vtdNaeWcHQvjEvzKEiysFA1zbX7GPZsPXNFGHXmBfKnCunvHqe1c9v2Ljt1f5oPbnAJYZ7PXacihzdehLrbt94W5tGCE76tgd4xiBc2dtKjWhVSmMP8bZdKgigjceCKwHYww1yUspi4ArF1DY4pZ5WE66oKR4Em65HRzvYoF1x6WuYC";
        mockMvc.perform(post(URL)
                .param("trans_token", trans_token))
                .andExpect(status().isOk());
    }

    @Test
    public void validateIssueAssetTransaction() throws Exception {
        var trans_token = "2dNMRDczqcGZUUrjEX4ZncrdZsTgc8giEZXHTwDiC7Pu99etBP8KmnmvXmEzgKev2r554vzbviZrfv7b791nxBM7CQqdarttgfZnsyVGsuHzHZAAXT2HoL5wPYaRmbN1C6ZkTXTfRdKPufEBexkCCP8e597K5VUzuxBTQruXonRqvhTvm8WM7Qt1oYczSmcXLRjEXstHnzKUWS7KvUXSp9mjLyf8uxfjKXVn7o1N1LDi6jot7zz4JP7tCpDwioKuaifCChEFgT9FfCAuhpNhgcks7Tfiaco8dDpec8STzNxDigXsqqbLTzh35";
        mockMvc.perform(post(URL)
                .param("trans_token", trans_token))
                .andExpect(status().isOk());
    }
}