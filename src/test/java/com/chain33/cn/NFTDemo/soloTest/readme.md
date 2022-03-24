mintByManater和mintByUser目录下都是在平行链上发行NFT的例子，  
如果只是想跑一个demo试一下，也可以本文档来本地部署一个solo节点来测试验证。  

solo节点发行NFT的步骤： 
一. solo节点部署  
1. 下载solo节点安装包：
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
chain33.solo.toml      -- chain33平行链配置文件
```  
3. 启动(window上可以装一个git bash来执行)
```  
nohup ./chain33 -f chain33.solo.toml >> para.out&  
```  

二. 运行demo程序  
运行 EVMTest.java


