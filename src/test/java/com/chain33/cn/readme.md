# 基于Chain33区块链NFT的发行教程

### 文档修改记录

| 版本号 | 版本描述                              | 修改日期   | 备注 |
| ------ | ------------------------------------- | ---------- | ---- |
| V1.0   | 1. 通过 JAVA-SDK 在 BTY 平行链上发行 NFT<br>2. BTY平行链部署<br>3.本地solo测试环境部署 | 2022/03/09 |
| V1.0   | 1. 增加联盟链环境部署，以及通过JAVA-SDK在联盟链部署发行NFT | 2022/04/28 |

## 1. 前言
### 1.1 目的
本文档指导用户如何部署Chain33单节点开发环境（SOLO共识）， Chain33联盟链（QBFT共识），Chain33平行链（BTY公链下的平行链），以及如何这三种环境下部署
并运行NFT智能合约（ERC1155和ERC721）

### 1.2 术语与缩略语

| 序号 | 术语 缩写                              | 解释   |
| ------ | ------------------------------------- | ---------- |
| 1   | SOLO单节点| 选用SOLO共识机制，只需一台服务器就可部署一条单节点的区块链，用于快速开发测试验证，不能用于生产|
| 2   | 联盟链| 最少4台服务器组成的联盟链网络，采用QBFT拜占庭容错共识协议|
| 3   | BTY平行链| BTY平行链依附于BTY主链，每条平行链都拥有自己的名称来互相区分，平行链与平行链之间数据相互隔离， 平行链与主链之间通过grpc通信。|
| 4   | ERC721| 服务于非同质化代币（NFT）, 每个Token都是不一样的，都有自己的唯一性和独特价值,不可分割，可追踪。|
| 5   | ERC1155| 也是服务于非同质化代币(NFT),相比于ERC721它同时还支持在一个合约中存储多个数字资产，支持一次性批量发行多个不同类型的的数字资产，支持在一次转账过程中转多个不同类型的数字资产。|
| 6   | 交易组| 把两笔及以上的交易放在一个组里一次性发送。|
| 7   | 代扣手续费| 将代扣交易和正常用户的交易打包进一个交易组中，代扣交易使用代扣地址签名，用于链上手续费扣除。|

## 2. 文档目录介绍
1. ConsortumNFT: 4节点联盟链的搭建，以及如何在联盟链上部署运行NFT合约，  适用于联盟链业务场景。
2. NFTDemo目录： 在bty平行链上运行demo的方法，包含如何部署bty公链下的平行链，平行链如何连接主链，如何运行demo程序 ，适用于公链业务场景。  
3. localTest目录：用于本地快速验证调试，可在自己的window或linux机器上部署测试区块链。  
4. BlockChainTest.java中包含了一些链上的查询方法，如果项目中有用到，可以参考：    
&nbsp; 4.1 getBalance -- 取用户地址在主链上的燃料值  
&nbsp; 4.2 getLastHeight -- 取平行链目前最大区块高度（注意要连平行链，如果连到主链的rpc端口，取下来的值就是主链的高度了）   
&nbsp; 4.3 validateAddress -- 校验区块链地址是否合法（区块链地址不能简单通过长度判断是否合法，需要满足base58编码形式） 

## 3. 区块链环境搭建  
根据需求部署所需的区块链环境：  
1. 搭建BTY平行链环境：    
[[BTY平行链环境]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/NFTDemo/readme.md)   
2. 搭建Solo单节点开发环境：  
[[Solo单节点]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/SoloNFT/singleSolo/readme.md)  
[[Solo单节点+平行链]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/SoloNFT/soloAndPara/readme.md)   
3. 搭建联盟链环境：

## 4. 搭建JAVA-SDK开发环境  
参考： [[搭建JAVA-SDK开发环境]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/JAVA-SDK开发环境.md) 

## 5. NFT合约开发部署
参考： [[NFT合约开发部署]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/NFT合约开发部署.md) 
