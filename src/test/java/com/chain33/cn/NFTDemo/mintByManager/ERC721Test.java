package com.chain33.cn.NFTDemo.mintByManager;

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
 * NFT ERC721 发行和转让
 * @author fkeit
 *
 */
public class ERC721Test {

	// 平行链所在服务器IP地址
		String ip = "localhost";
		// 平行链服务端口
		int port = 8901;
		RpcClient client = new RpcClient(ip, port);
		
	    // 平行链名称，固定格式user.p.xxxx.样例中使用的名称叫mbaas， 根据自己平行链名称变化。  这个名称一定要和平行链配置文件中的名称完全一致。
		String paraName = "user.p.mbaas.";

		// 合约部署人（管理员）地址和私钥,地址下需要有BTY来缴纳手续费
		// 生成方式参考下面testCreateAccount方法，私钥和地址一一对应
		String managerAddress = "14nh6p7CUNtLXAHEiVkSd5mLUWynzafHBx";
		String managerPrivateKey = "7dfe80684f7007b2829a28c85be681304f7f4cf6081303dbace925826e2891d1";
//		String managerAddress = "替换成自己的地址，用下面createAccount方法生成";
	//  String managerPrivateKey = "替换成自己的私钥，用下面createAccount方法生成";
	    
	    // 用户手续费代扣地址和私钥,地址下需要有BTY来缴纳手续费
		// 生成方式参考下面testCreateAccount方法，私钥和地址一一对应
		String withholdAddress = "17RH6oiMbUjat3AAyQeifNiACPFefvz3Au";
	    String withholdPrivateKey = "56d1272fcf806c3c5105f3536e39c8b33f88cb8971011dfe5886159201884763";
//		String withholdAddress = "替换成自己的地址，用下面createAccount方法生成";
//	    String withholdPrivateKey = "替换成自己的私钥，用下面createAccount方法生成";
	    
	    // 用户A地址和私钥
		String useraAddress;
	    String useraPrivateKey;
	    
	    // 用户B地址和私钥
		String userbAddress;
	    String userbPrivateKey;
	    
