# BTY环境及使用说明  
mintByManager目录下的NFT合约只支持管理员来发行NFT，在合约的mint方法中，限制了只允许合约的部署人（管理员）才能允许调用。  适用于平台对于NFT发行有严格限制的业务场景。   
mintByUser此目录下的NFT合约不限制只有管理员才能发行，任何用户都可以调用mint方法发行NFT， 适用于平台任意作者都可以发行NFT的业务场景。   
deployByUser此目录下的用例支持任意用户部署NFT合约，适用于平台支持每一个艺术家都可以部署自己的智能合约。

## 1. 环境部署：  
支持在同一台服务器上同时部署BTY主链节点和BTY平行链节点（只要保证两者的jsonrpc和grpc端口不冲突即可）  
### 1.1 BTY主链部署  
- 1.1.1 准备一台4核8G的linux服器（ ubuntu或Centos都可），硬盘>300G （同步速度SSD硬盘效率要远远高于机械盘，根据自己情况选择硬盘类型）  
- 1.1.2 从 https://github.com/bityuan/bityuan/releases 下载最新版本的release运行(比如以当前最新版本6.8.0来说明)        
```  
# 下载
wget https://github.com/bityuan/bityuan/releases/download/v6.8.0/bityuan-linux-amd64.tar.gz
# 新建目录
mkdir bityuan
# 解压
tar -zxvf bityuan-linux-amd64.tar.gz -C bityuan
```  
- 1.1.3 目录下包含以下几个文件  
```  
bityuan-linux-amd64                -- BTY节点程序
bityuan-cli-linux-amd64            -- BTY节点命令行工具
bityuan.toml                       -- bityuan配置文件（带数据分片功能，占空间小），后面启动时用这个配置
bityuan-fullnode.toml              -- bityuan配置文件（全节点模式，占空间大）
```  

- 1.1.4 修改配置  
```  
[blockchain]
 # 默认是false, 需要改成true， 此参数含义是主链给平行链同步区块sequence信息，如果不开启，平行链无法同步  
isRecordBlockSequence=true  
[rpc]  
 # 默认只限制localhost访问，想要不限制，可以去掉localhost,只保留:8801  
jrpcBindAddr="localhost:8801"
 # 默认只限制localhost访问，想要不限制，可以去掉localhost,只保留:8802  
grpcBindAddr="localhost:8802"  
 # 白名单IP，如果不限制，把127.0.0.1改成 *  
whitelist=["127.0.0.1"] 
```  

- 1.1.5 启动主链程序
```  
nohup ./bityuan-linux-amd64 -f bityuan.toml >> bty.out&
```  

- 1.1.6 检查主链的同步状态(进程启动后，等待一会后执行) 
```  
#  主要看返回信息中自己节点的height信息， 和主链最大高度一致代表同步成功。  这一过程时间比较长，按目前2000万左右的区块高度， SSD硬盘同步需要三天左右时间， 普通机械硬盘耗时可能翻倍
 ./bityuan-cli-linux-amd64 net peer info  
```   

- 1.1.7 创建钱包，接收空投 (这一步可以在主链节点还没有同步完情况下执行)   
每一笔交易上链，都需要支付BTY作为手续费， 前期测试交易量比较少的情况下，可以用空投的BTY来充当手续费
```  
# 生成助记词，以下命令执行完返回的一串英文字符串就是助记词  
./bityuan-cli-linux-amd64 seed generate -l 0 
# 保存助记词
./bityuan-cli-linux-amd64 seed save -s "上一步返回的助记词" -p 钱包密码（必须小写字母+数据的组合，比如abcd1234这种）
# 解锁钱包  
./bityuan-cli-linux-amd64 wallet unlock -p 上一步设置的钱包密码
# 查看钱包里资产， 其中标签名为dht node award和airdropaddr两个地址下，每天都会收到BTY空投， 可以覆盖测试用的手续费
./bityuan-cli-linux-amd64 account list
```  

- 1.1.8 转移空投地址下的BTY到另外的地址(只有在主链节点同步完后，才能看到空投资产，所以这一步要在同步完后再执行,同步过程中执行会报一个：ErrNotSync的错)   
```  
# 查看空投地址的私钥
./bityuan-cli-linux-amd64 account dump_key -a 空投地址
# 将BTY从空投地址转到另外地址下（分三步，构造交易，签名，发送）
./bityuan-cli-linux-amd64  coins transfer -a 100 -n test -t  用户另外的地址
./bityuan-cli-linux-amd64  wallet sign -k 空投地址的私钥 -d 上一步返回的数据
./bityuan-cli-linux-amd64  wallet send -d 上一步返回的数据
# 查看转账交易的执行结果
通过区块链浏览器，输入地址或上一步返回的转账交易hash值，去浏览器上查询   
浏览器地址： https://mainnet.bityuan.com/home
```   
 
