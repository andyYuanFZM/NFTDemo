# NFT合约开发
NFT(非同质化数字资产)是具有唯一且彼此不可替换属性的数字资产，具有标准化、通用性、流动性以及可编程特性，常见的应用场景包括收藏品、游戏物品、数字艺术、证书、域名等。NFT不像BTC或ETH这些可以分割成0.1或是0.02，NFT的单位永远是1，唯一性和稀缺性是它的典型特征。  
NFT因为2017年的以太猫而大火，同时开创了第一个NFT的标准：ERC721。 之后又由Enjin公司在ERC721的基础上制订了ERC1155的标准，它支持在一个合约中存储多个数字资产，支持一次性批量发行多个不同类型的的数字资产，支持在一次转账过程中转多个不同类型的数字资产。  
Chain33区块链上的EVM虚拟机也完全兼容上述的NFT标准，下文提供ERC721和ERC1155两类合约的样例，它们实现了最基础的NFT发行，转让以及NFT相关属性的查询等功能。  

注：  
1. 下文两个示例只提供最基础的资产发行，转移，查询等功能，如果有更多的业务需求，需要基于样例再结合实际的业务需求自行进行二次开发。 
2. Solidity语言更多信息，请参见[[Solidity官方文档]](https://learnblockchain.cn/docs/solidity/)  

## ERC721合约
### 场景设计
假设对一批艺术品（数字画作）发行链上NFT资产通证， 每一幅画有且仅有一个NFT通证对应。 每一个NFT通证都可以在区块链中流转。  
可以根据用户地址查询他NFT资产的持有数量。  
可以通过NFT的ID查看到数字画在网络/区块链上的存储位置。  

### 合约编写思路
1. 合约初始化时设定通证名称（name）和符号(symbol)，以及定义此合约的拥有者（可以限制哪些接口只能拥有者才能调用）。  name和symbol可以和某类作品对应，比如【敦煌飞天数字藏品】或【无聊猿NFT】等，而每一类下又可以发行多个通证，每个通证都有唯一的编号。  
2. 提供NFT通证发行的接口，在发行接口中可以定义是否只能允许合约拥有者才能调用， 同时在发行时要指定通证发行到哪个用户地址下，以及当前通证对应的数字画作本身以及描述信息存储位置的URL信息。   
3. 其它： NFT的转让，NFT的查询等接口。 这些接口在引用的sol文件中都有了定义，在没有业务定制情况下，可以直接引用，不需要重写接口。   

### ERC721合约代码
```
pragma solidity 0.8.1;

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

### ERC1155合约
### 场景设计
假设对一批艺术品（数字画作）发行链上NFT资产通证,支持同一个NFT ID下有多份数量（完全一样的一幅画发行了超过1份的数量）， 且支持对NFT资产的批量转移到另一个用户名下。 

### 合约编写思路
1. 支持批量发行NFT的合约接口，用户可传入NFT的编号列表，每个NFT的数量列表，以及NFT的属性（描述信息，图片存放位置等）来批量发行NFT,同时合约限定只有合约部署者才可以发行资产。  
2. 其它：NFT的转让，NFT的查询等接口。     

### ERC1155合约代码
```
// SPDX-License-Identifier: SimPL-2.0
pragma solidity ^0.8.1;
import "github.com/OpenZeppelin/openzeppelin-contracts/blob/master/contracts/token/ERC1155/ERC1155.sol";

contract newERC1155 is ERC1155 {

    address public _owner;
    mapping(uint256 => string) private _tokenURI;
    
    constructor() public  ERC1155("") {
        _owner = msg.sender;
    }
    
    /**
     * 初始化NFT资产
     * _to:NFT发行在哪个地址下
     * ids: NFT资产数组
     * amounts: NFT数额，和上面的ids长度要保持一致，并且一一对应
     * uris: NFT的URI信息，和上面的ids长度要保持一致，并且一一对应
     */
    function mint(address _to, uint256[] memory ids, uint256[] memory amounts, string[] memory uris) external {
        require(msg.sender == _owner, "only authorized owner can mint NFT.");
        require(ids.length == amounts.length, "The ids and amounts are not match");
        require(ids.length == uris.length, "The ids and uris are not match");
        _mintBatch(_to, ids, amounts, "");
        if (uris.length > 0) {
            for (uint256 i = 0; i < ids.length; i++) {
                _setURI(ids[i], uris[i]);
            }
        }
        
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

    /**
     * 设置NFT URI信息
     * id: NFT编号
     * uri: URI信息
     */
    function _setURI(uint256 _id, string memory _uri) internal {
        _tokenURI[_id] = _uri;
    }

    function uri(uint256 _id) public view virtual override returns (string memory) {
        return _tokenURI[_id];
    }
    
}
```

# NFT合约部署调用流程
应用和区块链交互流程： 
![Image text](https://github.com/andyYuanFZM/NFTDemo/blob/main/src/test/java/com/chain33/cn/resource/flow.png)  
1. 用户在区块链上注册，应用层调用JAVA-SDK中的创建公私钥方法，生成私钥和区块链地址。 然后将它们在应用层数据库中和用户关联，这样每一个用户都有了一个区块链上的身份。  
2. NFT合约部署，调用JAVA-SDK构造部署合约的交易并签名该交易上链，一般而言NFT合约的部署是一次性动作（在没有合约逻辑变动，部署好后就固定运行在区块链上）， 建议合约的部署由专门的管理员来操作。管理员私钥和地址，也需要安全的存放在数据库中。  
3. NFT资产发行，调用JAVA-SDK构造并签名发行NFT的交易。  
4. NFT资产转移，调用JAVA-SDK构造并使用用户私钥签名转让NFT资产的交易。 
5. 查询动作，调用JAVA-SDK查询NFT相关信息。 

注：   
1. 所有交易上链后会实时的返回一笔hash，这个hash是由rpc接口计算出来实时返回的。但此时并不代表交易已经成功上链（还有后续的共识，交易执行还在处理中），所以需要应用层异步的去根据这个hash查询上链结果，确认上链成功后将这个hash在数据库中和业务数据绑定。  
2. 关于链上的燃料费：
&nbsp; 2.1 BTY平行链场景：  发往BTY平行链上的每一笔交易都需要扣除燃料费，燃料费计算和扣除方式见NFTDemo目录下的readme文档。    
&nbsp; 2.2 solo场景：这种场景用于开发和测试，默认配置是关闭手续费功能，交易上链不用考虑手续费的问题。  
&nbsp; 2.3 联盟链的场景： 联盟链在实际生产部署时，可以选择打开或关闭手续费的功能。 具体操作见相应目录下的readme文档。

## NFT合约编译
将上述两份合约代码分别拷贝到在线IDE中, 使用[[remix]](https://remix.ethereum.org/), 注意编译器版本要和代码上的一致（选用0.8.1）  
从IDE中编译获取到ABI和bytecode，为下一步合约部署上链做准备。  
<font color="red"> 注：  各目录下里面包含了智能合约代码，JAVA部署调用合约代码，以及两份合约编译好的ABI和bytecode，在不需要做合约二次开发的情况下可以直接拷贝引用。</font>

## 合约部署调用流程
以下以ERC115合约为例来说明，ERC721的类似，具体用法见对应的样例

### 1. 创建私钥，地址信息
调用newAccountLocal接口用于生成私钥，公钥，地址等信息。 
```
// 函数原型
public AccountInfo newAccountLocal()
```  

### 2. 预估GAS费
调用queryEVMGas，估算gas费用。但这一步会产生耗时，所以在实际应用过程中，不建议在业务代码中每一次数据上链都调用这个接口，可以先通过此方法预估不同类型交易的gas费（mint交易，transfer交易等），然后再在这个值的基础上再加上0.001后，设置成手续费。
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