	    // solidity合约源码见：./solidity/ERC1155.sol
	    // 合约编译出来的bytecode
	    String codes = "60806040523480156200001157600080fd5b5060405162002eb138038062002eb18339818101604052810190620000379190620002c7565b60016000806301ffc9a760e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060006101000a81548160ff02191690831515021790555060016000806380ac58cd60e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060006101000a81548160ff0219169083151502179055506001600080635b5e139f60e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060006101000a81548160ff021916908315150217905550816005908051906020019062000142929190620001a5565b5080600690805190602001906200015b929190620001a5565b5033600860006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050506200046b565b828054620001b390620003d7565b90600052602060002090601f016020900481019282620001d7576000855562000223565b82601f10620001f257805160ff191683800117855562000223565b8280016001018555821562000223579182015b828111156200022257825182559160200191906001019062000205565b5b50905062000232919062000236565b5090565b5b808211156200025157600081600090555060010162000237565b5090565b60006200026c62000266846200036e565b6200033a565b9050828152602081018484840111156200028557600080fd5b62000292848285620003a1565b509392505050565b600082601f830112620002ac57600080fd5b8151620002be84826020860162000255565b91505092915050565b60008060408385031215620002db57600080fd5b600083015167ffffffffffffffff811115620002f657600080fd5b62000304858286016200029a565b925050602083015167ffffffffffffffff8111156200032257600080fd5b62000330858286016200029a565b9150509250929050565b6000604051905081810181811067ffffffffffffffff821117156200036457620003636200043c565b5b8060405250919050565b600067ffffffffffffffff8211156200038c576200038b6200043c565b5b601f19601f8301169050602081019050919050565b60005b83811015620003c1578082015181840152602081019050620003a4565b83811115620003d1576000848401525b50505050565b60006002820490506001821680620003f057607f821691505b602082108114156200040757620004066200040d565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b612a36806200047b6000396000f3fe608060405234801561001057600080fd5b50600436106100f55760003560e01c806370a0823111610097578063b88d4fde11610066578063b88d4fde14610284578063c87b56dd146102a0578063d3fc9864146102d0578063e985e9c5146102ec576100f5565b806370a08231146101fc57806395d89b411461022c578063a22cb4651461024a578063b2bdfa7b14610266576100f5565b8063095ea7b3116100d3578063095ea7b31461017857806323b872dd1461019457806342842e0e146101b05780636352211e146101cc576100f5565b806301ffc9a7146100fa57806306fdde031461012a578063081812fc14610148575b600080fd5b610114600480360381019061010f919061253b565b61031c565b6040516101219190612722565b60405180910390f35b610132610383565b60405161013f919061273d565b60405180910390f35b610162600480360381019061015d919061258d565b610415565b60405161016f91906126bb565b60405180910390f35b610192600480360381019061018d9190612493565b610530565b005b6101ae60048036038101906101a99190612388565b610913565b005b6101ca60048036038101906101c59190612388565b610d65565b005b6101e660048036038101906101e1919061258d565b610d85565b6040516101f391906126bb565b60405180910390f35b61021660048036038101906102119190612323565b610e6b565b604051610223919061277f565b60405180910390f35b610234610f25565b604051610241919061273d565b60405180910390f35b610264600480360381019061025f9190612457565b610fb7565b005b61026e6110b4565b60405161027b91906126bb565b60405180910390f35b61029e600480360381019061029991906123d7565b6110da565b005b6102ba60048036038101906102b5919061258d565b611131565b6040516102c7919061273d565b60405180910390f35b6102ea60048036038101906102e591906124cf565b611221565b005b6103066004803603810190610301919061234c565b61130f565b6040516103139190612722565b60405180910390f35b6000806000837bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19167bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916815260200190815260200160002060009054906101000a900460ff169050919050565b60606005805461039290612903565b80601f01602080910402602001604051908101604052809291908181526020018280546103be90612903565b801561040b5780601f106103e05761010080835404028352916020019161040b565b820191906000526020600020905b8154815290600101906020018083116103ee57829003601f168201915b5050505050905090565b600081600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f3030333030320000000000000000000000000000000000000000000000000000815250906104f3576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104ea919061273d565b60405180910390fd5b506002600084815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16915050919050565b8060006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690503373ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614806106295750600460008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b6040518060400160405280600681526020017f3030333030330000000000000000000000000000000000000000000000000000815250906106a0576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610697919061273d565b60405180910390fd5b5082600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f30303330303200000000000000000000000000000000000000000000000000008152509061077d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610774919061273d565b60405180910390fd5b5060006001600086815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508073ffffffffffffffffffffffffffffffffffffffff168673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f30303330303800000000000000000000000000000000000000000000000000008152509061085d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610854919061273d565b60405180910390fd5b50856002600087815260200190815260200160002060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550848673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92560405160405180910390a4505050505050565b8060006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690503373ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614806109e457503373ffffffffffffffffffffffffffffffffffffffff166002600084815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b80610a755750600460008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b6040518060400160405280600681526020017f303033303034000000000000000000000000000000000000000000000000000081525090610aec576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ae3919061273d565b60405180910390fd5b5082600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303032000000000000000000000000000000000000000000000000000081525090610bc9576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610bc0919061273d565b60405180910390fd5b5060006001600086815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508673ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f303033303037000000000000000000000000000000000000000000000000000081525090610ca8576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610c9f919061273d565b60405180910390fd5b50600073ffffffffffffffffffffffffffffffffffffffff168673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303031000000000000000000000000000000000000000000000000000081525090610d51576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610d48919061273d565b60405180910390fd5b50610d5c86866113a3565b50505050505050565b610d8083838360405180602001604052806000815250611458565b505050565b60006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303032000000000000000000000000000000000000000000000000000081525090610e65576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610e5c919061273d565b60405180910390fd5b50919050565b60008073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303031000000000000000000000000000000000000000000000000000081525090610f14576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610f0b919061273d565b60405180910390fd5b50610f1e82611a26565b9050919050565b606060068054610f3490612903565b80601f0160208091040260200160405190810160405280929190818152602001828054610f6090612903565b8015610fad5780601f10610f8257610100808354040283529160200191610fad565b820191906000526020600020905b815481529060010190602001808311610f9057829003601f168201915b5050505050905090565b80600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f17307eab39ab6107e8899845ad3d59bd9653f200f220920489ca2b5937696c31836040516110a89190612722565b60405180910390a35050565b600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b61112a85858585858080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050611458565b5050505050565b606081600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f30303330303200000000000000000000000000000000000000000000000000008152509061120f576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611206919061273d565b60405180910390fd5b5061121983611a6f565b915050919050565b600860009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146112b1576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016112a89061275f565b60405180910390fd5b6112bb8484611b14565b6113098383838080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f82011690508083019250505050505050611d02565b50505050565b6000600460008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905092915050565b60006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506113e482611e0c565b6113ee8183611e45565b6113f88383611fb0565b818373ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef60405160405180910390a4505050565b8160006001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690503373ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16148061152957503373ffffffffffffffffffffffffffffffffffffffff166002600084815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b806115ba5750600460008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff165b6040518060400160405280600681526020017f303033303034000000000000000000000000000000000000000000000000000081525090611631576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611628919061273d565b60405180910390fd5b5083600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f30303330303200000000000000000000000000000000000000000000000000008152509061170e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611705919061273d565b60405180910390fd5b5060006001600087815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508773ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f3030333030370000000000000000000000000000000000000000000000000000815250906117ed576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016117e4919061273d565b60405180910390fd5b50600073ffffffffffffffffffffffffffffffffffffffff168773ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303031000000000000000000000000000000000000000000000000000081525090611896576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161188d919061273d565b60405180910390fd5b506118a187876113a3565b6118c08773ffffffffffffffffffffffffffffffffffffffff16612138565b15611a1c5760008773ffffffffffffffffffffffffffffffffffffffff1663150b7a02338b8a8a6040518563ffffffff1660e01b815260040161190694939291906126d6565b602060405180830381600087803b15801561192057600080fd5b505af1158015611934573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052508101906119589190612564565b905063150b7a0260e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916817bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916146040518060400160405280600681526020017f303033303035000000000000000000000000000000000000000000000000000081525090611a19576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611a10919061273d565b60405180910390fd5b50505b5050505050505050565b6000600360008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b6060600760008381526020019081526020016000208054611a8f90612903565b80601f0160208091040260200160405190810160405280929190818152602001828054611abb90612903565b8015611b085780601f10611add57610100808354040283529160200191611b08565b820191906000526020600020905b815481529060010190602001808311611aeb57829003601f168201915b50505050509050919050565b600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303031000000000000000000000000000000000000000000000000000081525090611bbc576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611bb3919061273d565b60405180910390fd5b50600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f303033303036000000000000000000000000000000000000000000000000000081525090611c97576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611c8e919061273d565b60405180910390fd5b50611ca28282611fb0565b808273ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef60405160405180910390a45050565b81600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614156040518060400160405280600681526020017f303033303032000000000000000000000000000000000000000000000000000081525090611dde576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611dd5919061273d565b60405180910390fd5b5081600760008581526020019081526020016000209080519060200190611e06929190612183565b50505050565b6002600082815260200190815260200160002060006101000a81549073ffffffffffffffffffffffffffffffffffffffff021916905550565b8173ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f303033303037000000000000000000000000000000000000000000000000000081525090611f1e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611f15919061273d565b60405180910390fd5b506001600360008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254611f6f9190612828565b925050819055506001600082815260200190815260200160002060006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690555050565b600073ffffffffffffffffffffffffffffffffffffffff166001600083815260200190815260200160002060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146040518060400160405280600681526020017f30303330303600000000000000000000000000000000000000000000000000008152509061208a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401612081919061273d565b60405180910390fd5b50816001600083815260200190815260200160002060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600360008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825461212d91906127d2565b925050819055505050565b60008060007fc5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a47060001b9050833f91506000801b821415801561217a5750808214155b92505050919050565b82805461218f90612903565b90600052602060002090601f0160209004810192826121b157600085556121f8565b82601f106121ca57805160ff19168380011785556121f8565b828001600101855582156121f8579182015b828111156121f75782518255916020019190600101906121dc565b5b5090506122059190612209565b5090565b5b8082111561222257600081600090555060010161220a565b5090565b600081359050612235816129a4565b92915050565b60008135905061224a816129bb565b92915050565b60008135905061225f816129d2565b92915050565b600081519050612274816129d2565b92915050565b60008083601f84011261228c57600080fd5b8235905067ffffffffffffffff8111156122a557600080fd5b6020830191508360018202830111156122bd57600080fd5b9250929050565b60008083601f8401126122d657600080fd5b8235905067ffffffffffffffff8111156122ef57600080fd5b60208301915083600182028301111561230757600080fd5b9250929050565b60008135905061231d816129e9565b92915050565b60006020828403121561233557600080fd5b600061234384828501612226565b91505092915050565b6000806040838503121561235f57600080fd5b600061236d85828601612226565b925050602061237e85828601612226565b9150509250929050565b60008060006060848603121561239d57600080fd5b60006123ab86828701612226565b93505060206123bc86828701612226565b92505060406123cd8682870161230e565b9150509250925092565b6000806000806000608086880312156123ef57600080fd5b60006123fd88828901612226565b955050602061240e88828901612226565b945050604061241f8882890161230e565b935050606086013567ffffffffffffffff81111561243c57600080fd5b6124488882890161227a565b92509250509295509295909350565b6000806040838503121561246a57600080fd5b600061247885828601612226565b92505060206124898582860161223b565b9150509250929050565b600080604083850312156124a657600080fd5b60006124b485828601612226565b92505060206124c58582860161230e565b9150509250929050565b600080600080606085870312156124e557600080fd5b60006124f387828801612226565b94505060206125048782880161230e565b935050604085013567ffffffffffffffff81111561252157600080fd5b61252d878288016122c4565b925092505092959194509250565b60006020828403121561254d57600080fd5b600061255b84828501612250565b91505092915050565b60006020828403121561257657600080fd5b600061258484828501612265565b91505092915050565b60006020828403121561259f57600080fd5b60006125ad8482850161230e565b91505092915050565b6125bf8161285c565b82525050565b6125ce8161286e565b82525050565b60006125df8261279a565b6125e981856127b0565b93506125f98185602086016128d0565b61260281612993565b840191505092915050565b6000612618826127a5565b61262281856127c1565b93506126328185602086016128d0565b61263b81612993565b840191505092915050565b60006126536023836127c1565b91507f6f6e6c7920617574686f72697a6564206f776e65722063616e206d696e74206e60008301527f66742e00000000000000000000000000000000000000000000000000000000006020830152604082019050919050565b6126b5816128c6565b82525050565b60006020820190506126d060008301846125b6565b92915050565b60006080820190506126eb60008301876125b6565b6126f860208301866125b6565b61270560408301856126ac565b818103606083015261271781846125d4565b905095945050505050565b600060208201905061273760008301846125c5565b92915050565b60006020820190508181036000830152612757818461260d565b905092915050565b6000602082019050818103600083015261277881612646565b9050919050565b600060208201905061279460008301846126ac565b92915050565b600081519050919050565b600081519050919050565b600082825260208201905092915050565b600082825260208201905092915050565b60006127dd826128c6565b91506127e8836128c6565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0382111561281d5761281c612935565b5b828201905092915050565b6000612833826128c6565b915061283e836128c6565b92508282101561285157612850612935565b5b828203905092915050565b6000612867826128a6565b9050919050565b60008115159050919050565b60007fffffffff0000000000000000000000000000000000000000000000000000000082169050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b60005b838110156128ee5780820151818401526020810190506128d3565b838111156128fd576000848401525b50505050565b6000600282049050600182168061291b57607f821691505b6020821081141561292f5761292e612964565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000601f19601f8301169050919050565b6129ad8161285c565b81146129b857600080fd5b50565b6129c48161286e565b81146129cf57600080fd5b50565b6129db8161287a565b81146129e657600080fd5b50565b6129f2816128c6565b81146129fd57600080fd5b5056fea26469706673582212201b0d637fb7d7ad03d2ee6df624568bab48583aadb0134de305f2367aeea64a8964736f6c63430008000033";
	    // 合约对应的abi
	    String abi = "[{\"inputs\": [{\"internalType\": \"string\",\"name\": \"_name\",\"type\": \"string\"},{\"internalType\": \"string\",\"name\": \"_symbol\",\"type\": \"string\"}],\"stateMutability\": \"nonpayable\",\"type\": \"constructor\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_approved\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"Approval\",\"type\": \"event\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_operator\",\"type\": \"address\"},{\"indexed\": false,\"internalType\": \"bool\",\"name\": \"_approved\",\"type\": \"bool\"}],\"name\": \"ApprovalForAll\",\"type\": \"event\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_from\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"Transfer\",\"type\": \"event\"},{\"inputs\": [],\"name\": \"_owner\",\"outputs\": [{\"internalType\": \"address\",\"name\": \"\",\"type\": \"address\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_approved\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"approve\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"}],\"name\": \"balanceOf\",\"outputs\": [{\"internalType\": \"uint256\",\"name\": \"\",\"type\": \"uint256\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"getApproved\",\"outputs\": [{\"internalType\": \"address\",\"name\": \"\",\"type\": \"address\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"_operator\",\"type\": \"address\"}],\"name\": \"isApprovedForAll\",\"outputs\": [{\"internalType\": \"bool\",\"name\": \"\",\"type\": \"bool\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"},{\"internalType\": \"string\",\"name\": \"_uri\",\"type\": \"string\"}],\"name\": \"mint\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [],\"name\": \"name\",\"outputs\": [{\"internalType\": \"string\",\"name\": \"_name\",\"type\": \"string\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"ownerOf\",\"outputs\": [{\"internalType\": \"address\",\"name\": \"_owner\",\"type\": \"address\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_from\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"safeTransferFrom\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_from\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"},{\"internalType\": \"bytes\",\"name\": \"_data\",\"type\": \"bytes\"}],\"name\": \"safeTransferFrom\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_operator\",\"type\": \"address\"},{\"internalType\": \"bool\",\"name\": \"_approved\",\"type\": \"bool\"}],\"name\": \"setApprovalForAll\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"bytes4\",\"name\": \"_interfaceID\",\"type\": \"bytes4\"}],\"name\": \"supportsInterface\",\"outputs\": [{\"internalType\": \"bool\",\"name\": \"\",\"type\": \"bool\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [],\"name\": \"symbol\",\"outputs\": [{\"internalType\": \"string\",\"name\": \"_symbol\",\"type\": \"string\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"tokenURI\",\"outputs\": [{\"internalType\": \"string\",\"name\": \"\",\"type\": \"string\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_from\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"_tokenId\",\"type\": \"uint256\"}],\"name\": \"transferFrom\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"}]";

	    /**
	     * ERC721合约部署，调用测试
	     * @throws Exception 
	     */
	    @Test
	    public void testERC721() throws Exception {
	    	
	        // 手续费，可以固定设置一个较大的值，保证交易能成功，此处设置0.01个BTY
	        long fee = 1000000;
	    	
	    	// =======> 为用户A和B生成私钥和地址
	    	AccountInfo infoA = createAccount();
	    	useraAddress = infoA.getAddress();
	    	useraPrivateKey = infoA.getPrivateKey();
	    	
	    	AccountInfo infoB = createAccount();
	    	userbAddress = infoB.getAddress();
	    	userbPrivateKey = infoB.getPrivateKey();
	    	
	    	// =======>  通过管理员部署合约
	        // 部署合约, 参数： 平行链合约名， 签名地址，签名私钥
	        String hash = deployContract(paraName, managerAddress, managerPrivateKey);
	        
	        // 计算上一步部署到链上的合约地址
	        String contractAddress = TransactionUtil.convertExectoAddr(managerAddress + hash.substring(2));
	        System.out.println("部署好的合约地址 = " + contractAddress);
	        
	        // =======>  调用合约发行NFT,ERC721只支持一物一token
	        // 构造合约调用, mint对应solidity合约里的方法名， 后面三个参数分别代表:NFT发在用户A下, NFT编号, URI信息，比如可以存储NFT图片存储的网址或ipfs地址等
	        int tokenId = 10000;
	        byte[] initNFT = EvmUtil.encodeParameter(abi, "mint", useraAddress, tokenId, "http://www.163.com");

	        hash = callContract(initNFT, contractAddress, managerAddress, managerPrivateKey, paraName, fee);
	        
	        // =======>  查询用户A地址下的余额
	        byte[] packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress);
	        queryContract(packAbiGet, contractAddress, "转账前用户A,NFTID=" + tokenId + "余额");
	        	        
	        // =======>  从A地址向B地址转账,使用代扣交易
	        // 代扣交易需要对平行链合约地址做一个处理
	        String execer = paraName + "evm";
	        // 平行链合约地址计算(平行链title前缀+合约名称)
	        String paracontractAddress = client.convertExectoAddr(execer);
	        // 用户A将第1个NFT转给用户B
	    	byte[] transfer = EvmUtil.encodeParameter(abi, "safeTransferFrom", useraAddress, userbAddress, tokenId);
	    	// 构造转账交易体，先用用户A对此笔交易签名，
	    	String txEncode = EvmUtil.callEvmContractWithhold(transfer,"", 0, execer, useraPrivateKey, contractAddress);
	    	// 再调用代扣交易方法，用代扣私钥对交易组做签名
	    	createNobalance(txEncode, paracontractAddress, useraPrivateKey, withholdPrivateKey);

	        
	        // =======>  查询用户A和用户B地址下的余额
	        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress);
	        queryContract(packAbiGet, contractAddress, "转账后用户A,NFTID=" + tokenId + "余额");
	        
	        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", userbAddress);
	        queryContract(packAbiGet, contractAddress, "转账后用户B,NFTID=" + tokenId + "余额");
	        
	        // =======>   查询token URI
	        packAbiGet = EvmUtil.encodeParameter(abi, "tokenURI", tokenId);
	        queryContractString(packAbiGet, contractAddress, "NFTID=" + tokenId + "的URI信息");
	    }
	    
	    /**
	     * Step1: 生成私钥，地址
	     * 一般在用户注册时调用，生成后在数据库中和用户信息绑定，后续直接从库中查出来使用
	     */
	    private AccountInfo createAccount() {
	    	Account account = new Account();
			AccountInfo accountInfo = account.newAccountLocal();
			return accountInfo;
	    }
	    
	    /**
	     * Step2:部署合约
	     * @throws Exception
	     */
	    private String deployContract(String execer, String address, String privateKey) throws Exception {

	        // 部署合约
	        String txEncode;
	        String txhash = "";
	        QueryTransactionResult txResult = new QueryTransactionResult();
	        
	        // 此合约的构造参数有参数，此处为构造方式
	        byte[] evmWithCode = EvmUtil.encodeContructor(abi, "NFT name", "ART");  
            byte[] code = ByteUtil.merge(HexUtil.fromHexString(codes), evmWithCode);
	        	        
	    	// TODO: 估算合约GAS费， 实际应用过程中，不建议在业务代码中直接调用gas费， 只是做预估使用。  实际可以在代码里设置一个大于这个值的数
	        String evmCode = EvmUtil.getCreateEvmEncode(code, "", "deploy ERC721 contract", execer);
	        long gas = client.queryEVMGas("evm", evmCode, address);
	        System.out.println("Gas fee is:" + gas);
	        
	        // 通过合约code, 管理员私钥，平行链名称+evm,手续费等参数构造部署合约交易，并签名
	        txEncode = EvmUtil.createEvmContract(code, "", "deploy ERC721 contract", privateKey, execer, gas);
	        // 将构造并签好名的交易通过rpc接口发送到平行链上
	        txhash = client.submitTransaction(txEncode);
	        System.out.println("部署合约交易hash = " + txhash);
	        
	        // BTY平均3-5秒一个区块确认， 需要延时去查结果
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
				System.out.println("合约部署成功");

			} else {
				System.out.println("合约部署失败，一般失败原因可能是因为地址下手续费不够");
			}
			
			return txhash;
	    }
	    
	    /**
	     * Step3: 调用合约
	     * @param contractAddr
	     * @param address
	     * @param privateKey
	     * @throws IOException 
	     * @throws InterruptedException 
	     */
	    private String callContract(byte[] code, String contractAddr, String address, String privateKey, String execer, long gas) throws Exception {
	    	
	        // 调用合约
	        String txEncode;
	        String txhash = "";
	        QueryTransactionResult txResult = new QueryTransactionResult();
	    	
	    	txEncode = EvmUtil.callEvmContract(code,"", 0, contractAddr, privateKey, execer, gas);
	        txhash = client.submitTransaction(txEncode);
	        System.out.println("调用合约hash = " + txhash);
	        
	        // BTY平均3-5秒一个区块确认， 需要延时去查结果
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
				System.out.println("合约调用成功");
				
			} else {
				System.out.println("合约调用失败，一般失败原因可能是因为地址下手续费不够");
			}
			
			return txhash;
	    	
	    }
	    
	    /**
	     * 查询方法
	     * @param queryAbi
	     * @param contractAddress
	     * @throws Exception 
	     */
	    private void queryContract(byte[] queryAbi, String contractAddress, String title) throws Exception {
	        // 查询用户A和用户B地址下的资产余额
	        JSONObject query = client.callEVMAbi(contractAddress, HexUtil.toHexString(queryAbi));
	        JSONObject output = query.getJSONObject("result");
	        String rawData = output.getString("rawData");
	        System.out.println(title + ": " + HexUtil.hexStringToAlgorism(HexUtil.removeHexHeader(rawData)));
	    }
	    
	    /**
	     * 查询方法
	     * @param queryAbi
	     * @param contractAddress
	     * @throws Exception 
	     */
	    private void queryContractString(byte[] queryAbi, String contractAddress, String title) throws Exception {
	        // 查询用户A和用户B地址下的资产余额
	        JSONObject query = client.callEVMAbi(contractAddress, HexUtil.toHexString(queryAbi));
	        JSONObject output = query.getJSONObject("result");
	        String rawData = output.getString("rawData");
	        System.out.println(title + ": " + HexUtil.hexStringToString(HexUtil.removeHexHeader(rawData)));
	    }
	    
	    
	    /**
	     * 构建代扣手续费交易
	     * 
	     * @param txEncode
	     * @param contranctAddress
	     * @return
	     * @throws InterruptedException
	     * @throws IOException 
	     */
	    private String createNobalance(String txEncode, String contranctAddress, String userPrivatekey, String withHoldPrivateKey) throws Exception {
	        String createNoBalanceTx = client.createNoBalanceTx(txEncode, "");
		    // 解析交易
		    List<DecodeRawTransaction> decodeRawTransactions = client.decodeRawTransaction(createNoBalanceTx);
		    
		    String hexString = TransactionUtil.signDecodeTx(decodeRawTransactions, contranctAddress, userPrivatekey, withHoldPrivateKey);
		    String submitTransaction = client.submitTransaction(hexString);
		    System.out.println("代扣hash= " + submitTransaction);
		    
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
				System.out.println("合约调用成功");
				
			} else {
				System.out.println("合约调用失败，一般失败原因可能是因为地址下手续费不够");
			}
			return nextString;
	    }
}
