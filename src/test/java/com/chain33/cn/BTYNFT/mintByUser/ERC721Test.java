package com.chain33.cn.BTYNFT.mintByUser;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.chain33.cn.CommonUtil;

import cn.chain33.javasdk.client.Account;
import cn.chain33.javasdk.client.RpcClient;
import cn.chain33.javasdk.model.AccountInfo;
import cn.chain33.javasdk.model.decode.DecodeRawTransaction;
import cn.chain33.javasdk.model.rpcresult.QueryTransactionResult;
import cn.chain33.javasdk.utils.ByteUtil;
import cn.chain33.javasdk.utils.EvmUtil;
import cn.chain33.javasdk.utils.HexUtil;
import cn.chain33.javasdk.utils.TransactionUtil;

/**
 * NFT ERC721 ���к�ת��
 * @author fkeit
 *
 */
public class ERC721Test {

	// ƽ�������ڷ�����IP��ַ
		String ip = "172.22.16.179";
		// ƽ��������˿�
		int port = 8901;
		RpcClient client = new RpcClient(ip, port);
		
	    // ƽ�������ƣ��̶���ʽuser.p.xxxx.������ʹ�õ����ƽ�mbaas�� �����Լ�ƽ�������Ʊ仯��  �������һ��Ҫ��ƽ���������ļ��е�������ȫһ�¡�
		String paraName = "user.p.mbaas.";

		// ��Լ�����ˣ�����Ա����ַ��˽Կ,��ַ����Ҫ��BTY������������
		// ���ɷ�ʽ�ο�����testCreateAccount������˽Կ�͵�ַһһ��Ӧ
		String managerAddress = "14nh6p7CUNtLXAHEiVkSd5mLUWynzafHBx";
		String managerPrivateKey = "7dfe80684f7007b2829a28c85be681304f7f4cf6081303dbace925826e2891d1";
//		String managerAddress = "�滻���Լ��ĵ�ַ��������createAccount��������";
//		String managerPrivateKey = "�滻���Լ���˽Կ��������createAccount��������,ע��˽Կǧ����й©";
	    
	    // �û������Ѵ��۵�ַ��˽Կ,��ַ����Ҫ��BTY������������
		// ���ɷ�ʽ�ο�����testCreateAccount������˽Կ�͵�ַһһ��Ӧ
		String withholdAddress = "17RH6oiMbUjat3AAyQeifNiACPFefvz3Au";
	    String withholdPrivateKey = "56d1272fcf806c3c5105f3536e39c8b33f88cb8971011dfe5886159201884763";
//		String withholdAddress = "�滻���Լ��ĵ�ַ��������createAccount��������";
//	    String withholdPrivateKey = "�滻���Լ���˽Կ��������createAccount��������,ע��˽Կǧ����й©";
	    
	    // �û�A��ַ��˽Կ
		String useraAddress;
	    String useraPrivateKey;
	    
	    // �û�B��ַ��˽Կ
		String userbAddress;
	    String userbPrivateKey;
	    
