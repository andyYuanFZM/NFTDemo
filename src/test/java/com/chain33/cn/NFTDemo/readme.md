mintByManager目录下的NFT合约只支持管理员来发行NFT，在合约的mint方法中，限制了只允许合约的部署人（管理员）才能允许调用。  适用于平台对于NFT发行有严格限制的业务场景。   
mintByUser此目录下的NFT合约不限制只有管理员才能发行，任何用户都可以调用mint方法发行NFT， 适用于平台任意作者都可以发行NFT的业务场景。   

一. 平行链环境部署：  
支持在同一台服务器上同时部署BTY主链节点和BTY平行链节点（只要保证两者的jsonrpc和grpc端口不冲突即可）  
1. BTY主链部署  
- 1.1 准备一台4核8G的linux服器（ ubuntu或Centos都可），硬盘>300G （同步速度SSD硬盘效率要远远高于机械盘，根据自己情况选择硬盘类型）  
- 1.2 从 https://github.com/bityuan/bityuan/releases 下载最新版本的release运行(比如以当前最新版本6.7.2来说明)        
```  
# 下载
wget https://github.com/bityuan/bityuan/releases/download/v6.7.2/bityuan-linux-amd64.tar.gz
# 新建目录
mkdir bityuan
# 解压
tar -zxvf bityuan-linux-amd64.tar.gz -C bityuan
```  
- 1.3 目录下包含以下几个文件  
```  
bityuan-linux-amd64                -- BTY节点程序
bityuan-cli-linux-amd64            -- BTY节点命令行工具
bityuan.toml                       -- bityuan配置文件（带数据分片功能，占空间小），后面启动时用这个配置
bityuan-fullnode.toml              -- bityuan配置文件（全节点模式，占空间大）
```  
- 1.4 启动主链程序
```  
nohup ./bityuan-linux-amd64 -f bityuan.toml >> bty.out&
```  

- 1.5 检查主链的同步状态(进程启动后，等待一会后执行) 
```  
#  主要看返回信息中自己节点的height信息， 和主链最大高度一致代表同步成功。  这一过程时间比较长，按目前2000万左右的区块高度， SSD硬盘同步需要三天左右时间， 普通机械硬盘耗时可能翻倍
 ./bityuan-cli-linux-amd64 net peer info  
```   

- 1.6 创建钱包，接收空投 (这一步可以在主链节点还没有同步完情况下执行)   
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

- 1.7 转移空投地址下的BTY到另外的地址(只有在主链节点同步完后，才能看到空投资产，所以这一步要在同步完后再执行,同步过程中执行会报一个：ErrNotSync的错)   
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
 
2. 平行链部署 （在主链部署完成后进行）  
因为目前bty主链已经有100多G的数据， 同步时间会比较长，所以为了方便开发者验证，可以在主链同步过程中临时使用官方的对外接口做测试验证， 具体见2.3中的说明 。  
- 2.1  下载，解压压缩包，并进入目录  
```  
wget https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/parachain/linux/chain33_para_linux_0670237.tar.gz  
tar -zxvf chain33_para_linux_0670237.tar.gz  
cd chain33_para_linux_0670237  
```  

- 2.2 目录下包含以下三个文件  
```  
chain33                -- chain33节点程序
chain33-cli            -- chain33节点命令行工具
chain33.para.toml      -- chain33平行链配置文件
```  

- 2.3 修改配置文件（chain33.para.toml），修改以下几个配置项：  
```  
 #平行链名称，用来唯一标识一条平行链，  可将mbaas修改成自己想要的名称（只支持英文字符），最后一个 . 号不能省略
Title="user.p.mbaas."
 #主链的grpc地址，改成：ParaRemoteGrpcClient="jiedian2.bityuan.com,cloud.bityuan.com"
 #注：上述连接最好只用于测试，如果商用的话，需要将指向自己部署的主链IP:8802，这样通信更流畅
ParaRemoteGrpcClient="localhost:8802"
 #指示从主链哪个高度开始同步，比如目前主链高度是19391000，建议配置是提前1000个区块（19391000-1000=19390000）
startHeight=1  ==> 改成 startHeight=19390000
```  

- 2.4 启动平行链
```  
nohup ./chain33 -f chain33.para.toml >> para.out&  
```  

- 2.5 检查平行链和主链的同步状态(进程启动后，等待一会后再执行)  
```
 #返回true代表同步完成
./chain33-cli --rpc_laddr="http://localhost:8901" para is_sync
 # 当前平行链最大区块高度
./chain33-cli --rpc_laddr="http://localhost:8901" block last_header
```  

备注：如果主链或平行链部署过程中遇到问题，可联系官方客服确认。  

二. 运行demo程序
1. 调用 BlockChainTest.java中的createAccount方法，生成两对地址和私钥
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