### 1.2 平行链部署 （在主链部署完成后进行）  
因为目前bty主链已经有100多G的数据， 同步时间会比较长，所以为了方便开发者验证，可以在主链同步过程中临时使用官方的对外接口做测试验证， 具体见2.3中的说明 。  
- 1.2.1  下载，解压压缩包，并进入目录  
```  
wget https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/parachain/linux/chain33_para_linux_0670237.tar.gz  
tar -zxvf chain33_para_linux_0670237.tar.gz  
cd chain33_para_linux_0670237  
```  

- 1.2.2 目录下包含以下三个文件  
```  
chain33                -- chain33节点程序
chain33-cli            -- chain33节点命令行工具
chain33.para.toml      -- chain33平行链配置文件
```  

- 1.2.3 修改配置文件（chain33.para.toml），修改以下几个配置项：  
```  
 #平行链名称，用来唯一标识一条平行链，  可将mbaas修改成自己想要的名称（只支持英文字符），最后一个 . 号不能省略
Title="user.p.mbaas."
 #主链的grpc地址，改成：ParaRemoteGrpcClient="jiedian2.bityuan.com,cloud.bityuan.com"
 #注：上述连接最好只用于测试，如果商用的话，需要将指向自己部署的主链IP:8802，这样通信更流畅
ParaRemoteGrpcClient="localhost:8802"
 #指示从主链哪个高度开始同步，比如目前主链高度是19391000，建议配置是提前1000个区块（19391000-1000=19390000）
startHeight=1  ==> 改成 startHeight=19390000
```  

- 1.2.4 启动平行链
```  
nohup ./chain33 -f chain33.para.toml >> para.out&  
```  

- 1.2.5 检查平行链和主链的同步状态(进程启动后，等待一会后再执行)  
```
 #返回true代表同步完成
./chain33-cli --rpc_laddr="http://localhost:8901" para is_sync
 # 当前平行链最大区块高度
./chain33-cli --rpc_laddr="http://localhost:8901" block last_header
```  

备注：如果主链或平行链部署过程中遇到问题，可联系官方客服确认。  

## 2. 熟悉了解NFT合约开发部署  
参考： [[NFT合约开发部署]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/NFT合约开发部署.md)  

