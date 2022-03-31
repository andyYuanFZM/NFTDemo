# BTY 平行链 NFT 发行教程

### 文档修改记录

| 版本号 | 版本描述                              | 修改日期   | 备注 |
| ------ | ------------------------------------- | ---------- | ---- |
| V1.0   | 1. 通过 JAVA-SDK 在 BTY 平行链上发行 NFT<br>2. BTY平行链部署<br>3.本地solo测试环境部署 | 2022/03/09 |

## 1. 前言
### 1.1 目的
本文档主要用于指导用户在自己的平行链上，通过JAVA-SDK发行ERC1155和ERC721的NFT，以及如何调用接口进行NFT的转账和NFT属性查询等能力。

### 1.2 术语与缩略语

| 序号 | 术语 缩写                              | 解释   |
| ------ | ------------------------------------- | ---------- |
| 1   | BTY平行链| BTY平行链依附于BTY主链，每条平行链都拥有自己的名称来互相区分，平行链与平行链之间数据相互隔离， 平行链与主链之间通过grpc通信。|
| 2   | ERC721| 服务于非同质化代币（NFT）, 每个Token都是不一样的，都有自己的唯一性和独特价值,不可分割，可追踪。|
| 3   | ERC1155| 也是服务于非同质化代币(NFT),相比于ERC721它同时还支持在一个合约中存储多个数字资产，支持一次性批量发行多个不同类型的的数字资产，支持在一次转账过程中转多个不同类型的数字资产。|
| 4   | 交易组| 把两笔及以上的交易放在一个组里一次性发送。|
| 5   | 代扣手续费| 将代扣交易和正常用户的交易打包进一个交易组中，代扣交易使用代扣地址签名，用于链上手续费扣除。|

## 2. 文档目录介绍
1. btyPara目录： 在bty平行链上运行demo的方法，包含如何部署bty公链下的平行链，平行链如何连接主链，如何运行demo程序  
1.1 minByManager子目录：合约中限制了只能是合约部署人（管理员）才可以发行NFT，适用于NFT只允许平台方发行的情况。  
1.2 mintByUser子目录：合约不限制NFT的发行人，个人用户也可以上平台发行nft  
具体选用哪种方式，依据实际的业务特性而定  
此目录下中的方式是直接接入到正式公链上，是一个真实的环境， 任何交易上链都涉及手续费， 所以在运行demo前，需要按子目录文档里说明，给相应的管理员地址和代扣地址充值手续费。   其中管理员地址可以放少量手续费, 代扣地址下手续费需要多放一些。  
 + 管理员地址： 合约部署人的地址  
 + 代扣地址：给平台所有用户交易（比如转移nft）做手续费代扣用，这样用户可以不用关注公链上的燃料  

2. localTest目录：用于本地快速验证调试，可在自己的window或linux机器上部署测试区块链  
2.1 singleSolo子目录： 部署一个单节点的主链， 用于开发测试，体验合约部署调用流程。  
2.2 soloAndPara子目录： 用于部署一个单节点的主链，再基于这个主链部署一个平行链节点（可以在同一台服务器上部署，window和linux皆可）,然后NFT合约部署在平行链上。  
此目录中的方式是单节测试时使用，默认关闭了主链上的手续费（交易里手续费的值正常设置，但是主链不会收取，地址上没有燃料交易也能正常上链）。  

3. BlockChainTest.java中包含了一些链上的查询方法，如果项目中有用到，可以参考：    
3.1 getBalance -- 取用户地址在主链上的燃料值（注意要连主链，非平行链）  
3.2 getLastHeight -- 取平行链目前最大区块高度（注意要连平行链，如果连到主链的rpc端口，取下来的值就是主链的高度了）   
3.3 validateAddress -- 校验区块链地址是否合法（区块链地址不能简单通过长度判断是否合法，需要满足base58编码形式） 

