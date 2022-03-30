package com.chain33.cn.NFTDemo.localTest.soloAndPara.mintByUser;

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
 * NFT ERC1155 发行和转让
 *
 */
public class ERC1155Test {

	// 平行链所在服务器IP地址
	String ip = "172.22.16.251";
	// 平行链服务端口
	int port = 8901;
	RpcClient client = new RpcClient(ip, port);
	
    // 平行链名称，固定格式user.p.xxxx.样例中使用的名称叫mbaas， 根据自己平行链名称变化。  这个名称一定要和平行链配置文件中的名称完全一致。
	String paraName = "user.p.mbaas.";

	// 合约部署人（管理员）地址和私钥,地址下需要有BTY来缴纳手续费
	// 生成方式参考下面testCreateAccount方法，私钥和地址一一对应
//	String managerAddress = "14nh6p7CUNtLXAHEiVkSd5mLUWynzafHBx";
//	String managerPrivateKey = "7dfe80684f7007b2829a28c85be681304f7f4cf6081303dbace925826e2891d1";
	String managerAddress = "替换成自己的地址，用下面createAccount方法生成";
	String managerPrivateKey = "替换成自己的私钥，用下面createAccount方法生成";
    
    // 用户手续费代扣地址和私钥,地址下需要有BTY来缴纳手续费
	// 生成方式参考下面testCreateAccount方法，私钥和地址一一对应
//	String withholdAddress = "17RH6oiMbUjat3AAyQeifNiACPFefvz3Au";
//    String withholdPrivateKey = "56d1272fcf806c3c5105f3536e39c8b33f88cb8971011dfe5886159201884763";
	String withholdAddress = "替换成自己的地址，用下面createAccount方法生成";
    String withholdPrivateKey = "替换成自己的私钥，用下面createAccount方法生成";
    
    // 用户A地址和私钥
	String useraAddress;
    String useraPrivateKey;
    
    // 用户B地址和私钥
	String userbAddress;
    String userbPrivateKey;
    