## 3. 通过JAVA-SDK进行数据上链     
[[JAVA-SDK环境部署]](https://github.com/andyYuanFZM/NFTDemo/tree/main/src/test/java/com/chain33/cn/JAVA-SDK开发环境.md)  

其它SDK或接口方式：  
[[GO-SDK]](https://github.com/33cn/chain33-sdk-go)   
[[JSON-RPC: TODO 待补充]]()   
[[Web3: TODO 待补充]]()   

## 4. 运行demo程序  
1. 调用 BlockChainTest.java中的createAccount方法，生成一对地址和私钥
2. 修改ERC1155Test和ERC721Test两个文件，将上一步生成的内容，分别填充到以下几个参数中，注意私钥即资产，要隐私存放，而地址是可以公开的
```  
// 管理员地址和私钥
String managerAddress = "";
String managerPrivateKey = "";
    
// 代扣地址和私钥
String withholdAddress = "";
String withholdPrivateKey = "";
```  
3. 给上述两个地址下充值少量(每个地址10个)的燃料（BTY）
4. 修改ERC1155Test和ERC721Test两个文件中以下两个参数
```  
// 改成自己平行链所在服务器IP地址
String ip = "";
// 改成自己平行链服务端口，对应的是配置文件里的jrpcBindAddr配置项，默认的是8901。 注意：如果远程访问，防火墙要放行此端口
int port = 8901;
```   
5. 修改平行链名称
```  
// 改成一.4中自己设置的平行链名称
String paraName = "user.p.mbaas.";
```   
6. 运行测试程序  

## 5. 应用对接注意事项   
交易上链失败有两大类情况：  
1. 交易上链了，但交易执行失败（有返回交易hash）：   这类交易通过了mempool（交易缓存池）的合法性检查，但是在合约执行过程中失败了（ 比如转移了错误数量的NFT）。
2. 交易没有上链（没有返回交易hash，rpc接口直接返回出错信息）： 这类交易在mempool的合法性检查中没有通过，包括以下以类错误：  
	- 签名错误（ErrSign）： 签名校验不通过，一般不会遇到，除非人为去改交易内容。  -- 不常见   
	- 交易重复（ErrDupTx）：mempool中发现重复交易，一般不会遇到，除非人为发送重复交易（所谓重复是hash完全一模一样的两笔交易，而不是指业务上数据相同）。 -- 不常见   
	- 手续费不足： 代扣地址下手续费不足会导致交易无法上链，需要保证代扣地址下GAS费充足。  -- 有可能会遇到  
	- 手续费太低（ErrTxFeeTooLow）： 交易设置的手续费比链上要求的手续费低，常见于部署EVM合约或批量发行大量NFT的场合， 需要通过queryEVMGas预估计出一个GAS费，然后在这个基础上再加上0.001作为手续费，这样能保证交易不会上链失败。 -- 有可能遇到，参考用例中手续费设置方式 
	- 交易账户在mempool中存在过多交易（ErrManyTx）： BTY为防止来自于同一个地址的频繁交易，限制每个账户在mempool中的最大交易数量不能超过100， 所以当交易频率很高时，mempool中代扣手续费的交易（都是来自同一个代扣地址）可能会超过100的， 而100笔以后的交易会被丢弃（rpc返回errmanytx的错）从而导致关联的交易也被丢弃。   -- 有可能会遇到   

举例说明应用层和区块链整合后的一般处理方案：  
为保证数据的一致性，需要判断交易在区块链上确实成功（拿到交易hash，且实际交易的执行结果是：ExecOk）, 业务上才能判定为成功。    且BTY支持以代扣的方式来扣除用户交易的燃料费， 代扣包含两笔交易：第一笔是代扣的交易（最简单的存证）， 第二笔是实际用户的交易。  交易上链后，区块链返回的是第一笔交易的hash值， 而这笔交易hash不能用于判断交易是否执行成功，需要拿这个hash值查到对应的第二笔交易才能判断。  具体见下面处理流程以及测试用例：  

场景：NFT平台上新品，大量用户抢购。  
处理流程：  
1. 用户完成支付，应用层触发NFT转账动作，并拿到区块链返回的交易hash(在采用代扣的情况下，这个hash对应的是代扣手续费交易，而非实际转NFT交易)  
2. 由于区块链是异步处理且有一定时延，这时NFT资产还没有真正转到用户地址下，但应用层可以预先将该笔订单标记为【待完成】。 这个状态不用给用户显示，只由应用层记录，对于用户而言，他只要一完成支付，就视为成功，不需要让他等待区块链的处理结果。  
3. 应用层把第1步中拿到的hash放到处理队列中，定时拿这个hash去查结果，如果查询结果返回空，hash继续留在队列中等待下一次查询， 如果结果不为空，从返回结果中拿到第二笔交易的hash,再根据这第二笔的交易hash去查询（这一步查询不用等待，拿到hash后可以马上查），从这次查询的返回结果拿到执行结果，如果是ExecOk代表执行成功，应用层再【待完成】的订单标记为【完成】，并从处理队列中删除第一笔交易的hash。 如果结果是ExecPack，代表执行失败，这个一般可能是业务上的bug(比如转了超过地址下数量的NFT资产，或是权限不够等等)，这种情况后续怎么处理由应用层根据实际情况来判断。    
4. 其它异常  
4.1 根据hash一直查不到有结果返回的可能性况： 1. 部署的平行链名称和上链交易中带的平行链名称不一致。  2.  平行链连接的主链节点高度落后于主网最大高度，或是平行连连接的那个主链节点离线。  
4.2 数据上链后，没有拿到交易hash，且rpc返回ErrTxFeeTooLow， 代表设置的交易手续费低于区块链所需的最低手续费。 需要在发交易前调用查询GAS费接口来估算出手续费（参考用例），再设置到交易中。  
4.3 数据上链后，没有拿到交易hash，且rpc返回ErrManyTx。  有大量交易上来时，应用层用队列缓存发送，比如每间隔10秒发送100笔交易，这样基本能解决ErrManyTx问题，如果再有个别交易还发生ErrManyTx，也需要把失败的再放回队列，后面重新发送。  
4.4 数据上链后，没有拿到交易hash，且rpc返回ErrTxMsgSizeTooBig， 代表交易体太大。 一般出现于ERC1155批量mint一批NFT时， 建议一次mint的token数量不要超过1000个， 数量比较大的，可以分多次来mint。  
4.5 定期检查代扣地址下的余额，保证手续费充足。  

