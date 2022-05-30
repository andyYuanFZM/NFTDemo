package com.chain33.cn.BTYNFT.btyPara.mintByUser;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

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
		String ip = "localhost";
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
	    
	    // solidity��ԼԴ�����./solidity/ERC721.sol
	    // ��Լ���������bytecode
	    String codes = "60806040523480156200001157600080fd5b5060405162002d9b38038062002d9b8339818101604052810190620000379190620002c7565b60016000806301ffc9a760e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060006101000a81548160ff02191690831515021790555060016000806380ac58cd60e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060006101000a81548160ff0219169083151502179055506001600080635b5e139f60e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060006101000a81548160ff021916908315150217905550816005908051906020019062000142929190620001a5565b5080600690805190602001906200015b929190620001a5565b5033600860006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050506200046b565b828054620001b390620003d7565b90600052602060002090601f016020900481019282620001d7576000855562000223565b82601f10620001f257805160ff191683800117855562000223565b8280016001018555821562000223579182015b828111156200022257825182559160200191906001019062000205565b5b50905062000232919062000236565b5090565b5b808211156200025157600081600090555060010162000237565b5090565b60006200026c62000266846200036e565b6200033a565b9050828152602081018484840111156200028557600080fd5b62000292848285620003a1565b509392505050565b600082601f830112620002ac57600080fd5b8151620002be84826020860162000255565b91505092915050565b60008060408385031215620002db57600080fd5b600083015167ffffffffffffffff811115620002f657600080fd5b62000304858286016200029a565b925050602083015167ffffffffffffffff8111156200032257600080fd5b62000330858286016200029a565b9150509250929050565b6000604051905081810181811067ffffffffffffffff821117156200036457620003636200043c565b5b8060405250919050565b600067ffffffffffffffff8211156200038c576200038b6200043c565b5b601f19601f8301169050602081019050919050565b60005b83811015620003c1578082015181840152602081019050620003a4565b83811115620003d1576000848401525b50505050565b60006002820490506001821680620003f057607f821691505b602082108114156200040757620004066200040d565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b612920806200047b6000396000f3fe608060405234801561001057600080fd5b50600436106100f55760003560e01c806370a0823111610097578063b88d4fde11610066578063b88d4fde14610284578063c87b56dd146102a0578063d3fc9864146102d0578063e985e9c5146102ec576100f5565b806370a08231146101fc57806395d89b411461022c578063a22cb4651461024a578063b2bdfa7b14610266576100f5565b8063095ea7b3116100d3578063095ea7b31461017857806323b872dd1461019457806342842e0e146101b05780636352211e146101cc576100f5565b806301ffc9a7146100fa57806306fdde031461012a578063081812fc14610148575b600080fd5b610114600480360381019061010f91906124ab565b61031c565b604051610121919061262c565b60405180910390f35b610132610383565b60405161013f9190612647565b60405180910390f35b610162600480360381019061015d91906124fd565b610415565b60405161016f91906125c5565b60405180910390f35b610192600480360381019061018d9190612403565b610530565b005b6101ae60048036038101906101a991906122f8565b610913565b005b6101ca60048036038101906101c591906122f8565b610d65565b005b6101e660048036038101906101e191906124fd565b610d85565b6040516101f391906125c5565b60405180910390f35b61021660048036038101906102119190612293565b610e6b565b6040516102239190612669565b60405180910390f35b610234610f25565b6040516102419190612647565b60405180910390f35b610264600480360381019061025f91906123c7565b610fb7565b005b61026e6110b4565b60405161027b91906125c5565b60405180910390f35b61029e60048036038101906102999190612347565b6110da565b005b6102ba60048036038101906102b591906124fd565b611131565b6040516102c79190612647565b60405180910390f35b6102ea60048036038101906102e5919061243f565b611221565b005b610306600480360381019061030191906122bc565b61127f565b604051610313919061262c565b60405180910390f35b6000806000837bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff169050919050565b606060058054610392906127ed565b80601f01602080910402602001604051908101604052809291908181526020018280546103be906127ed565b801561040b5780601f106103e05761010080835404028352916020019161040b565b820191906000526020600020905b8154815290600101906020018083116103ee57829003601f168201915b5050505050905090565b600081600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f3030333030320000000000000000000000000000000000000000000000000000815250906104f3576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104ea9190612647565b60405180910390fd5b506002600084815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16915050919050565b8060006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690503373ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614806106295750600460008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b6040518060400160405280600681526020017f3030333030330000000000000000000000000000000000000000000000000000815250906106a0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016106979190612647565b60405180910390fd5b5082600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f30303330303200000000000000000000000000000000000000000000000000008152509061077d576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016107749190612647565b60405180910390fd5b5060006001600086815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508073ffffffffffffffffffffffffffffffffffffffff168673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f30303330303800000000000000000000000000000000000000000000000000008152509061085d576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108549190612647565b60405180910390fd5b50856002600087815260200190815260200160002060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550848673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92560405160405180910390a4505050505050565b8060006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690503373ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614806109e457503373ffffffffffffffffffffffffffffffffffffffff166002600084815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b80610a755750600460008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b6040518060400160405280600681526020017f303033303034000000000000000000000000000000000000000000000000000081525090610aec576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ae39190612647565b60405180910390fd5b5082600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303032000000000000000000000000000000000000000000000000000081525090610bc9576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610bc09190612647565b60405180910390fd5b5060006001600086815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508673ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f303033303037000000000000000000000000000000000000000000000000000081525090610ca8576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610c9f9190612647565b60405180910390fd5b50600073ffffffffffffffffffffffffffffffffffffffff168673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303031000000000000000000000000000000000000000000000000000081525090610d51576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610d489190612647565b60405180910390fd5b50610d5c8686611313565b50505050505050565b610d80838383604051806020016040528060008152506113c8565b505050565b60006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303032000000000000000000000000000000000000000000000000000081525090610e65576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610e5c9190612647565b60405180910390fd5b50919050565b60008073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303031000000000000000000000000000000000000000000000000000081525090610f14576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610f0b9190612647565b60405180910390fd5b50610f1e82611996565b9050919050565b606060068054610f34906127ed565b80601f0160208091040260200160405190810160405280929190818152602001828054610f60906127ed565b8015610fad5780601f10610f8257610100808354040283529160200191610fad565b820191906000526020600020905b815481529060010190602001808311610f9057829003601f168201915b5050505050905090565b80600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f17307eab39ab6107e8899845ad3d59bd9653f200f220920489ca2b5937696c31836040516110a8919061262c565b60405180910390a35050565b600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b61112a85858585858080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f820116905080830192505050505050506113c8565b5050505050565b606081600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f30303330303200000000000000000000000000000000000000000000000000008152509061120f576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016112069190612647565b60405180910390fd5b50611219836119df565b915050919050565b61122b8484611a84565b6112798383838080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050611c72565b50505050565b6000600460008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905092915050565b60006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905061135482611d7c565b61135e8183611db5565b6113688383611f20565b818373ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef60405160405180910390a4505050565b8160006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690503373ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16148061149957503373ffffffffffffffffffffffffffffffffffffffff166002600084815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b8061152a5750600460008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b6040518060400160405280600681526020017f3030333030340000000000000000000000000000000000000000000000000000815250906115a1576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016115989190612647565b60405180910390fd5b5083600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f30303330303200000000000000000000000000000000000000000000000000008152509061167e576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016116759190612647565b60405180910390fd5b5060006001600087815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508773ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f30303330303700000000000000000000000000000000000000000000000000008152509061175d576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016117549190612647565b60405180910390fd5b50600073ffffffffffffffffffffffffffffffffffffffff168773ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303031000000000000000000000000000000000000000000000000000081525090611806576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016117fd9190612647565b60405180910390fd5b506118118787611313565b6118308773ffffffffffffffffffffffffffffffffffffffff166120a8565b1561198c5760008773ffffffffffffffffffffffffffffffffffffffff1663150b7a02338b8a8a6040518563ffffffff1660e01b815260040161187694939291906125e0565b602060405180830381600087803b15801561189057600080fd5b505af11580156118a4573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906118c891906124d4565b905063150b7a0260e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916817bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916146040518060400160405280600681526020017f303033303035000000000000000000000000000000000000000000000000000081525090611989576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016119809190612647565b60405180910390fd5b50505b5050505050505050565b6000600360008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b60606007600083815260200190815260200160002080546119ff906127ed565b80601f0160208091040260200160405190810160405280929190818152602001828054611a2b906127ed565b8015611a785780601f10611a4d57610100808354040283529160200191611a78565b820191906000526020600020905b815481529060010190602001808311611a5b57829003601f168201915b50505050509050919050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303031000000000000000000000000000000000000000000000000000081525090611b2c576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611b239190612647565b60405180910390fd5b50600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f303033303036000000000000000000000000000000000000000000000000000081525090611c07576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611bfe9190612647565b60405180910390fd5b50611c128282611f20565b808273ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef60405160405180910390a45050565b81600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303032000000000000000000000000000000000000000000000000000081525090611d4e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611d459190612647565b60405180910390fd5b5081600760008581526020019081526020016000209080519060200190611d769291906120f3565b50505050565b6002600082815260200190815260200160002060006101000a81549073ffffffffffffffffffffffffffffffffffffffff021916905550565b8173ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f303033303037000000000000000000000000000000000000000000000000000081525090611e8e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611e859190612647565b60405180910390fd5b506001600360008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254611edf9190612712565b925050819055506001600082815260200190815260200160002060006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690555050565b600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f303033303036000000000000000000000000000000000000000000000000000081525090611ffa576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611ff19190612647565b60405180910390fd5b50816001600083815260200190815260200160002060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600360008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825461209d91906126bc565b925050819055505050565b60008060007fc5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a47060001b9050833f91506000801b82141580156120ea5750808214155b92505050919050565b8280546120ff906127ed565b90600052602060002090601f0160209004810192826121215760008555612168565b82601f1061213a57805160ff1916838001178555612168565b82800160010185558215612168579182015b8281111561216757825182559160200191906001019061214c565b5b5090506121759190612179565b5090565b5b8082111561219257600081600090555060010161217a565b5090565b6000813590506121a58161288e565b92915050565b6000813590506121ba816128a5565b92915050565b6000813590506121cf816128bc565b92915050565b6000815190506121e4816128bc565b92915050565b60008083601f8401126121fc57600080fd5b8235905067ffffffffffffffff81111561221557600080fd5b60208301915083600182028301111561222d57600080fd5b9250929050565b60008083601f84011261224657600080fd5b8235905067ffffffffffffffff81111561225f57600080fd5b60208301915083600182028301111561227757600080fd5b9250929050565b60008135905061228d816128d3565b92915050565b6000602082840312156122a557600080fd5b60006122b384828501612196565b91505092915050565b600080604083850312156122cf57600080fd5b60006122dd85828601612196565b92505060206122ee85828601612196565b9150509250929050565b60008060006060848603121561230d57600080fd5b600061231b86828701612196565b935050602061232c86828701612196565b925050604061233d8682870161227e565b9150509250925092565b60008060008060006080868803121561235f57600080fd5b600061236d88828901612196565b955050602061237e88828901612196565b945050604061238f8882890161227e565b935050606086013567ffffffffffffffff8111156123ac57600080fd5b6123b8888289016121ea565b92509250509295509295909350565b600080604083850312156123da57600080fd5b60006123e885828601612196565b92505060206123f9858286016121ab565b9150509250929050565b6000806040838503121561241657600080fd5b600061242485828601612196565b92505060206124358582860161227e565b9150509250929050565b6000806000806060858703121561245557600080fd5b600061246387828801612196565b94505060206124748782880161227e565b935050604085013567ffffffffffffffff81111561249157600080fd5b61249d87828801612234565b925092505092959194509250565b6000602082840312156124bd57600080fd5b60006124cb848285016121c0565b91505092915050565b6000602082840312156124e657600080fd5b60006124f4848285016121d5565b91505092915050565b60006020828403121561250f57600080fd5b600061251d8482850161227e565b91505092915050565b61252f81612746565b82525050565b61253e81612758565b82525050565b600061254f82612684565b612559818561269a565b93506125698185602086016127ba565b6125728161287d565b840191505092915050565b60006125888261268f565b61259281856126ab565b93506125a28185602086016127ba565b6125ab8161287d565b840191505092915050565b6125bf816127b0565b82525050565b60006020820190506125da6000830184612526565b92915050565b60006080820190506125f56000830187612526565b6126026020830186612526565b61260f60408301856125b6565b81810360608301526126218184612544565b905095945050505050565b60006020820190506126416000830184612535565b92915050565b60006020820190508181036000830152612661818461257d565b905092915050565b600060208201905061267e60008301846125b6565b92915050565b600081519050919050565b600081519050919050565b600082825260208201905092915050565b600082825260208201905092915050565b60006126c7826127b0565b91506126d2836127b0565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff038211156127075761270661281f565b5b828201905092915050565b600061271d826127b0565b9150612728836127b0565b92508282101561273b5761273a61281f565b5b828203905092915050565b600061275182612790565b9050919050565b60008115159050919050565b60007fffffffff0000000000000000000000000000000000000000000000000000000082169050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b60005b838110156127d85780820151818401526020810190506127bd565b838111156127e7576000848401525b50505050565b6000600282049050600182168061280557607f821691505b602082108114156128195761281861284e565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000601f19601f8301169050919050565b61289781612746565b81146128a257600080fd5b50565b6128ae81612758565b81146128b957600080fd5b50565b6128c581612764565b81146128d057600080fd5b50565b6128dc816127b0565b81146128e757600080fd5b5056fea2646970667358221220cf8b4dba029c6a132f00b0383c7ea54948201b989ebba902cf506ccb00d3ef6764736f6c63430008000033";
	    // ��Լ��Ӧ��abi
	    String abi = "[{\"inputs\": [{\"internalType\": \"string\",\"name\": \"_name\",\"type\": \"string\"},{\"internalType\": \"string\",\"name\": \"_symbol\",\"type\": \"string\"}],\"stateMutability\": \"nonpayable\",\"type\": \"constructor\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_approved\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"Approval\",\"type\": \"event\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_operator\",\"type\": \"address\"},{\"indexed\": false,\"internalType\": \"bool\",\"name\": \"_approved\",\"type\": \"bool\"}],\"name\": \"ApprovalForAll\",\"type\": \"event\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_from\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"Transfer\",\"type\": \"event\"},{\"inputs\": [],\"name\": \"_owner\",\"outputs\": [{\"internalType\": \"address\",\"name\": \"\",\"type\": \"address\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_approved\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"approve\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"}],\"name\": \"balanceOf\",\"outputs\": [{\"internalType\": \"uint256\",\"name\": \"\",\"type\": \"uint256\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"getApproved\",\"outputs\": [{\"internalType\": \"address\",\"name\": \"\",\"type\": \"address\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"_operator\",\"type\": \"address\"}],\"name\": \"isApprovedForAll\",\"outputs\": [{\"internalType\": \"bool\",\"name\": \"\",\"type\": \"bool\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"},{\"internalType\": \"string\",\"name\": \"_uri\",\"type\": \"string\"}],\"name\": \"mint\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [],\"name\": \"name\",\"outputs\": [{\"internalType\": \"string\",\"name\": \"_name\",\"type\": \"string\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"ownerOf\",\"outputs\": [{\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_from\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"safeTransferFrom\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_from\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"},{\"internalType\": \"bytes\",\"name\": \"_data\",\"type\": \"bytes\"}],\"name\": \"safeTransferFrom\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_operator\",\"type\": \"address\"},{\"internalType\": \"bool\",\"name\": \"_approved\",\"type\": \"bool\"}],\"name\": \"setApprovalForAll\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"bytes4\",\"name\": \"_interfaceID\",\"type\": \"bytes4\"}],\"name\": \"supportsInterface\",\"outputs\": [{\"internalType\": \"bool\",\"name\": \"\",\"type\": \"bool\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [],\"name\": \"symbol\",\"outputs\": [{\"internalType\": \"string\",\"name\": \"_symbol\",\"type\": \"string\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"tokenURI\",\"outputs\": [{\"internalType\": \"string\",\"name\": \"\",\"type\": \"string\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_from\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"transferFrom\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"}]";

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
	        byte[] initNFT = EvmUtil.encodeParameter(abi, "mint", useraAddress, tokenId, "http://www.163.com");
	    	String txEncode = EvmUtil.callEvmContractWithhold(initNFT,"", 0, execer, useraPrivateKey, contractAddress);
	    	// �ٵ��ô��۽��׷������ô���˽Կ�Խ�������ǩ��
	    	createNobalance(txEncode, paracontractAddress, useraPrivateKey, withholdPrivateKey);
	        
	        // =======>  ��ѯ�û�A��ַ�µ����
	        byte[] packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress);
	        queryContract(packAbiGet, contractAddress, "ת��ǰ�û�A,NFTID=" + tokenId + "���");
	        	        
	        // =======>  ��A��ַ��B��ַת��,ʹ�ô��۽���
	        // �û�A����1��NFTת���û�B
	    	byte[] transfer = EvmUtil.encodeParameter(abi, "safeTransferFrom", useraAddress, userbAddress, tokenId);
	    	// ����ת�˽����壬�����û�A�Դ˱ʽ���ǩ����
	    	txEncode = EvmUtil.callEvmContractWithhold(transfer,"", 0, execer, useraPrivateKey, contractAddress);
	    	// �ٵ��ô��۽��׷������ô���˽Կ�Խ�������ǩ��
	    	createNobalance(txEncode, paracontractAddress, useraPrivateKey, withholdPrivateKey);

	        
	        // =======>  ��ѯ�û�A���û�B��ַ�µ����
	        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress);
	        queryContract(packAbiGet, contractAddress, "ת�˺��û�A,NFTID=" + tokenId + "���");
	        
	        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", userbAddress);
	        queryContract(packAbiGet, contractAddress, "ת�˺��û�B,NFTID=" + tokenId + "���");
	        
	        // =======>   ��ѯtoken URI
	        packAbiGet = EvmUtil.encodeParameter(abi, "tokenURI", tokenId);
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
	        byte[] evmWithCode = EvmUtil.encodeContructor(abi, "NFT name", "ART");  
            byte[] code = ByteUtil.merge(HexUtil.fromHexString(codes), evmWithCode);
	        	        
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