    // solidity合约源码见：./solidity/ERC1155.sol
    // 合约编译出来的bytecode
    String codes = "60806040523480156200001157600080fd5b506040518060200160405280600081525062000033816200007b60201b60201c565b5033600360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550620001ac565b80600290805190602001906200009392919062000097565b5050565b828054620000a59062000147565b90600052602060002090601f016020900481019282620000c9576000855562000115565b82601f10620000e457805160ff191683800117855562000115565b8280016001018555821562000115579182015b8281111562000114578251825591602001919060010190620000f7565b5b50905062000124919062000128565b5090565b5b808211156200014357600081600090555060010162000129565b5090565b600060028204905060018216806200016057607f821691505b602082108114156200017757620001766200017d565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b612a2b80620001bc6000396000f3fe608060405234801561001057600080fd5b50600436106100a85760003560e01c80639727756a116100715780639727756a14610189578063a22cb465146101a5578063ab918735146101c1578063b2bdfa7b146101dd578063e985e9c5146101fb578063f242432a1461022b576100a8565b8062fdd58e146100ad57806301ffc9a7146100dd5780630e89341c1461010d5780632eb2c2d61461013d5780634e1273f414610159575b600080fd5b6100c760048036038101906100c29190611b78565b610247565b6040516100d49190612543565b60405180910390f35b6100f760048036038101906100f29190611c6f565b610310565b60405161010491906123a6565b60405180910390f35b61012760048036038101906101229190611cc1565b6103f2565b60405161013491906123c1565b60405180910390f35b6101576004803603810190610152919061196f565b610486565b005b610173600480360381019061016e9190611c03565b610527565b604051610180919061234d565b60405180910390f35b6101a3600480360381019061019e9190611abd565b6106d8565b005b6101bf60048036038101906101ba9190611b3c565b6106f8565b005b6101db60048036038101906101d69190611bb4565b61070e565b005b6101e561072f565b6040516101f29190612270565b60405180910390f35b61021560048036038101906102109190611933565b610755565b60405161022291906123a6565b60405180910390f35b61024560048036038101906102409190611a2e565b6107e9565b005b60008073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614156102b8576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102af90612423565b60405180910390fd5b60008083815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b60007fd9b67a26000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191614806103db57507f0e89341c000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916145b806103eb57506103ea8261088a565b5b9050919050565b606060028054610401906127bd565b80601f016020809104026020016040519081016040528092919081815260200182805461042d906127bd565b801561047a5780601f1061044f5761010080835404028352916020019161047a565b820191906000526020600020905b81548152906001019060200180831161045d57829003601f168201915b50505050509050919050565b61048e6108f4565b73ffffffffffffffffffffffffffffffffffffffff168573ffffffffffffffffffffffffffffffffffffffff1614806104d457506104d3856104ce6108f4565b610755565b5b610513576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161050a90612483565b60405180910390fd5b61052085858585856108fc565b5050505050565b6060815183511461056d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610564906124e3565b60405180910390fd5b6000835167ffffffffffffffff8111156105b0577f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6040519080825280602002602001820160405280156105de5781602001602082028036833780820191505090505b50905060005b84518110156106cd57610677858281518110610629577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b602002602001015185838151811061066a577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b6020026020010151610247565b8282815181106106b0577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b602002602001018181525050806106c6906127ef565b90506105e4565b508091505092915050565b6106f383838360405180602001604052806000815250610c5c565b505050565b61070a6107036108f4565b8383610ec6565b5050565b61072a33848484604051806020016040528060008152506107e9565b505050565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905092915050565b6107f16108f4565b73ffffffffffffffffffffffffffffffffffffffff168573ffffffffffffffffffffffffffffffffffffffff1614806108375750610836856108316108f4565b610755565b5b610876576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161086d90612443565b60405180910390fd5b6108838585858585611033565b5050505050565b60007f01ffc9a7000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916149050919050565b600033905090565b8151835114610940576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161093790612503565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff1614156109b0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016109a790612463565b60405180910390fd5b60006109ba6108f4565b90506109ca8187878787876112b5565b60005b8451811015610bc7576000858281518110610a11577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b602002602001015190506000858381518110610a56577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b60200260200101519050600080600084815260200190815260200160002060008b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905081811015610af7576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610aee906124a3565b60405180910390fd5b81810360008085815260200190815260200160002060008c73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508160008085815260200190815260200160002060008b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254610bac91906126b1565b9250508190555050505080610bc0906127ef565b90506109cd565b508473ffffffffffffffffffffffffffffffffffffffff168673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167f4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb8787604051610c3e92919061236f565b60405180910390a4610c548187878787876112bd565b505050505050565b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff161415610ccc576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610cc390612523565b60405180910390fd5b8151835114610d10576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610d0790612503565b60405180910390fd5b6000610d1a6108f4565b9050610d2b816000878787876112b5565b60005b8451811015610e3057838181518110610d70577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b6020026020010151600080878481518110610db4577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b6020026020010151815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254610e1691906126b1565b925050819055508080610e28906127ef565b915050610d2e565b508473ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167f4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb8787604051610ea892919061236f565b60405180910390a4610ebf816000878787876112bd565b5050505050565b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415610f35576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610f2c906124c3565b60405180910390fd5b80600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167f17307eab39ab6107e8899845ad3d59bd9653f200f220920489ca2b5937696c318360405161102691906123a6565b60405180910390a3505050565b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff1614156110a3576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161109a90612463565b60405180910390fd5b60006110ad6108f4565b90506110cd8187876110be8861148d565b6110c78861148d565b876112b5565b600080600086815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905083811015611164576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161115b906124a3565b60405180910390fd5b83810360008087815260200190815260200160002060008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508360008087815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825461121991906126b1565b925050819055508573ffffffffffffffffffffffffffffffffffffffff168773ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f62888860405161129692919061255e565b60405180910390a46112ac828888888888611553565b50505050505050565b505050505050565b6112dc8473ffffffffffffffffffffffffffffffffffffffff16611723565b15611485578373ffffffffffffffffffffffffffffffffffffffff1663bc197c8187878686866040518663ffffffff1660e01b815260040161132295949392919061228b565b602060405180830381600087803b15801561133c57600080fd5b505af192505050801561136d57506040513d601f19601f8201168201806040525081019061136a9190611c98565b60015b6113fc576113796128e3565b8061138457506113c1565b806040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016113b891906123c1565b60405180910390fd5b6040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016113f3906123e3565b60405180910390fd5b63bc197c8160e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916817bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191614611483576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161147a90612403565b60405180910390fd5b505b505050505050565b60606000600167ffffffffffffffff8111156114d2577f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6040519080825280602002602001820160405280156115005781602001602082028036833780820191505090505b509050828160008151811061153e577f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b60200260200101818152505080915050919050565b6115728473ffffffffffffffffffffffffffffffffffffffff16611723565b1561171b578373ffffffffffffffffffffffffffffffffffffffff1663f23a6e6187878686866040518663ffffffff1660e01b81526004016115b89594939291906122f3565b602060405180830381600087803b1580156115d257600080fd5b505af192505050801561160357506040513d601f19601f820116820180604052508101906116009190611c98565b60015b6116925761160f6128e3565b8061161a5750611657565b806040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161164e91906123c1565b60405180910390fd5b6040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611689906123e3565b60405180910390fd5b63f23a6e6160e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916817bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191614611719576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161171090612403565b60405180910390fd5b505b505050505050565b600080823b905060008111915050919050565b6000611749611744846125b8565b612587565b9050808382526020820190508285602086028201111561176857600080fd5b60005b85811015611798578161177e888261184c565b84526020840193506020830192505060018101905061176b565b5050509392505050565b60006117b56117b0846125e4565b612587565b905080838252602082019050828560208602820111156117d457600080fd5b60005b8581101561180457816117ea888261191e565b8452602084019350602083019250506001810190506117d7565b5050509392505050565b600061182161181c84612610565b612587565b90508281526020810184848401111561183957600080fd5b61184484828561277b565b509392505050565b60008135905061185b81612999565b92915050565b600082601f83011261187257600080fd5b8135611882848260208601611736565b91505092915050565b600082601f83011261189c57600080fd5b81356118ac8482602086016117a2565b91505092915050565b6000813590506118c4816129b0565b92915050565b6000813590506118d9816129c7565b92915050565b6000815190506118ee816129c7565b92915050565b600082601f83011261190557600080fd5b813561191584826020860161180e565b91505092915050565b60008135905061192d816129de565b92915050565b6000806040838503121561194657600080fd5b60006119548582860161184c565b92505060206119658582860161184c565b9150509250929050565b600080600080600060a0868803121561198757600080fd5b60006119958882890161184c565b95505060206119a68882890161184c565b945050604086013567ffffffffffffffff8111156119c357600080fd5b6119cf8882890161188b565b935050606086013567ffffffffffffffff8111156119ec57600080fd5b6119f88882890161188b565b925050608086013567ffffffffffffffff811115611a1557600080fd5b611a21888289016118f4565b9150509295509295909350565b600080600080600060a08688031215611a4657600080fd5b6000611a548882890161184c565b9550506020611a658882890161184c565b9450506040611a768882890161191e565b9350506060611a878882890161191e565b925050608086013567ffffffffffffffff811115611aa457600080fd5b611ab0888289016118f4565b9150509295509295909350565b600080600060608486031215611ad257600080fd5b6000611ae08682870161184c565b935050602084013567ffffffffffffffff811115611afd57600080fd5b611b098682870161188b565b925050604084013567ffffffffffffffff811115611b2657600080fd5b611b328682870161188b565b9150509250925092565b60008060408385031215611b4f57600080fd5b6000611b5d8582860161184c565b9250506020611b6e858286016118b5565b9150509250929050565b60008060408385031215611b8b57600080fd5b6000611b998582860161184c565b9250506020611baa8582860161191e565b9150509250929050565b600080600060608486031215611bc957600080fd5b6000611bd78682870161184c565b9350506020611be88682870161191e565b9250506040611bf98682870161191e565b9150509250925092565b60008060408385031215611c1657600080fd5b600083013567ffffffffffffffff811115611c3057600080fd5b611c3c85828601611861565b925050602083013567ffffffffffffffff811115611c5957600080fd5b611c658582860161188b565b9150509250929050565b600060208284031215611c8157600080fd5b6000611c8f848285016118ca565b91505092915050565b600060208284031215611caa57600080fd5b6000611cb8848285016118df565b91505092915050565b600060208284031215611cd357600080fd5b6000611ce18482850161191e565b91505092915050565b6000611cf68383612252565b60208301905092915050565b611d0b81612707565b82525050565b6000611d1c82612650565b611d26818561267e565b9350611d3183612640565b8060005b83811015611d62578151611d498882611cea565b9750611d5483612671565b925050600181019050611d35565b5085935050505092915050565b611d7881612719565b82525050565b6000611d898261265b565b611d93818561268f565b9350611da381856020860161278a565b611dac816128c5565b840191505092915050565b6000611dc282612666565b611dcc81856126a0565b9350611ddc81856020860161278a565b611de5816128c5565b840191505092915050565b6000611dfd6034836126a0565b91507f455243313135353a207472616e7366657220746f206e6f6e204552433131353560008301527f526563656976657220696d706c656d656e7465720000000000000000000000006020830152604082019050919050565b6000611e636028836126a0565b91507f455243313135353a204552433131353552656365697665722072656a6563746560008301527f6420746f6b656e730000000000000000000000000000000000000000000000006020830152604082019050919050565b6000611ec9602b836126a0565b91507f455243313135353a2062616c616e636520717565727920666f7220746865207a60008301527f65726f20616464726573730000000000000000000000000000000000000000006020830152604082019050919050565b6000611f2f6029836126a0565b91507f455243313135353a2063616c6c6572206973206e6f74206f776e6572206e6f7260008301527f20617070726f76656400000000000000000000000000000000000000000000006020830152604082019050919050565b6000611f956025836126a0565b91507f455243313135353a207472616e7366657220746f20746865207a65726f20616460008301527f64726573730000000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000611ffb6032836126a0565b91507f455243313135353a207472616e736665722063616c6c6572206973206e6f742060008301527f6f776e6572206e6f7220617070726f76656400000000000000000000000000006020830152604082019050919050565b6000612061602a836126a0565b91507f455243313135353a20696e73756666696369656e742062616c616e636520666f60008301527f72207472616e73666572000000000000000000000000000000000000000000006020830152604082019050919050565b60006120c76029836126a0565b91507f455243313135353a2073657474696e6720617070726f76616c2073746174757360008301527f20666f722073656c6600000000000000000000000000000000000000000000006020830152604082019050919050565b600061212d6029836126a0565b91507f455243313135353a206163636f756e747320616e6420696473206c656e67746860008301527f206d69736d6174636800000000000000000000000000000000000000000000006020830152604082019050919050565b60006121936028836126a0565b91507f455243313135353a2069647320616e6420616d6f756e7473206c656e6774682060008301527f6d69736d617463680000000000000000000000000000000000000000000000006020830152604082019050919050565b60006121f96021836126a0565b91507f455243313135353a206d696e7420746f20746865207a65726f2061646472657360008301527f73000000000000000000000000000000000000000000000000000000000000006020830152604082019050919050565b61225b81612771565b82525050565b61226a81612771565b82525050565b60006020820190506122856000830184611d02565b92915050565b600060a0820190506122a06000830188611d02565b6122ad6020830187611d02565b81810360408301526122bf8186611d11565b905081810360608301526122d38185611d11565b905081810360808301526122e78184611d7e565b90509695505050505050565b600060a0820190506123086000830188611d02565b6123156020830187611d02565b6123226040830186612261565b61232f6060830185612261565b81810360808301526123418184611d7e565b90509695505050505050565b600060208201905081810360008301526123678184611d11565b905092915050565b600060408201905081810360008301526123898185611d11565b9050818103602083015261239d8184611d11565b90509392505050565b60006020820190506123bb6000830184611d6f565b92915050565b600060208201905081810360008301526123db8184611db7565b905092915050565b600060208201905081810360008301526123fc81611df0565b9050919050565b6000602082019050818103600083015261241c81611e56565b9050919050565b6000602082019050818103600083015261243c81611ebc565b9050919050565b6000602082019050818103600083015261245c81611f22565b9050919050565b6000602082019050818103600083015261247c81611f88565b9050919050565b6000602082019050818103600083015261249c81611fee565b9050919050565b600060208201905081810360008301526124bc81612054565b9050919050565b600060208201905081810360008301526124dc816120ba565b9050919050565b600060208201905081810360008301526124fc81612120565b9050919050565b6000602082019050818103600083015261251c81612186565b9050919050565b6000602082019050818103600083015261253c816121ec565b9050919050565b60006020820190506125586000830184612261565b92915050565b60006040820190506125736000830185612261565b6125806020830184612261565b9392505050565b6000604051905081810181811067ffffffffffffffff821117156125ae576125ad612896565b5b8060405250919050565b600067ffffffffffffffff8211156125d3576125d2612896565b5b602082029050602081019050919050565b600067ffffffffffffffff8211156125ff576125fe612896565b5b602082029050602081019050919050565b600067ffffffffffffffff82111561262b5761262a612896565b5b601f19601f8301169050602081019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b60006126bc82612771565b91506126c783612771565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff038211156126fc576126fb612838565b5b828201905092915050565b600061271282612751565b9050919050565b60008115159050919050565b60007fffffffff0000000000000000000000000000000000000000000000000000000082169050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b82818337600083830152505050565b60005b838110156127a857808201518184015260208101905061278d565b838111156127b7576000848401525b50505050565b600060028204905060018216806127d557607f821691505b602082108114156127e9576127e8612867565b5b50919050565b60006127fa82612771565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82141561282d5761282c612838565b5b600182019050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b6000601f19601f8301169050919050565b60008160e01c9050919050565b600060443d10156128f357612996565b60046000803e6129046000516128d6565b6308c379a081146129155750612996565b60405160043d036004823e80513d602482011167ffffffffffffffff8211171561294157505050612996565b808201805167ffffffffffffffff811115612960575050505050612996565b8060208301013d850181111561297b57505050505050612996565b612984826128c5565b60208401016040528296505050505050505b90565b6129a281612707565b81146129ad57600080fd5b50565b6129b981612719565b81146129c457600080fd5b50565b6129d081612725565b81146129db57600080fd5b50565b6129e781612771565b81146129f257600080fd5b5056fea2646970667358221220c3e4581941cb2031533e233696ecb4dc84fd27369ea697a00e7f3e196cfe9c4b64736f6c63430008000033";
    // 合约对应的abi
    String abi = "[{\"inputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"constructor\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"account\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"operator\",\"type\": \"address\"},{\"indexed\": false,\"internalType\": \"bool\",\"name\": \"approved\",\"type\": \"bool\"}],\"name\": \"ApprovalForAll\",\"type\": \"event\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"operator\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"from\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"to\",\"type\": \"address\"},{\"indexed\": false,\"internalType\": \"uint256[]\",\"name\": \"ids\",\"type\": \"uint256[]\"},{\"indexed\": false,\"internalType\": \"uint256[]\",\"name\": \"values\",\"type\": \"uint256[]\"}],\"name\": \"TransferBatch\",\"type\": \"event\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": true,\"internalType\": \"address\",\"name\": \"operator\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"from\",\"type\": \"address\"},{\"indexed\": true,\"internalType\": \"address\",\"name\": \"to\",\"type\": \"address\"},{\"indexed\": false,\"internalType\": \"uint256\",\"name\": \"id\",\"type\": \"uint256\"},{\"indexed\": false,\"internalType\": \"uint256\",\"name\": \"value\",\"type\": \"uint256\"}],\"name\": \"TransferSingle\",\"type\": \"event\"},{\"anonymous\": false,\"inputs\": [{\"indexed\": false,\"internalType\": \"string\",\"name\": \"value\",\"type\": \"string\"},{\"indexed\": true,\"internalType\": \"uint256\",\"name\": \"id\",\"type\": \"uint256\"}],\"name\": \"URI\",\"type\": \"event\"},{\"inputs\": [],\"name\": \"_owner\",\"outputs\": [{\"internalType\": \"address\",\"name\": \"\",\"type\": \"address\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"account\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"id\",\"type\": \"uint256\"}],\"name\": \"balanceOf\",\"outputs\": [{\"internalType\": \"uint256\",\"name\": \"\",\"type\": \"uint256\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address[]\",\"name\": \"accounts\",\"type\": \"address[]\"},{\"internalType\": \"uint256[]\",\"name\": \"ids\",\"type\": \"uint256[]\"}],\"name\": \"balanceOfBatch\",\"outputs\": [{\"internalType\": \"uint256[]\",\"name\": \"\",\"type\": \"uint256[]\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"account\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"operator\",\"type\": \"address\"}],\"name\": \"isApprovedForAll\",\"outputs\": [{\"internalType\": \"bool\",\"name\": \"\",\"type\": \"bool\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"_to\",\"type\": \"address\"},{\"internalType\": \"uint256[]\",\"name\": \"ids\",\"type\": \"uint256[]\"},{\"internalType\": \"uint256[]\",\"name\": \"amounts\",\"type\": \"uint256[]\"}],\"name\": \"mint\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"from\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"to\",\"type\": \"address\"},{\"internalType\": \"uint256[]\",\"name\": \"ids\",\"type\": \"uint256[]\"},{\"internalType\": \"uint256[]\",\"name\": \"amounts\",\"type\": \"uint256[]\"},{\"internalType\": \"bytes\",\"name\": \"data\",\"type\": \"bytes\"}],\"name\": \"safeBatchTransferFrom\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"from\",\"type\": \"address\"},{\"internalType\": \"address\",\"name\": \"to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"id\",\"type\": \"uint256\"},{\"internalType\": \"uint256\",\"name\": \"amount\",\"type\": \"uint256\"},{\"internalType\": \"bytes\",\"name\": \"data\",\"type\": \"bytes\"}],\"name\": \"safeTransferFrom\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"operator\",\"type\": \"address\"},{\"internalType\": \"bool\",\"name\": \"approved\",\"type\": \"bool\"}],\"name\": \"setApprovalForAll\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"bytes4\",\"name\": \"interfaceId\",\"type\": \"bytes4\"}],\"name\": \"supportsInterface\",\"outputs\": [{\"internalType\": \"bool\",\"name\": \"\",\"type\": \"bool\"}],\"stateMutability\": \"view\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"address\",\"name\": \"to\",\"type\": \"address\"},{\"internalType\": \"uint256\",\"name\": \"id\",\"type\": \"uint256\"},{\"internalType\": \"uint256\",\"name\": \"amount\",\"type\": \"uint256\"}],\"name\": \"transferArtNFT\",\"outputs\": [],\"stateMutability\": \"nonpayable\",\"type\": \"function\"},{\"inputs\": [{\"internalType\": \"uint256\",\"name\": \"\",\"type\": \"uint256\"}],\"name\": \"uri\",\"outputs\": [{\"internalType\": \"string\",\"name\": \"\",\"type\": \"string\"}],\"stateMutability\": \"view\",\"type\": \"function\"}]";