	    /**
	     * ERC721��Լ���𣬵��ò���
	     * @throws Exception 
	     */
	    @Test
	    public void testERC721() throws Exception {
	    		    	
	    	// =======> Ϊ�û�A��B����˽Կ�͵�ַ
	    	AccountInfo infoA = createAccount();
	    	useraAddress = infoA.getAddress();
	    	useraPrivateKey = infoA.getPrivateKey();
	    	
	    	AccountInfo infoB = createAccount();
	    	userbAddress = infoB.getAddress();
	    	userbPrivateKey = infoB.getPrivateKey();
	    	
	    	// =======>  ͨ������Ա�����Լ
	        // �����Լ, ������ ƽ������Լ���� ǩ����ַ��ǩ��˽Կ
	        String hash = deployContract(paraName, managerAddress, managerPrivateKey);
	        
	        // ������һ���������ϵĺ�Լ��ַ
	        String contractAddress = TransactionUtil.convertExectoAddr(managerAddress + hash.substring(2));
	        System.out.println("����õĺ�Լ��ַ = " + contractAddress);
	        
	        // =======>  ���ú�Լ����NFT,ERC721ֻ֧��һ��һtoken
	        // �����Լ����, mint��Ӧsolidity��Լ��ķ������� �������������ֱ����:NFT�����û�A��, NFT���, URI��Ϣ��������Դ洢NFTͼƬ�洢����ַ��ipfs��ַ��
	        // ���۽�����Ҫ��ƽ������Լ��ַ��һ������
	        String execer = paraName + "evm";
	        // ƽ������Լ��ַ����(ƽ����titleǰ׺+��Լ����)
	        String paracontractAddress = client.convertExectoAddr(execer);
	        
	        int tokenId = 10000;
	        byte[] initNFT = EvmUtil.encodeParameter(CommonUtil.abi_User_721, "mint", useraAddress, tokenId, "{\"ͼƬ����\":\"��xxx����\";\"����ʱ��\":\"2022/12/25\";\"ͼƬ���·��\":\"http://www.baidu.com\"}");
	    	String txEncode = EvmUtil.callEvmContractWithhold(initNFT,"", 0, execer, useraPrivateKey, contractAddress);
	    	// �ٵ��ô��۽��׷������ô���˽Կ�Խ�������ǩ��
	    	createNobalance(txEncode, paracontractAddress, useraPrivateKey, withholdPrivateKey);
	        
	        // =======>  ��ѯ�û�A��ַ�µ����
	        byte[] packAbiGet = EvmUtil.encodeParameter(CommonUtil.abi_User_721, "balanceOf", useraAddress);
	        queryContract(packAbiGet, contractAddress, "ת��ǰ�û�A,NFTID=" + tokenId + "���");
	        	        
	        // =======>  ��A��ַ��B��ַת��,ʹ�ô��۽���
	        // �û�A����1��NFTת���û�B
	    	byte[] transfer = EvmUtil.encodeParameter(CommonUtil.abi_User_721, "safeTransferFrom", useraAddress, userbAddress, tokenId);
	    	// ����ת�˽����壬�����û�A�Դ˱ʽ���ǩ����
	    	txEncode = EvmUtil.callEvmContractWithhold(transfer,"", 0, execer, useraPrivateKey, contractAddress);
	    	// �ٵ��ô��۽��׷������ô���˽Կ�Խ�������ǩ��
	    	createNobalance(txEncode, paracontractAddress, useraPrivateKey, withholdPrivateKey);

	        
	        // =======>  ��ѯ�û�A���û�B��ַ�µ����
	        packAbiGet = EvmUtil.encodeParameter(CommonUtil.abi_User_721, "balanceOf", useraAddress);
	        queryContract(packAbiGet, contractAddress, "ת�˺��û�A,NFTID=" + tokenId + "���");
	        
	        packAbiGet = EvmUtil.encodeParameter(CommonUtil.abi_User_721, "balanceOf", userbAddress);
	        queryContract(packAbiGet, contractAddress, "ת�˺��û�B,NFTID=" + tokenId + "���");
	        
	        // =======>   ��ѯtoken URI
	        packAbiGet = EvmUtil.encodeParameter(CommonUtil.abi_User_721, "tokenURI", tokenId);
	        queryContractString(packAbiGet, contractAddress, "NFTID=" + tokenId + "��URI��Ϣ");
	    }
	    
	    /**
	     * Step1: ����˽Կ����ַ
	     * һ�����û�ע��ʱ���ã����ɺ������ݿ��к��û���Ϣ�󶨣�����ֱ�Ӵӿ��в����ʹ��
	     */
	    private AccountInfo createAccount() {
	    	Account account = new Account();
			AccountInfo accountInfo = account.newAccountLocal();
			return accountInfo;
	    }
	    
