# 基于Chain33区块链NFT的发行教程

### 文档修改记录

| 版本号 | 版本描述                              | 修改日期   | 备注 |
| ------ | ------------------------------------- | ---------- | ---- |
| V1.0   | 1. 通过 JAVA-SDK 在 BTY 平行链上发行 NFT<br>2. BTY平行链部署<br>3.本地solo测试环境部署 | 2022/03/09 |
| V1.0   | 1. 增加联盟链环境部署，以及通过JAVA-SDK在联盟链部署发行NFT | 2022/04/28 |
| V1.0   | 1. 增加通过代扣的方式来部署合约方法<br>2. 更新java-sdk到1.0.6 | 2022/05/16 |
| V1.0   | 1. 增加YCC链的部署及使用说明 | 2022/05/30 |

## 1. 前言
### 1.1 目的
本文档指导用户如何基于Chain33区块链开发框架部署各种类型的区块链环境，以及如何在这些环境上测试开发NFT合约
 1. 单节点开发环境（SOLO共识）  
 2. Chain33联盟链（QBFT共识）  
 3. BTY公链节点和平行链（SPOS共识）  
 4. YCC公链节点和平行链（POS共识）  

### 1.2 术语与缩略语

| 序号 | 术语 缩写                              | 解释   |
| ------ | ------------------------------------- | ---------- |
| 1   | SOLO单节点| 选用SOLO共识机制，只需一台服务器就可部署一条单节点的区块链，用于快速开发测试验证，不能用于生产|
| 2   | 联盟链| 最少4台服务器组成的联盟链网络，采用QBFT拜占庭容错共识协议|
| 3   | BTY平行链| 主链是BTY, 平行链依附于BTY主链，每条平行链都拥有自己的名称来互相区分，平行链与平行链之间数据相互隔离， 平行链与主链之间通过grpc通信。|
| 4   | YCC平行链| 主链是YCC, 平行链依附于YCC主链，每条平行链都拥有自己的名称来互相区分，平行链与平行链之间数据相互隔离， 平行链与主链之间通过grpc通信。|
| 5   | ERC721| 服务于非同质化代币（NFT）, 每个Token都是不一样的，都有自己的唯一性和独特价值,不可分割，可追踪。|
| 6   | ERC1155| 也是服务于非同质化代币(NFT),相比于ERC721它同时还支持在一个合约中存储多个数字资产，支持一次性批量发行多个不同类型的的数字资产，支持在一次转账过程中转多个不同类型的数字资产。|
| 7   | 交易组| 把两笔及以上的交易放在一个组里一次性发送。|
| 8   | 代扣手续费| 将代扣交易和正常用户的交易打包进一个交易组中，代扣交易使用代扣地址签名，用于链上手续费扣除。适用于BTY和YCC主链+平行链的场景|


## 2. 各区块链环境搭建及使用说明  
根据需求部署所需的区块链环境：  
1. BTY环境， BTYNFT目录： BTY平行链的场景（公链）,该子目录下包含BTY主链节点，以及BTY平行链环境搭建，平行链上合约部署和运行的JAVA样例。  
[[BTY主链和平行链]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/NFTDemo/readme.md)   
2. 联盟链环境，ConsortumNFT目录: 联盟链的场景，该子目录下包含4节点联盟链的搭建， 联盟链上合约部署和运行的JAVA样例。 
[[联盟链]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/ConsortiumNFT/readme.md) 
3. YCC环境， YCCNFT目录： YCC平行链的场景（公链）,该子目录下包含YCC主链节点，以及YCC平行链节点搭建，YCC平行链上合约部署和运行的JAVA样例。    
[[YCC主链和平行链环境]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/YCCNFT/readme.md)  
4. Solo单节点开发环境， SoloNFT目录：单节点验证场景，用于本地快速验证调试，该子目录下包含SOLO节点的环境搭建，以下合约部署和JAVA调用样例。
[[Solo单节点]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/SoloNFT/singleSolo/readme.md)  
[[Solo单节点+平行链]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/SoloNFT/soloAndPara/readme.md)    