    /**
     * ERC1155合约部署，调用测试
     * @throws Exception 
     */
    @Test
    public void testERC1155() throws Exception {
    	
    	
    	// =======> step1： 为用户A和B生成私钥和地址
    	AccountInfo infoA = createAccount();
    	useraAddress = infoA.getAddress();
    	useraPrivateKey = infoA.getPrivateKey();
    	
    	AccountInfo infoB = createAccount();
    	userbAddress = infoB.getAddress();
    	userbPrivateKey = infoB.getPrivateKey();
    	
    	// =======>  step2: 通过管理员部署合约，部署好之后，合约就运行区块链内存中，后续可以直接调用，不用每次都调用部署合约这一步操作（除非业务上有需要）
        // 部署合约, 参数： 平行链合约名， 签名地址，签名私钥
        String hash = deployContract(paraName, managerAddress, managerPrivateKey);
        
        // 计算上一步部署到链上的合约地址
        String contractAddress = TransactionUtil.convertExectoAddr(managerAddress + hash.substring(2));
        System.out.println("部署好的合约地址 = " + contractAddress);
        
        // =======>  step3: 调用合约发行NFT,假设为2件游戏道具各生成100个NFT资产, id从10000开始        
        int lenght = 2;
        int[] ids = new int[lenght];
        int[] amounts = new int[lenght];
        for (int i = 0; i < lenght; i++) {
        	ids[i] = 10000 + i;
        	amounts[i] = 100;
        }
        
        // 代扣交易需要对平行链合约地址做一个处理
        String execer = paraName + "evm";
        // 平行链合约地址计算(平行链title前缀+合约名称)
        String paracontractAddress = client.convertExectoAddr(execer);
        
        // 构造合约调用, mint对应solidity合约里的方法名， useraAddress, ids, amounts这三项对应合约里的参数。  将NFT发行在用户A地址下
        byte[] initNFT = EvmUtil.encodeParameter(abi, "mint", useraAddress, ids, amounts);
    	// 构造发行NFT交易体，用户A对此笔交易签名：表示用户A有发行NFT的权限
    	String txEncode = EvmUtil.callEvmContractWithhold(initNFT,"", 0, execer, useraPrivateKey, contractAddress);
    	// 再调用代扣交易方法，用代扣私钥对交易组做签名
    	createNobalance(txEncode, paracontractAddress, useraPrivateKey, withholdPrivateKey);
        
        // =======>  查询用户A地址下的余额
        byte[] packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress, ids[0]);
        queryContract(packAbiGet, contractAddress, "转账前用户A,NFTID=" + ids[0] + "余额");
        
        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress, ids[1]);
        queryContract(packAbiGet, contractAddress, "转账前用户A,NFTID=" + ids[1] + "余额");
        
        // =======>  从A地址向B地址转账,使用代扣交易

        // 用户A将第1个NFT中的50个转给用户B
    	byte[] transfer = EvmUtil.encodeParameter(abi, "transferArtNFT", userbAddress, ids[0], 50);
    	// 构造转账交易体，先用用户A对此笔交易签名，
    	txEncode = EvmUtil.callEvmContractWithhold(transfer,"", 0, execer, useraPrivateKey, contractAddress);
    	// 再调用代扣交易方法，用代扣私钥对交易组做签名
    	createNobalance(txEncode, paracontractAddress, useraPrivateKey, withholdPrivateKey);

        
        // =======>  查询用户A地址下的余额
        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress, ids[0]);
        queryContract(packAbiGet, contractAddress, "转账后用户A,NFTID=" + ids[0] + "余额");
        
        // =======>  查询用户B地址下的余额
        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", userbAddress, ids[0]);
        queryContract(packAbiGet, contractAddress, "转账后用户B,NFTID=" + ids[0] + "余额");
        
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
        
        byte[] code = ByteUtil.merge(HexUtil.fromHexString(codes), abi.getBytes());
        
    	// TODO: 估算部署合约GAS费， 实际应用过程中，不建议在业务代码中直接调用gas费， 只是做预估使用。  实际可以在代码里设置一个大于这个值的数（合约部署手续费一般都高于合约调用，所以这边单独估算）
        String evmCode = EvmUtil.getCreateEvmEncode(code, "", "deploy ERC1155 contract", execer);
        long gas = client.queryEVMGas("evm", evmCode, address);
        System.out.println("Gas fee is:" + gas);
        
        // 通过合约code, 管理员私钥，平行链名称+evm,手续费等参数构造部署合约交易，并签名
        txEncode = EvmUtil.createEvmContract(code, "", "evm-sdk-test", privateKey, execer, gas);
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