	    /**
	     * Step2:�����Լ
	     * @throws Exception
	     */
	    private String deployContract(String execer, String address, String privateKey) throws Exception {

	        // �����Լ
	        String txEncode;
	        String txhash = "";
	        QueryTransactionResult txResult = new QueryTransactionResult();
	        
	        // �˺�Լ�Ĺ�������в������˴�Ϊ���췽ʽ
	        byte[] evmWithCode = EvmUtil.encodeContructor(CommonUtil.abi_User_721, "NFT name", "ART");  
            byte[] code = ByteUtil.merge(HexUtil.fromHexString(CommonUtil.byteCode_User_721), evmWithCode);
	        	        
	    	// �����ԼGAS��
	        String evmCode = EvmUtil.getCreateEvmEncode(code, "", "deploy ERC721 contract", execer);
	        long gas = client.queryEVMGas("evm", evmCode, address);
	        System.out.println("Gas fee is:" + gas);
	        
	        // ͨ����Լcode, ����Ա˽Կ��ƽ��������+evm,�����ѵȲ������첿���Լ���ף���ǩ��
	        txEncode = EvmUtil.createEvmContract(code, "", "deploy ERC721 contract", privateKey, execer, gas);
	        // �����첢ǩ�����Ľ���ͨ��rpc�ӿڷ��͵�ƽ������
	        txhash = client.submitTransaction(txEncode);
	        System.out.println("�����Լ����hash = " + txhash);
	        
	        // BTYƽ��3-5��һ������ȷ�ϣ� ��Ҫ��ʱȥ����
	        Thread.sleep(5000);
			for (int tick = 0; tick < 20; tick++){
				txResult = client.queryTransaction(txhash);
				if(txResult == null) {
					Thread.sleep(3000);
					continue;
				}			
				break;
			}
			
			if ("ExecOk".equals(txResult.getReceipt().getTyname())) {
				System.out.println("��Լ����ɹ�");

			} else {
				System.out.println("��Լ����ʧ�ܣ�һ��ʧ��ԭ���������Ϊ��ַ�������Ѳ���");
			}
			
			return txhash;
	    }
	    
	    
	    /**
	     * ��ѯ����
	     * @param queryAbi
	     * @param contractAddress
	     * @throws Exception 
	     */
	    private void queryContract(byte[] queryAbi, String contractAddress, String title) throws Exception {
	        // ��ѯ�û�A���û�B��ַ�µ��ʲ����
	        JSONObject query = client.callEVMAbi(contractAddress, HexUtil.toHexString(queryAbi));
	        JSONObject output = query.getJSONObject("result");
	        String rawData = output.getString("rawData");
	        System.out.println(title + ": " + HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)));
	    }
	    
	    /**
	     * ��ѯ����
	     * @param queryAbi
	     * @param contractAddress
	     * @throws Exception 
	     */
	    private void queryContractString(byte[] queryAbi, String contractAddress, String title) throws Exception {
	        // ��ѯ�û�A���û�B��ַ�µ��ʲ����
	        JSONObject query = client.callEVMAbi(contractAddress, HexUtil.toHexString(queryAbi));
	        JSONObject output = query.getJSONObject("result");
	        String rawData = output.getString("rawData");
	        System.out.println(title + ": " + HexUtil.hexStringToString(HexUtil.removeHexHeader(rawData)).replaceAll("\u0000",""));
	    }
	    
	    
	    /**
	     * �������������ѽ���
	     * 
	     * @param txEncode
	     * @param contranctAddress
	     * @return
	     * @throws InterruptedException
	     * @throws IOException 
	     */
	    private String createNobalance(String txEncode, String contranctAddress, String userPrivatekey, String withHoldPrivateKey) throws Exception {
	        String createNoBalanceTx = client.createNoBalanceTx(txEncode, "");
		    // ��������
		    List<DecodeRawTransaction> decodeRawTransactions = client.decodeRawTransaction(createNoBalanceTx);
		    
		    String hexString = TransactionUtil.signDecodeTx(decodeRawTransactions, contranctAddress, userPrivatekey, withHoldPrivateKey);
		    String submitTransaction = client.submitTransaction(hexString);
		    System.out.println("����hash= " + submitTransaction);
		    
		    String nextString = null;
	        QueryTransactionResult txResult = new QueryTransactionResult();

			Thread.sleep(5000);
			for (int tick = 0; tick < 20; tick++){
				QueryTransactionResult result = client.queryTransaction(submitTransaction);
				if(result == null) {
					Thread.sleep(3000);
					continue;
				}

				System.out.println("next:" + result.getTx().getNext());
				QueryTransactionResult nextResult = client.queryTransaction(result.getTx().getNext());
				System.out.println("ty:" + nextResult.getReceipt().getTyname());
				nextString = result.getTx().getNext();
				break;
			}
			
			
			txResult = client.queryTransaction(nextString);
			if ("ExecOk".equals(txResult.getReceipt().getTyname())) {
				System.out.println("��Լ���óɹ�");
				
			} else {
				System.out.println("��Լ����ʧ�ܣ�һ��ʧ��ԭ���������Ϊ��ַ�������Ѳ���");
			}
			return nextString;
	    }
}
