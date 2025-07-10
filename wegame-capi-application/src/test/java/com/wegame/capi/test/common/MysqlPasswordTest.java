package com.wegame.capi.test.common;

import com.baomidou.dynamic.datasource.toolkit.CryptoUtils;

public class MysqlPasswordTest {
    private static String private_key = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAoO6AEib/3FZfIzilv8WDIIsLupmEzMabhPuwhncVqrb+55Us+QnmxwdRSt0Y2mHc5MTDFJAZvgQO+8/bFKu4CwIDAQABAkAfbdSnNuSbJGcaGtML+8fbECyvOZIc5+CxfPIBoLojh57SuItd9ZizffMXi8f0neO35Fvu6fZGhqxsPseGhT4pAiEA9Xxc5GkfrWjRnNWCfawH6oVT2SXipRV7QJ7tntEjhV8CIQCn0wpl6hUsmvEHIozM0SWtDUDLJPvgxur0bWvFTvpA1QIgHJs6Y++QHnAJhSIoS6bBYWXaCILur1gafcRHYmtynTcCIQCjsFIqCWFRumZxIR/X/f8ThSO3gIE0QpCUi81cC6ZHaQIhAMWXTp0muR23ErpjbvVBPJDfgyd/1jYg3iYwsyvbLmOe";
    private static String public_key = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKDugBIm/9xWXyM4pb/FgyCLC7qZhMzGm4T7sIZ3Faq2/ueVLPkJ5scHUUrdGNph3OTEwxSQGb4EDvvP2xSruAsCAwEAAQ==";

    public static void main(String[] args) throws Exception {
        // 公钥加密
        String encrypt = CryptoUtils.encrypt(private_key, "Sports-live-qwe123!@#");
        System.out.println("密码加密为：" + encrypt);
        // 私钥进行解密
        System.out.println("密码解密为：" + CryptoUtils.decrypt(public_key, encrypt));

    }
}