## 3. 基于JAVA-SDK搭建NFT开发环境
### 3.1 下载JAVA-SDK
1. 下载最新版本的JAVA-SDK包[[下载链接]](https://github.com/33cn/chain33-sdk-java/releases/download/1.0.15/chain33-sdk-java-1.0.15.zip)  
2. 解压JAVA-SDK压缩包。  
3. 将SDK压缩包中的JAR包安装到本地仓库  
```
# 在jar包所在目录，执行如下命令
mvn install:install-file -Dfile=chain33-sdk-java.jar -DgroupId=cn.chain33 -DartifactId=chain33-sdk-java -Dversion=1.0.15 -Dpackaging=jar
```
执行结果中打印BUILD SUCCESS，表明添加成功。
如果因time out导致构建失败，可以再次执行以上命令，直至构建成功。

### 3.2 引用JAVA-SDK
在现有JAVA项目中导入JAVA-SDK以及相关依赖, 下文以新建一个工程为例说明。

1. 使用IDE创建一个基于Maven构建的工程（新建工程时的GroupId、ArtifactId、Version等参数根据实际需求设置）  
2. 在工程的pom.xml中添加下述依赖,并进行依赖安装  
```
<dependencies>
       <dependency>
         <groupId>cn.chain33</groupId>
         <artifactId>chain33-sdk-java</artifactId>
         <version>1.0.15</version>
       </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>2.17.1</version>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>3.16.1</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.47</version>
        </dependency>
        <dependency>
            <groupId>org.bitcoinj</groupId>
            <artifactId>bitcoinj-core</artifactId>
            <version>0.14.7</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.13</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>1.67</version>
        </dependency>
        <dependency>
            <groupId>net.vrallev.ecc</groupId>
            <artifactId>ecc-25519-java</artifactId>
            <version>1.0.3</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-all</artifactId>
            <version>1.34.1</version>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>30.0-jre</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-collections4</artifactId>
            <version>4.4</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.12.4</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.12.4</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.12.4</version>
        </dependency>
</dependencies>
```
如果导入依赖缓慢，或出现Connection timed out的报错信息，则可能是因为默认中央仓库下载超时，可以切换成mvn的阿里云镜像重试。  
```
<!—阿里云镜像 -->
<mirror> 
    <id>alimaven</id> 
    <name>aliyun maven</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public/</url> 
    <mirrorOf>central</mirrorOf>         
</mirror>
```
如果结果中打印BUILD SUCCESS则表明执行成功；否则根据报错信息检查并修复错误  

## 4. NFT合约开发
NFT(非同质化数字资产)是具有唯一且彼此不可替换属性的数字资产，具有标准化、通用性、流动性以及可编程特性，常见的应用场景包括收藏品、游戏物品、数字艺术、证书、域名等。NFT不像BTC或ETH这些可以分割成0.1或是0.02，NFT的单位永远是1，唯一性和稀缺性是它的典型特征。  
NFT因为2017年的以太猫而大火，同时开创了第一个NFT的标准：ERC721。 之后又由Enjin公司在ERC721的基础上制订了ERC1155的标准，它支持在一个合约中存储多个数字资产，支持一次性批量发行多个不同类型的的数字资产，支持在一次转账过程中转多个不同类型的数字资产。  
BTY平行链上的EVM虚拟机也完全兼容上述的NFT标准，下文提供ERC721和ERC1155两类合约的样例，它们已经实现了最基础的NFT发行，转让以及NFT相关属性的查询等功能。  

注：  
1. 下文两个示例只提供最基础功能，如果有更多的业务需求，需要基于样例再结合实际的业务需求自行进行二次开发。 
2. Solidity语言更多信息，请参见[[Solidity官方文档]](https://learnblockchain.cn/docs/solidity/)  

### 4.1 ERC721合约
#### 4.1.1 场景设计
假设要对一批名画发行链上NFT资产通证， 每一幅画有且仅有一个NFT通证对应。 每一个NFT通证都可以在区块链上进行流转。通过用户地址可以查看用户NFT资产的持有数量；通过通证ID可以查看到画在网络/区块链上的存储地址。

#### 4.1.2 ERC721合约代码
```
pragma solidity 0.8.0;

import "https://github.com/0xcert/ethereum-erc721/src/contracts/tokens/nf-token-metadata.sol";

contract newERC721 is NFTokenMetadata {

    address public _owner;

  /**
   * @dev 构造函数,可设定token名称和token symbol.
   */
  constructor(string memory _name, string memory _symbol) {
    nftName = _name; 
    nftSymbol = _symbol;
    _owner = msg.sender;
  }

  /**
   * @dev 发行NFT,限定只有合约部署人才可以调用
   * @param _to NFT发行在哪个地址下
   * @param _tokenId NFT的tokenid(整型)
   * @param _uri token uri信息
   */
  function mint(address _to, uint256 _tokenId, string calldata _uri) external {
    require(msg.sender == _owner, "only authorized owner can mint nft.");
    super._mint(_to, _tokenId);
    super._setTokenUri(_tokenId, _uri);
  }
}
```

#### 4.2 ERC1155合约
#### 4.2.1 场景设计
假设对游戏中某件稀有道具发行一定数量的NFT资产（大于1件），同时可支持一次性批量转移多件NFT  
#### 4.2.2 合约设计
1. 支持批量发行NFT的合约接口，用户可传入NFT的编号列表，每个NFT的数量列表来批量发行NFT,同时合约限定只有合约部署者才可以发行资产。  
2. 支持根据用户地址查询NFT资产
#### 4.2.3 智能合约代码
```
pragma solidity ^0.8.0;
import "github.com/OpenZeppelin/openzeppelin-contracts/blob/master/contracts/token/ERC1155/ERC1155.sol";

contract newERC1155 is ERC1155 {

    address public _owner;
    constructor() public  ERC1155("") {
        _owner = msg.sender;
    }
    
    /**
     * 初始化NFT资产
     * _to:NFT发行在哪个地址下
     * ids: NFT资产数组
     * amounts: NFT数额，和上面的ids长度要保持一致，并且一一对应
     */
    function mint(address _to, uint256[] memory ids, uint256[] memory amounts) external {
        require(msg.sender == _owner, "only authorized owner can mint NFT.");
        _mintBatch(_to, ids, amounts, "");
    }

    /**
     * 转让NFT
     * to: 转让的去向地址
     * id: NFT编号
     * amount: 转让数量
     */
    function transferArtNFT(address to, uint256 id, uint256 amount) external {
        // 转账
        safeTransferFrom(msg.sender, to, id, amount, "");
    }
}
```

# 5. NFT合约部署调用
应用和区块链交互流程： 
![Image text](https://github.com/andyYuanFZM/NFTDemo/blob/main/src/test/java/com/chain33/cn/NFTDemo/resource/flow.png)  
1. 用户在区块链上注册，应用层调用JAVA-SDK中的创建公私钥方法，生成私钥和区块链地址。 然后将它们在应用层数据库中和用户关联，这样每一个用户都有了一个区块链上的身份。  
2. NFT合约部署，调用JAVA-SDK构造部署合约的交易并签名该交易上链，一般而言NFT合约的部署是一次性动作（在没有合约逻辑变动，部署好后就固定运行在区块链上）， 建议合约的部署由专门的管理员来操作。管理员私钥和地址，也需要安全的存放在数据库中。  
3. NFT资产发行，调用JAVA-SDK构造并签名发行NFT的交易，在合约中NFT资产的发行也是限定了只允许管理员才可以操作。  
4. NFT资产转移，调用JAVA-SDK构造并使用用户私钥签名转让NFT资产的交易  

注：   
1. 所有交易上链后会实时的返回一笔hash，这个hash是由rpc接口计算出来实时返回的。但此时并不代表交易已经成功上链（还有后续的共识，交易执行还在处理中），所以需要应用层异步的去根据这个hash查询上链结果，确认上链成功后将这个hash在数据库中和业务数据绑定。  
2. 发往BTY平行链上的每一笔交易都需要扣除燃料费，手续费从用户私钥对应的地址下扣除。 所以上述流程步骤2和步骤3的管理员地址下需要有充足的BTY作为手续费， 步骤4用户地址下也需要有充足的BTY作为手续费。 如果不方便平台用户持有BTY，那么我们也提供了一个代扣的方式， 平台准备一个代扣地址，里面存入充足的BTY， 所有用户转账的交易都由这个代扣地址来承担手续费。  

## 5.1 NFT合约编译
将上述两份合约代码分别拷贝到在线IDE中, 使用[[remix]](https://remix.ethereum.org/)  
从IDE中编译获取到ABI和bytecode，为下一步合约部署上链做准备。  
注：  
下文【附录】里面包含了合约代码，JAVA部署调用合约代码，以及两份合约编译好的ABI和bytecode，不需要做合约二次开发的可以直接引用。

## 5.2 合约部署调用流程
以下以ERC115合约为例来说明，ERC721的类似，具体用法见对应的样例

### 5.2.1 创建私钥，地址信息
调用newAccountLocal接口用于生成私钥，公钥，地址等信息。 
```
// 函数原型
public AccountInfo newAccountLocal()
```
### 5.2.2 预估GAS费
调用queryEVMGas，估算gas费用。但这一步会产生耗时，所以在实际应用过程中，不建议在业务代码中调用gas费，可以先通过此方法预估gas费，然后再判断应该在代码中设置多少手续费
```
// 函数原型
public long queryEVMGas(String execer, String tx, String address)
```
### 5.2.3 部署EVM合约
调用createEvmContract往区块链上部署EVM合约
```
// 函数原型
public static String createEvmContract(byte[] code, String note, String alias, String privateKey, String paraName)
```
### 5.2.4 调用EVM合约中方法
通过callEvmContract调用EVM合约方法
```
// 函数原型
public static String callEvmContract(byte[] parameter, String note, long amount, String contractAddr, String privateKey, String paraName)
```
### 5.2.5 合约查询功能
通过callEVMAbi方法，调用EVM合约中的查询方法
```
// 函数原型
public JSONObject callEVMAbi(String address, String abiPack)
```



