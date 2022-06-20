# SOLO共识+平行链使用说明


## 目录
	- [说明](#说明)
	- [SOLO主节点部署](#SOLO主节点部署)
	- [平行链节点部署](#平行链节点部署)
	- [NFT合约概述](#NFT合约概述)
	- [通过SDK实现合约部署调用](#通过SDK实现合约部署调用)
	
## 说明
单节点简单共识，区块产生不需要投票，可在应用和区块链对接开发测试阶段采用，可以window,linux上部署，方便用户快速对接应用做验证。
主链+平行链交易流程：  
- 交易在链下完成构造和签名,交易构造时需要在交易体中带上对应平行链的名称。   
- 签好名的交易通过平行链的jsonrpc接口发往平行链节点。   
- 平行链通过它和主链之间的grpc连接,将交易转发到主链节点,由主链打包区块共识后存入主链账本。   
- 主链区块生成后,平行链实时拉取新产生的区块,过滤出属于本平行链的交易（根据平行链名称）, 送入虚拟机执行后并写入平行链账本。  
下面介绍SOLO主链和平行链节点的部署，智能合约部署和调用方法。  
+ 注： 支持在同一台服务器上同时部署SOLO主节点和平行链节点（只要保证两者的jsonrpc和grpc端口不冲突即可）   
 
## SOLO主节点部署  
下载对应系统的solo节点安装包： 
```  
 #windows环境solo主链下载包:  
https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/mainet/solo/windows/chain33_solo_windos_0670237.zip
 #linux环境solo主链下载包：  
https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/mainet/solo/linux/chain33_solo_linux_0670237.tar.gz
```  

目录下包含以下三个文件  
```  
chain33                -- chain33节点程序
chain33-cli            -- chain33节点命令行工具
chain33.solo.toml      -- chain33配置文件
```  

 启动(window上可以装一个git bash来执行)
```  
nohup ./chain33 -f chain33.solo.toml >> para.out&  
```  

## 平行链节点部署 
下载对应系统的平行链节点安装包：  
```  
 #windows环境平行链下载包:  
https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/parachain/windows/chain33_para_windos_0670237.zip
 #linux环境平行链下载包：  
https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/parachain/linux/chain33_para_linux_0670237.tar.gz
```  

目录下包含以下三个文件  
```  
chain33                -- chain33节点程序
chain33-cli            -- chain33节点命令行工具
chain33.para.toml      -- chain33配置文件
```  

修改配置文件（chain33.para.toml），修改以下两项配置：   
```  
 #平行链名称，用来唯一标识一条平行链，  可将mbaas修改成自己想要的名称（只支持英文字符），最后一个 . 号不能省略
Title="user.p.mbaas."
 #平行链和主链的grpc连接，指向自己部署solo节点的ip地址，如果两个部署在同一台机器上，则不用改动
ParaRemoteGrpcClient="localhost:8802"
```  

启动(window上可以装一个git bash来执行)
```  
nohup ./chain33 -f chain33.para.toml >> para.out&  
```  

检查平行链的高度变化(进程启动后，等待一会后再执行)  
```  
 #当前平行链最大区块高度
./chain33-cli --rpc_laddr="http://localhost:8901" block last_header
```  

## NFT合约概述
NFT合约运行在平行链的EVM虚拟机中, EVM虚拟机运行solidity语言编写和编译的智能合约。 
Solidity语言更多信息, 请参阅  [[Solidity中文官方文档]](https://learnblockchain.cn/docs/solidity/)  
下文介绍ERC1155和ERC721两类合约最简单的使用，包括两种合约的基本介绍， 合约的编写和编译等。    [[NFT合约开发编译]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/NFT合约开发编译.md)  

## 通过SDK实现合约部署调用     
### JAVA-SDK
#### JAVA-SDK部署
适用于应用平台使用JAVA开发的情况,提供SDK对应的jar包，SDK里包含了公私钥生成,合约部署方法,合约调用方法,交易签名,交易查询,区块链信息查询等方法。  [[JAVA-SDK]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/JAVA-SDK开发环境.md)  

#### 运行JAVA Demo程序  
根据实际业务需求运行相应demo程序  
	- minByManager子目录： 合约中限制了只能是合约部署人（管理员）才可以发行NFT，适用于NFT只允许平台方发行的情况。  
	- mintByUser子目录： 合约不限制NFT的发行人，个人用户也可以上平台发行nft  

调用 [[BlockChainTest.java]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/BlockChain.java)  中的createAccount方法，生成地址和私钥  

修改ERC1155Test或ERC721Test文件，将上一步生成的内容，分别填充到以下几个参数中，注意私钥即资产，要隐私存放，而地址是可以公开的  
```  
// 管理员地址和私钥
String managerAddress = "";
String managerPrivateKey = "";

// 代扣地址和私钥
String withholdAddress = "";
String withholdPrivateKey = "";
```  

修改ERC1155Test或ERC721Test中以下两个参数
```  
// 改成自己平行链所在服务器IP地址
String ip = "";
// 改成自己平行链服务端口，对应的是配置文件里的jrpcBindAddr配置项，默认的是8901。 注意：如果远程访问，防火墙要放行此端口
int port = 8901;
```   
4. 修改平行链名称
```  
// 改成平行链环境配置中,自己设置的平行链名称
String paraName = "user.p.mbaas.";
```   
5. 运行测试程序  

### GO-SDK  
适用于应用平台使用Golang开发的情况,SDK里包含了公私钥生成,合约部署方法,合约调用方法,交易签名,交易查询,区块链信息查询等方法。 [[GO-SDK]](https://github.com/33cn/chain33-sdk-go)   

### web3.js
暂不支持

