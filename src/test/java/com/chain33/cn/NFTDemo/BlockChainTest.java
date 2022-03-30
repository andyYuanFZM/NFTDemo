package com.chain33.cn.NFTDemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.rpcresult.AccountAccResult;
import cn.chain33.javasdk.model.rpcresult.BlockResult;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * 一些区块链接口的查询
 *
 */
public class BlockChainTest {
	
	// 主链所在服务器IP地址,后续项目方部署自己主链后，替换掉此URL
	RpcClient mclient = new RpcClient("https://jiedian2.bityuan.com:8801");
	
	// 平行链所在服务器IP地址
	String paraIp = "121.43.111.94";
	// 平行链服务端口
	int paraPort = 8801;
	RpcClient pclient = new RpcClient(paraIp, paraPort);
	
	/**
	 * 获取代扣地址下的燃料余额(燃料在主链上，只能取主链的rpc链接)
	 * @throws IOException 
	 */
	@Test
	public void getBalance() throws IOException {
		List<String> list = new ArrayList<String>();
		list.add("1AKAm9vV6m4TbTzHKwirdygakF5HNus8Bg");
		list.add("1L26eqrBgZanXqosSLrzM9ad77B6KwYZov");

		List<AccountAccResult> queryBtyBalance;
		queryBtyBalance = mclient.getCoinsBalance(list, "coins");
		if (queryBtyBalance != null) {
			for (AccountAccResult accountAccResult : queryBtyBalance) {
				System.out.println(accountAccResult.getBalance()/1e8);
			}
		}
	}
	
	
	/**
	 * 获取当前平行链的最大高度(取平行链的rpc链接)
	 * @throws IOException 
	 */
	@Test
	public void getLastHeight() throws IOException {
    	BlockResult blockResult = pclient.getLastHeader();
    	System.out.println("当前最大区块高度为： " + blockResult.getHeight());
	}
	
	/**
	 * 生成用户私钥和地址
	 */
	@Test
	public void createAccount() {
    	Account account = new Account();
		AccountInfo accountInfo = account.newAccountLocal();
		// 生成用户私钥
		System.out.println(accountInfo.getPrivateKey());
		// 生成用户地址
		System.out.println(accountInfo.getAddress());
    }
	
	/**
	 * @description 验证地址的合法性
	 */
	@Test
	public void validateAddress() {
		String address = "1G1L2M1w1c1gpV6SP8tk8gBPGsJe2RfTks";
		boolean validAddressResult = TransactionUtil.validAddress(address);
		System.out.printf("validate result is:%s", validAddressResult);
	}
	
	
	
	

}
