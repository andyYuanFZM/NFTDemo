mintByManager目录下的NFT合约只支持管理员来发行NFT，在合约的mint方法中，限制了只允许合约的部署人（管理员）才能允许调用。  适用于平台对于NFT发行有严格限制的业务场景。   
mintByUser此目录下的NFT合约不限制只有管理员才能发行，任何用户都可以调用mint方法发行NFT， 适用于平台任意作者都可以发行NFT的业务场景。   

测试平行链上发行NFT的步骤： 
1. BTY主链部署
1.1 准备一台4核8G的linux服器（ ubuntu或Centos都可），硬盘>300G
1.2 从 https://github.com/bityuan/bityuan/releases 下载最新版本的release运行  
注意：目前bty主链已经有100多G的数据， 同步时间会比较长，可以在同步过程中先使用官方的对外接口做测试验证， 具体见2.4中的说明  

2. 平行链部署  
2.1  准备一台2核4G的linux服务器（ ubuntu或Centos都可，且这是最低配置，只能用于测试验证）
2.2  下载，解压压缩包，并进入目录
```  
wget https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/parachain/linux/chain33_para_linux_0670237.tar.gz  
tar -zxvf chain33_para_linux_0670237.tar.gz  
cd chain33_para_linux_0670237  
```  
3. 目录下包含以下三个文件  
```  
chain33                -- chain33节点程序
chain33-cli            -- chain33节点命令行工具
chain33.para.toml      -- chain33平行链配置文件
```  
4. 修改配置文件（chain33.para.toml），修改以下几个配置项：  
```  
 #平行链名称，用来唯一标识一条平行链，  可将mbaas修改成自己想要的名称（只支持英文字符），最后一个 . 号不能省略
Title="user.p.mbaas."
 #主链的grpc地址，改成：ParaRemoteGrpcClient="jiedian2.bityuan.com,cloud.bityuan.com"
 #注：上述连接最好只用于测试，如果商用的话，需要将指向自己部署的主链IP:8802，这样通信更流畅
ParaRemoteGrpcClient="localhost:8802"
 #指示从主链哪个高度开始同步，比如目前主链高度是19391000，建议配置是提前1000个区块（19391000-1000=19390000）
startHeight=1  ==> 改成 startHeight=19390000
```  
5. 启动平行链
```  
nohup ./chain33 -f chain33.para.toml >> para.out&  
```  
6. 检查平行链和主链的同步状态(进程启动后，等待一会后再执行)  
```  
# 返回true代表同步完成
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

