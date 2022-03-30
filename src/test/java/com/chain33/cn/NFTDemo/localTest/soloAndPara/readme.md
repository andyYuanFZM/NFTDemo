本地部署一个solo主链节点，再部署一个平行链节点     部署和测试步骤：  
 
一. solo主节点和平行链部署    
1. 下载对应系统的solo节点安装包： 
```  
 #windows:  
https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/mainet/solo/windows/chain33_solo_windos_0670237.zip
 #linux：  
https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/mainet/solo/linux/chain33_solo_linux_0670237.tar.gz
```  

2. 目录下包含以下三个文件  
```  
chain33                -- chain33节点程序
chain33-cli            -- chain33节点命令行工具
chain33.solo.toml      -- chain33配置文件
```  

3. 启动(window上可以装一个git bash来执行)
```  
nohup ./chain33 -f chain33.solo.toml >> para.out&  
```  

4. 下载对应系统的平行链节点安装包：  
```  
 #windows:  
https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/parachain/windows/chain33_para_windos_0670237.zip
 #linux：  
https://bty33.oss-cn-shanghai.aliyuncs.com/chain33Dev/parachain/linux/chain33_para_linux_0670237.tar.gz
```  

5. 目录下包含以下三个文件  
```  
chain33                -- chain33节点程序
chain33-cli            -- chain33节点命令行工具
chain33.para.toml      -- chain33配置文件
```  

6. 修改配置文件（chain33.para.toml），修改以下两项配置：   
```  
 #平行链名称，用来唯一标识一条平行链，  可将mbaas修改成自己想要的名称（只支持英文字符），最后一个 . 号不能省略
Title="user.p.mbaas."
 #平行链和主链的grpc连接，指向自己部署solo节点的ip地址，如果两个部署在同一台机器上，则不用改动
ParaRemoteGrpcClient="localhost:8802"
```  

7. 启动(window上可以装一个git bash来执行)
```  
nohup ./chain33 -f chain33.para.toml >> para.out&  
```  

8. 检查平行链的高度变化(进程启动后，等待一会后再执行)  
```  
# 当前平行链最大区块高度
./chain33-cli --rpc_laddr="http://localhost:8901" block last_header
```  

二. 运行demo程序  
1. 根据实际业务需求运行相应demo程序  
	1.1 minByManager子目录： 合约中限制了只能是合约部署人（管理员）才可以发行NFT，适用于NFT只允许平台方发行的情况。  
	1.2 mintByUser子目录： 合约不限制NFT的发行人，个人用户也可以上平台发行nft  

2. 修改ERC1155Test和ERC721Test两个文件中以下两个参数  
```  
// 改成自己平行链所在服务器IP地址  
String ip = "";  
// 改成自己平行链服务端口，对应的是配置文件里的jrpcBindAddr配置项，默认的是8901。 注意：如果远程访问，防火墙要放行此端口  
int port = 8901;  
```   

3. 修改平行链名称  
```  
// 改成一.6中自己设置的平行链名称  
String paraName = "user.p.mbaas.";  
```   
