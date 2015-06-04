package com.syamantakm.feign.client;

import feign.RequestLine;

/**
 * @author syamantak.
 */
public interface HelloService {
    @RequestLine("GET /hello")
    String greetings();
}
