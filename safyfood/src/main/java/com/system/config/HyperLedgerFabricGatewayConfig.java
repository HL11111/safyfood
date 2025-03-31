package com.system.config;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.CallOption;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;


/**
 * @Author mikasan
 * title
 * 配置类
 */

@Configuration
public class HyperLedgerFabricGatewayConfig {

    @Autowired
    private HyperLedgerFabricProperties hyperLedgerFabricProperties;

    @Bean
    public Gateway gateway() throws Exception {

        //读取用户签名证书
        BufferedReader certificationReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getCertificatePath()), StandardCharsets.UTF_8);

        //读取 X.509 证书 （读取证书文件并解析为 X509Certificate 对象
        X509Certificate certificate = Identities.readX509Certificate(certificationReader);

        //读取用户的私钥文件
        BufferedReader privateKeyReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getPrivateKeyPath()),StandardCharsets.UTF_8);

        //从指定的私钥文件中读取私钥并将其解析为一个 PrivateKey 对象
        PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);

        //创建实例
        Gateway gateway = Gateway.newInstance()
                .identity(new X509Identity(hyperLedgerFabricProperties.getMspId(),certificate))     //设置网关的身份认证
                .signer(Signers.newPrivateKeySigner(privateKey))    //设置网关的签名器
                .connection(newGrpcConnection())    //设置网关的连接参数（方法创建一个 gRPC 对象
                .evaluateOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))     //设置评估交易的选项，设置了一个超时时间为5s
                .endorseOptions(CallOption.deadlineAfter(60,TimeUnit.SECONDS))      //设置背书交易的选项，设置了一个超时时间为15s
                .submitOptions(CallOption.deadlineAfter(5,TimeUnit.SECONDS))        //设置提交交易的选项，设置了一个超时时间为5s
                .commitStatusOptions(CallOption.deadlineAfter(1,TimeUnit.MINUTES))  //设置提交状态的选项，设置了一个超时时间为1s
                .connect();     //连接到 Hyperledger Fabric 网络

        //返回的gateway对象 用于与HyperLedger Fabric网络交互
        return gateway;

    }


    // 创建一个 gRPC 连接，用于与 Hyperledger Fabric 网络的 Peer 节点通信
    private ManagedChannel newGrpcConnection() throws IOException, CertificateException {

        //创建一个用于读取 TLS 证书文件的 Reader 对象
        Reader tlsCertReader = Files.newBufferedReader(Paths.get(hyperLedgerFabricProperties.getTlsCertPath()));

        //把读取的TLS证书文件 解析为 X.509 证书对象
        X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);

        //创建一个用于创建 gRPC 连接的 NettyChannelBuilder 对象，并指定目标地址和端口好
        return NettyChannelBuilder.forTarget("192.168.10.106:7051")
                //使用 TLS 证书进行加密通信
                //设置SSL上下文，用于进行安全的TLS连接，创建一个用于客户端的SSL上下文，指定信任管理器，传入之前读取并解析的TLS证书对象
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build())
                //设置目标地址（forTarget）和覆盖权限（overrideAuthority）
                .overrideAuthority("peer0.org1.example.com") //指定要连接的目标服务器的权威机构
                .build();

    }

    //与hyperledger Fabric 网络的连接网络对象
    @Bean
    //network表示hyperledger fabric中的一个通道
    public Network network(Gateway gateway) {
        //通过gateway.getNetwork()方法 -> 获取指定通道的 Network 对象。
        return gateway.getNetwork(hyperLedgerFabricProperties.getChannel());
    }

    // ActivityContract合约对象，用于与链码进行交互
    @Bean
    //表示hyperledger fabric 中的一个智能合约
    public Contract contract(Network network) {
        //链码包 + 合约名。有多个合约，需要指定合约名
        return network.getContract("food-safety-contract","FootSafetyContract");
    }


}
