# QBFT共识（联盟链）+平行链使用说明

## 目录
	- [说明](#说明)
	- [联盟链环境部署 ](#联盟链环境部署)
	- [平行链节点部署](#平行链节点部署)
	- [NFT合约概述](#NFT合约概述)
	- [通过SDK实现合约部署调用](#通过SDK实现合约部署调用) 
	- [应用对接注意事项](#应用对接注意事项) 
	
## 说明
联盟主链采用了QBFT共识机制，支持拜占庭容错，节点根据权重（可配置）对区块进行投票确认, 节点数要满足N>3f，所以至少3f+1个节点（f代表错误节点,f最小取1,所以联盟链最少需要4个节点）。 
主链+平行链交易流程：  
- 交易在链下完成构造和签名,交易构造时需要在交易体中带上对应平行链的名称。   
- 签好名的交易通过平行链的jsonrpc接口发往平行链节点。   
- 平行链通过它和主链之间的grpc连接,将交易转发到主链节点,由主链打包区块共识后存入主链账本。   
- 主链区块生成后,平行链实时拉取新产生的区块,过滤出属于本平行链的交易（根据平行链名称）, 送入虚拟机执行后并写入平行链账本。  
下面介绍4节点联盟主链和平行链节点的部署，智能合约部署和调用方法。  
+ 注： 支持在同一台服务器上同时部署联盟主链节点和平行链节点（只要保证两者的jsonrpc和grpc端口不冲突即可）   

## 联盟链环境部署  
[[联盟链环境部署]](https://chain.33.cn/document/274)   

## 平行链节点部署 
主链部署完，才可以部署平行链。  
- 下载平行链，解压压缩包，并进入目录(支持在任意一台主链节点上部署，也支持单独服务器部署)
```  
wget https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/parachain/linux/chain33_para_linux_0670237.tar.gz  
tar -zxvf chain33_para_linux_0670237.tar.gz  
cd chain33_para_linux_0670237  
```  

- 目录下包含以下三个文件  
```  
chain33                -- chain33节点程序
chain33-cli            -- chain33节点命令行工具
chain33.para.toml      -- chain33平行链配置文件
```  

- 修改配置文件（chain33.para.toml），修改以下几个配置项：  
```  
 #平行链名称，用来唯一标识一条平行链，  可将mbaas修改成自己想要的名称（只支持英文字符），最后一个 . 号不能省略
Title="user.p.mbaas."
 #主链的grpc地址，可以临时使用官方的grpc连接：ParaRemoteGrpcClient="jiedian2.bityuan.com,cloud.bityuan.com"
 # 主链的grpc地址,填写主链的IP:8802
ParaRemoteGrpcClient="localhost:8802"
 #指示从主链哪个高度开始同步，刚部署完的联盟链区块高度是1，所以平行链也可以配置成1
startHeight=1
```  

- 启动平行链
```  
nohup ./chain33 -f chain33.para.toml >> para.out&  
```  

- 检查平行链和主链的同步状态(进程启动后，等待一会后再执行)  
```
 #返回true代表同步完成
./chain33-cli --rpc_laddr="http://localhost:8901" para is_sync
 # 当前平行链最大区块高度
./chain33-cli --rpc_laddr="http://localhost:8901" block last_header
```  

## NFT合约概述
NFT合约运行在平行链的EVM虚拟机中, EVM虚拟机运行solidity语言编写和编译的智能合约。 
Solidity语言更多信息, 请参阅  [[Solidity中文官方文档]](https://learnblockchain.cn/docs/solidity/)  
下文介绍ERC1155和ERC721两类合约最简单的使用，包括两种合约的基本介绍， 合约的编写和编译等。    [[NFT合约开发编译]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/NFT合约开发编译.md)  

## 通过SDK实现合约部署调用     
和公链每一笔交易都要收取手续费不一样, 联盟链默认是把手续费功能关闭的，用户在交易上链时不需要考虑地址下有没有燃料的问题，交易用私钥签名后就可以直接上链了。  
### JAVA-SDK
#### JAVA-SDK部署
适用于应用平台使用JAVA开发的情况,提供SDK对应的jar包，SDK里包含了公私钥生成,合约部署方法,合约调用方法,交易签名,交易查询,区块链信息查询等方法。  [[JAVA-SDK]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/JAVA-SDK开发环境.md)  

#### 运行JAVA Demo程序  
1. 调用 [[BlockChainTest.java]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/BlockChain.java)  中的createAccount方法，生成地址和私钥
2. 修改ConsortiumNFT/ERC1155Test.java文件，将上一步生成的内容，分别填充到以下几个参数中，注意私钥即资产，要隐私存放，而地址是可以公开的
```  
// 管理员地址和私钥
String managerAddress = "";
String managerPrivateKey = "";
```  
3. 修改ConsortiumNFT/ERC1155Test.java中以下两个参数
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

## 5. 应用对接注意事项   
交易上链失败的情况：  
1. 交易上链了，但交易执行失败（有返回交易hash）：   这类交易通过了mempool（交易缓存池）的合法性检查，但是在合约执行过程中失败了（ 比如转移了错误数量的NFT）。
2. 交易没有上链（没有返回交易hash，rpc接口直接返回出错信息）： 这类交易在mempool的合法性检查中没有通过，包括以下以类错误：  
	- 签名错误（ErrSign）： 签名校验不通过，一般不会遇到，除非人为去改交易内容。  -- 不常见   
	- 交易重复（ErrDupTx）：mempool中发现重复交易，一般不会遇到，除非人为发送重复交易（所谓重复是hash完全一模一样的两笔交易，而不是指业务上数据相同）。 -- 不常见   
	- 交易账户在mempool中存在过多交易（ErrManyTx）： 区块链为防止来自于同一个地址的频繁交易，限制每个账户在mempool中的最大交易数量不能超过100， 所以当单个用户交易频率很高时，短时间内超过100笔，100笔以后的交易会被丢弃（rpc返回errmanytx的错）从而导致关联的交易也被丢弃。   -- 有可能会遇到   

举例说明应用层和区块链整合后的一般处理方案：  
为保证数据的一致性，需要判断交易在区块链上确实成功（拿到交易hash，且实际交易的执行结果是：ExecOk）, 业务上才能判定为成功。   具体见下面处理流程以及测试用例：  

场景：NFT平台上新品，大量用户抢购。  
处理流程：  
1. 用户完成支付，应用层触发NFT转账动作，并拿到区块链返回的交易hash(在采用代扣的情况下，这个hash对应的是代扣手续费交易，而非实际转NFT交易)  
2. 由于区块链是异步处理且有一定时延，这时NFT资产还没有真正转到用户地址下，但应用层可以预先将该笔订单标记为【待完成】。 这个状态不用给用户显示，只由应用层记录，对于用户而言，他只要一完成支付，就视为成功，不需要让他等待区块链的处理结果。  
3. 应用层把第1步中拿到的hash放到处理队列中，定时拿这个hash去查结果，如果查询结果返回空，hash继续留在队列中等待下一次查询， 如果结果不为空，从查询的返回结果拿到执行结果，如果是ExecOk代表执行成功，应用层再【待完成】的订单标记为【完成】，并从处理队列中删除第一笔交易的hash。 如果结果是ExecPack，代表执行失败，这个一般可能是业务上的bug(比如转了超过地址下数量的NFT资产，或是权限不够等等)，这种情况后续怎么处理由应用层根据实际情况来判断。    
4. 其它异常  
4.1 数据上链后，没有拿到交易hash，且rpc返回ErrManyTx。  有大量交易上来时，应用层用队列缓存发送，比如每间隔10秒发送100笔交易，这样基本能解决ErrManyTx问题，如果再有个别交易还发生ErrManyTx，也需要把失败的再放回队列，后面重新发送。  
4.2 数据上链后，没有拿到交易hash，且rpc返回ErrTxMsgSizeTooBig， 代表交易体太大。 一般出现于ERC1155批量mint一批NFT时， 建议一次mint的token数量不要超过1000个， 数量比较大的，可以分多次来mint。  