# Chain33区块链EVM合约使用教程

## 文档目录
	- [文档修改记录](#文档修改记录)
	- [术语介绍](#术语介绍)
	- [背景介绍](#背景介绍)
	- [各区块链使用说明 ](#各区块链使用说明 )
	

### 文档修改记录
| 版本号 | 版本描述                              | 修改日期   | 备注 |
| ------ | ------------------------------------- | ---------- | ---- |
| V1.0   | 1. 通过 JAVA-SDK 在 BTY 平行链上发行 NFT<br>2. BTY平行链部署<br>3.本地solo测试环境部署 | 2022/03/09 |
| V1.0   | 1. 增加联盟链环境部署，以及通过JAVA-SDK在联盟链部署发行NFT | 2022/04/28 |
| V1.0   | 1. 增加通过代扣的方式来部署合约方法<br>2. 更新java-sdk到1.0.6 | 2022/05/16 |
| V1.0   | 1. 增加YCC链的部署及使用说明 | 2022/05/30 |

### 术语介绍 
| 序号 | 术语 缩写                              | 解释   |
| ------- | -------------------------------------- | --------------------- |
| 1   | SOLO| SOLO是最简单的共识，区块不经过投票直接产生，用在模拟区块链测试的场景中|
| 2   | 私有链| 一般采用raft共识机制，假设leader节点可信，从节点被动接受leader节点传过来的区块|
| 3   | 联盟链| 采用BFT共识机制，具备拜占庭容错能力，区块的产生需要经过节点投票，且要满足超过2/3票数一致|
| 4   | 公链| 采用POS共识机制，节点可随意加入和退出集群|
| 5   | 平行链| 平行链依附于主链，平行链之间通过名称来区分，平行链与平行链之间数据相互隔离， 平行链与主链之间通过grpc通信。|
| 6   | EVM| 以太坊虚拟机的缩写，目前EVM算是区块链中最大的生态，很多链都支持EVM的能力，Chain33也完全兼容EVM,通过EVM可以动态的部署智能合约进行计算|
| 7   | ERC721| 运行在EVM中，服务于非同质化代币（NFT）, 每个Token都是不一样的，都有自己的唯一性和独特价值,不可分割，可追踪。|
| 8   | ERC1155| 运行在EVM中，也是服务于非同质化代币(NFT),相比于ERC721它同时还支持在一个合约中存储多个数字资产，支持一次性批量发行多个不同类型的的数字资产，支持在一次转账过程中转多个不同类型的数字资产。|
| 9   | 交易组| 把两笔及以上的交易放在一个组里一次性发送。|
| 10   | 代扣手续费| 将代扣交易和正常用户的交易打包进一个交易组中，代扣交易使用代扣地址签名，用于链上手续费扣除。适用于公链主链+平行链这种需要燃料的场景|
| 11   | SDK| 封装了同区块链交互的接口和区块链通用方法（包括：公私钥生成，签名，交易构造等）, 支持java-sdk, go-sdk, web3.js等 |

### 背景介绍
Chain33本身不是一条区块链，它是一套区块链底层的开发框架, 采用插件化的架构,支持通过配置不同共识算法的插件来部署对应形态的区块链（私有链，联盟链，公链）。   私有链（raft共识）和联盟链（qbft共识）部署规模不大，支持用户自备服务器独立部署; 而公链大规模多节点的特点,对部署有比较高的要求,所以一般都非独立部署，而是通过以节点加入的方式参与到公链生态中。  

**Chain33插件库中目前支持以下几类成熟的共识插件：**   
- SOLO共识插件： 单节点简单共识，区块产生不需要投票，可在应用和区块链对接开发测试阶段采用，不建议用于生产环境。   
- RAFT共识插件： 私有链共识插件 ，采用raft共识机制，使用场景比较少。     
- QBFT共识插件： 联盟链共识插件，采用BFT类共识机制，国内联盟链项目多采用。     
- Ticket共识插件：公链共识插件，采用安全的POS机制，平均3秒左右一个区块，适用于大规模多共识节点的部署，共识节点可以方便的加入和退出。   
- POS33共识插件： 公链共识插件，采用VRF抽签投票的方式，一轮选定一部分共识节点来对区块投票，性能高，存证TPS超10000。   
- PARA共识插件：平行链共识插件，平行链不是独立存在的，它依附于主链（上述的5种都是主链），利用主链的共识算法来保证其安全性，同时平行链实现交易执行分片，主链下可以挂很多不同名称的平行链，每条平行链只负责自己独立的业务。 平行链条数的增加不会影响主链的性能，也不会影响其它平行链的性能。 比如EVM合约运行在平行链上， 而主链上只对这些交易的原始信息做共识和存证，所以主链只做存证而不用做具体的计算性能就可以很高。   
	
### 各区块链使用说明  
*基于用户自己的需求，选择相应的区块链加入：*
- Ticket共识公链（BTY公链）： 相关说明在/NFTDemo/src/test/java/com/chain33/cn/BTYNFT目录下  [[Ticket共识主链+平行链]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/BTYNFT/readme.md)  
特点： 运行时间长，从2018年主网上线运行超过4年时间; 共识节点可自由加入和退出,节点分布广且数量多，目前全网超过1700多个节点; 平均3秒左右一个区块，主网TPS300以上;  目前采用BTC格式地址。  

- POS33共识公链（YCC公链）：相关说明在 /NFTDemo/src/test/java/com/chain33/cn/YCCNFT目录下 [[POS33共识主链+平行链]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/YCCNFT/readme.md)  
特点： 2022年5月主网上线; 共识节点由抽签投票方式产生,共识节点相对集中但性能高;  平均1秒左右一个区块,主网TPS10000以上
***兼容以太坊地址,可以使用web3.js库和区块链交互，支持同小狐狸钱包对接。 但目前主网运行时长比BTY要短  ***  

- QBFT共识联盟链：相关说明在/NFTDemo/src/test/java/com/chain33/cn/ConsortiumNFT目录下 [[QBFT共识联盟链]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/ConsortiumNFT/readme.md)  
特点： 适用于联盟链的场景, 比如某家单位或一些单位组成的联盟, 对数据有一定隐私性的要求, 节点的加入和退出需要管理员审核的场景;  支持完全私有化独立部署; 平均每1秒左右一个区块; TPS2万左右; 通过工信部信通院的功能和性能测评。  

- Solo共识单节点： 相关说明在/NFTDemo/src/test/java/com/chain33/cn/SoloNFT目录下  [[SOLO共识单节点]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/SoloNFT/readme.md)     
特点： 公链和联盟链部署成本相对高，使用SOLO可以快速部署，快速开发验证，支持在windows和linux上部署。  

