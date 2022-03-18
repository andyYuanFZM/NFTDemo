package com.chain33.cn.NFTDemo;

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
	String ip = "121.43.111.94";
	// 平行链服务端口
	int port = 8801;
	RpcClient client = new RpcClient(ip, port);
	
    // 平行链名称，固定格式user.p.xxxx.样例中使用的名称叫mbaas， 根据自己平行链名称变化。  这个名称一定要和平行链配置文件中的名称完全一致。
	String paraName = "user.p.mbaas.";

	// 合约部署人（管理员）地址和私钥,地址下需要有BTY来缴纳手续费
	// 生成方式参考下面testCreateAccount方法，私钥和地址一一对应
	String managerAddress = "1AKAm9vV6m4TbTzHKwirdygakF5HNus8Bg";
    String managerPrivateKey = "65f879c5a5d305b1710c7f46f1d027f4f2bf3c05d09178aa5b057d73b4cc54ca";
    
    // 用户手续费代扣地址和私钥,地址下需要有BTY来缴纳手续费
	// 生成方式参考下面testCreateAccount方法，私钥和地址一一对应
	String withholdAddress = "1L26eqrBgZanXqosSLrzM9ad77B6KwYZov";
    String withholdPrivateKey = "720093b1563200b6f30105d13198d262b91f31ae49616c947fe9cb5658bdd0ff";
    
    // 用户A地址和私钥
	String useraAddress;
    String useraPrivateKey;
    
    // 用户B地址和私钥
	String userbAddress;
    String userbPrivateKey;
    
    // solidity合约源码见：./solidity/ERC1155.sol
    // 合约编译出来的bytecode
    String codes = "60806040523480156200001157600080fd5b506040518060200160405280600081525062000033816200007b60201b60201c565b5033600360006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550620001ac565b80600290805190602001906200009392919062000097565b5050565b828054620000a59062000147565b90600052602060002090601f016020900481019282620000c9576000855562000115565b82601f10620000e457805160ff191683800117855562000115565b8280016001018555821562000115579182015b8281111562000114578251825591602001919060010190620000f7565b5b50905062000124919062000128565b5090565b5b808211156200014357600081600090555060010162000129565b5090565b600060028204905060018216806200016057607f821691505b602082108114156200017757620001766200017d565b5b50919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b612b5780620001bc6000396000f3fe608060405234801561001057600080fd5b50600436106100a85760003560e01c80639727756a116100715780639727756a14610189578063a22cb465146101a5578063ab918735146101c1578063b2bdfa7b146101dd578063e985e9c5146101fb578063f242432a1461022b576100a8565b8062fdd58e146100ad57806301ffc9a7146100dd5780630e89341c1461010d5780632eb2c2d61461013d5780634e1273f414610159575b600080fd5b6100c760048036038101906100c29190611afe565b610247565b6040516100d4919061224b565b60405180910390f35b6100f760048036038101906100f29190611c09565b610310565b604051610104919061208e565b60405180910390f35b61012760048036038101906101229190611c63565b6103f2565b60405161013491906120a9565b60405180910390f35b610157600480360381019061015291906118cd565b610486565b005b610173600480360381019061016e9190611b91565b610527565b6040516101809190612035565b60405180910390f35b6101a3600480360381019061019e9190611a33565b610640565b005b6101bf60048036038101906101ba9190611abe565b6106f0565b005b6101db60048036038101906101d69190611b3e565b610706565b005b6101e5610727565b6040516101f29190611f58565b60405180910390f35b6102156004803603810190610210919061188d565b61074d565b604051610222919061208e565b60405180910390f35b6102456004803603810190610240919061199c565b6107e1565b005b60008073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff1614156102b8576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102af9061210b565b60405180910390fd5b60008083815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b60007fd9b67a26000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff191614806103db57507f0e89341c000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916145b806103eb57506103ea82610882565b5b9050919050565b606060028054610401906124ba565b80601f016020809104026020016040519081016040528092919081815260200182805461042d906124ba565b801561047a5780601f1061044f5761010080835404028352916020019161047a565b820191906000526020600020905b81548152906001019060200180831161045d57829003601f168201915b50505050509050919050565b61048e6108ec565b73ffffffffffffffffffffffffffffffffffffffff168573ffffffffffffffffffffffffffffffffffffffff1614806104d457506104d3856104ce6108ec565b61074d565b5b610513576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161050a9061216b565b60405180910390fd5b61052085858585856108f4565b5050505050565b6060815183511461056d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610564906121eb565b60405180910390fd5b6000835167ffffffffffffffff81111561058a576105896125f3565b5b6040519080825280602002602001820160405280156105b85781602001602082028036833780820191505090505b50905060005b8451811015610635576106058582815181106105dd576105dc6125c4565b5b60200260200101518583815181106105f8576105f76125c4565b5b6020026020010151610247565b828281518110610618576106176125c4565b5b6020026020010181815250508061062e9061251d565b90506105be565b508091505092915050565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146106d0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016106c7906121ab565b60405180910390fd5b6106eb83838360405180602001604052806000815250610c08565b505050565b6107026106fb6108ec565b8383610e26565b5050565b61072233848484604051806020016040528060008152506107e1565b505050565b600360009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16905092915050565b6107e96108ec565b73ffffffffffffffffffffffffffffffffffffffff168573ffffffffffffffffffffffffffffffffffffffff16148061082f575061082e856108296108ec565b61074d565b5b61086e576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108659061212b565b60405180910390fd5b61087b8585858585610f93565b5050505050565b60007f01ffc9a7000000000000000000000000000000000000000000000000000000007bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916827bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916149050919050565b600033905090565b8151835114610938576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161092f9061220b565b60405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff1614156109a8576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161099f9061214b565b60405180910390fd5b60006109b26108ec565b90506109c2818787878787611215565b60005b8451811015610b735760008582815181106109e3576109e26125c4565b5b602002602001015190506000858381518110610a0257610a016125c4565b5b60200260200101519050600080600084815260200190815260200160002060008b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905081811015610aa3576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610a9a9061218b565b60405180910390fd5b81810360008085815260200190815260200160002060008c73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508160008085815260200190815260200160002060008b73ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254610b5891906123ae565b9250508190555050505080610b6c9061251d565b90506109c5565b508473ffffffffffffffffffffffffffffffffffffffff168673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167f4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb8787604051610bea929190612057565b60405180910390a4610c0081878787878761121d565b505050505050565b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff161415610c78576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610c6f9061222b565b60405180910390fd5b8151835114610cbc576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610cb39061220b565b60405180910390fd5b6000610cc66108ec565b9050610cd781600087878787611215565b60005b8451811015610d9057838181518110610cf657610cf56125c4565b5b6020026020010151600080878481518110610d1457610d136125c4565b5b6020026020010151815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254610d7691906123ae565b925050819055508080610d889061251d565b915050610cda565b508473ffffffffffffffffffffffffffffffffffffffff16600073ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff167f4a39dc06d4c0dbc64b70af90fd698a233a518aa5d07e595d983b8c0526c8f7fb8787604051610e08929190612057565b60405180910390a4610e1f8160008787878761121d565b5050505050565b8173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415610e95576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610e8c906121cb565b60405180910390fd5b80600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055508173ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167f17307eab39ab6107e8899845ad3d59bd9653f200f220920489ca2b5937696c3183604051610f86919061208e565b60405180910390a3505050565b600073ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff161415611003576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ffa9061214b565b60405180910390fd5b600061100d6108ec565b905061102d81878761101e88611404565b61102788611404565b87611215565b600080600086815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050838110156110c4576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016110bb9061218b565b60405180910390fd5b83810360008087815260200190815260200160002060008973ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508360008087815260200190815260200160002060008873ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825461117991906123ae565b925050819055508573ffffffffffffffffffffffffffffffffffffffff168773ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff167fc3d58168c5ae7397731d063d5bbf3d657854427343f4c083240f7aacaa2d0f6288886040516111f6929190612266565b60405180910390a461120c82888888888861147e565b50505050505050565b505050505050565b61123c8473ffffffffffffffffffffffffffffffffffffffff16611665565b156113fc578373ffffffffffffffffffffffffffffffffffffffff1663bc197c8187878686866040518663ffffffff1660e01b8152600401611282959493929190611f73565b602060405180830381600087803b15801561129c57600080fd5b505af19250505080156112cd57506040513d601f19601f820116820180604052508101906112ca9190611c36565b60015b611373576112d9612622565b806308c379a0141561133657506112ee612a2f565b806112f95750611338565b806040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161132d91906120a9565b60405180910390fd5b505b6040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161136a906120cb565b60405180910390fd5b63bc197c8160e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916817bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916146113fa576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016113f1906120eb565b60405180910390fd5b505b505050505050565b60606000600167ffffffffffffffff811115611423576114226125f3565b5b6040519080825280602002602001820160405280156114515781602001602082028036833780820191505090505b5090508281600081518110611469576114686125c4565b5b60200260200101818152505080915050919050565b61149d8473ffffffffffffffffffffffffffffffffffffffff16611665565b1561165d578373ffffffffffffffffffffffffffffffffffffffff1663f23a6e6187878686866040518663ffffffff1660e01b81526004016114e3959493929190611fdb565b602060405180830381600087803b1580156114fd57600080fd5b505af192505050801561152e57506040513d601f19601f8201168201806040525081019061152b9190611c36565b60015b6115d45761153a612622565b806308c379a01415611597575061154f612a2f565b8061155a5750611599565b806040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161158e91906120a9565b60405180910390fd5b505b6040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016115cb906120cb565b60405180910390fd5b63f23a6e6160e01b7bffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916817bffffffffffffffffffffffffffffffffffffffffffffffffffffffff19161461165b576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611652906120eb565b60405180910390fd5b505b505050505050565b600080823b905060008111915050919050565b600061168b611686846122b4565b61228f565b905080838252602082019050828560208602820111156116ae576116ad612649565b5b60005b858110156116de57816116c4888261179a565b8452602084019350602083019250506001810190506116b1565b5050509392505050565b60006116fb6116f6846122e0565b61228f565b9050808382526020820190508285602086028201111561171e5761171d612649565b5b60005b8581101561174e57816117348882611878565b845260208401935060208301925050600181019050611721565b5050509392505050565b600061176b6117668461230c565b61228f565b9050828152602081018484840111156117875761178661264e565b5b611792848285612478565b509392505050565b6000813590506117a981612ac5565b92915050565b600082601f8301126117c4576117c3612644565b5b81356117d4848260208601611678565b91505092915050565b600082601f8301126117f2576117f1612644565b5b81356118028482602086016116e8565b91505092915050565b60008135905061181a81612adc565b92915050565b60008135905061182f81612af3565b92915050565b60008151905061184481612af3565b92915050565b600082601f83011261185f5761185e612644565b5b813561186f848260208601611758565b91505092915050565b60008135905061188781612b0a565b92915050565b600080604083850312156118a4576118a3612658565b5b60006118b28582860161179a565b92505060206118c38582860161179a565b9150509250929050565b600080600080600060a086880312156118e9576118e8612658565b5b60006118f78882890161179a565b95505060206119088882890161179a565b945050604086013567ffffffffffffffff81111561192957611928612653565b5b611935888289016117dd565b935050606086013567ffffffffffffffff81111561195657611955612653565b5b611962888289016117dd565b925050608086013567ffffffffffffffff81111561198357611982612653565b5b61198f8882890161184a565b9150509295509295909350565b600080600080600060a086880312156119b8576119b7612658565b5b60006119c68882890161179a565b95505060206119d78882890161179a565b94505060406119e888828901611878565b93505060606119f988828901611878565b925050608086013567ffffffffffffffff811115611a1a57611a19612653565b5b611a268882890161184a565b9150509295509295909350565b600080600060608486031215611a4c57611a4b612658565b5b6000611a5a8682870161179a565b935050602084013567ffffffffffffffff811115611a7b57611a7a612653565b5b611a87868287016117dd565b925050604084013567ffffffffffffffff811115611aa857611aa7612653565b5b611ab4868287016117dd565b9150509250925092565b60008060408385031215611ad557611ad4612658565b5b6000611ae38582860161179a565b9250506020611af48582860161180b565b9150509250929050565b60008060408385031215611b1557611b14612658565b5b6000611b238582860161179a565b9250506020611b3485828601611878565b9150509250929050565b600080600060608486031215611b5757611b56612658565b5b6000611b658682870161179a565b9350506020611b7686828701611878565b9250506040611b8786828701611878565b9150509250925092565b60008060408385031215611ba857611ba7612658565b5b600083013567ffffffffffffffff811115611bc657611bc5612653565b5b611bd2858286016117af565b925050602083013567ffffffffffffffff811115611bf357611bf2612653565b5b611bff858286016117dd565b9150509250929050565b600060208284031215611c1f57611c1e612658565b5b6000611c2d84828501611820565b91505092915050565b600060208284031215611c4c57611c4b612658565b5b6000611c5a84828501611835565b91505092915050565b600060208284031215611c7957611c78612658565b5b6000611c8784828501611878565b91505092915050565b6000611c9c8383611f3a565b60208301905092915050565b611cb181612404565b82525050565b6000611cc28261234d565b611ccc818561237b565b9350611cd78361233d565b8060005b83811015611d08578151611cef8882611c90565b9750611cfa8361236e565b925050600181019050611cdb565b5085935050505092915050565b611d1e81612416565b82525050565b6000611d2f82612358565b611d39818561238c565b9350611d49818560208601612487565b611d528161265d565b840191505092915050565b6000611d6882612363565b611d72818561239d565b9350611d82818560208601612487565b611d8b8161265d565b840191505092915050565b6000611da360348361239d565b9150611dae8261267b565b604082019050919050565b6000611dc660288361239d565b9150611dd1826126ca565b604082019050919050565b6000611de9602b8361239d565b9150611df482612719565b604082019050919050565b6000611e0c60298361239d565b9150611e1782612768565b604082019050919050565b6000611e2f60258361239d565b9150611e3a826127b7565b604082019050919050565b6000611e5260328361239d565b9150611e5d82612806565b604082019050919050565b6000611e75602a8361239d565b9150611e8082612855565b604082019050919050565b6000611e9860238361239d565b9150611ea3826128a4565b604082019050919050565b6000611ebb60298361239d565b9150611ec6826128f3565b604082019050919050565b6000611ede60298361239d565b9150611ee982612942565b604082019050919050565b6000611f0160288361239d565b9150611f0c82612991565b604082019050919050565b6000611f2460218361239d565b9150611f2f826129e0565b604082019050919050565b611f438161246e565b82525050565b611f528161246e565b82525050565b6000602082019050611f6d6000830184611ca8565b92915050565b600060a082019050611f886000830188611ca8565b611f956020830187611ca8565b8181036040830152611fa78186611cb7565b90508181036060830152611fbb8185611cb7565b90508181036080830152611fcf8184611d24565b90509695505050505050565b600060a082019050611ff06000830188611ca8565b611ffd6020830187611ca8565b61200a6040830186611f49565b6120176060830185611f49565b81810360808301526120298184611d24565b90509695505050505050565b6000602082019050818103600083015261204f8184611cb7565b905092915050565b600060408201905081810360008301526120718185611cb7565b905081810360208301526120858184611cb7565b90509392505050565b60006020820190506120a36000830184611d15565b92915050565b600060208201905081810360008301526120c38184611d5d565b905092915050565b600060208201905081810360008301526120e481611d96565b9050919050565b6000602082019050818103600083015261210481611db9565b9050919050565b6000602082019050818103600083015261212481611ddc565b9050919050565b6000602082019050818103600083015261214481611dff565b9050919050565b6000602082019050818103600083015261216481611e22565b9050919050565b6000602082019050818103600083015261218481611e45565b9050919050565b600060208201905081810360008301526121a481611e68565b9050919050565b600060208201905081810360008301526121c481611e8b565b9050919050565b600060208201905081810360008301526121e481611eae565b9050919050565b6000602082019050818103600083015261220481611ed1565b9050919050565b6000602082019050818103600083015261222481611ef4565b9050919050565b6000602082019050818103600083015261224481611f17565b9050919050565b60006020820190506122606000830184611f49565b92915050565b600060408201905061227b6000830185611f49565b6122886020830184611f49565b9392505050565b60006122996122aa565b90506122a582826124ec565b919050565b6000604051905090565b600067ffffffffffffffff8211156122cf576122ce6125f3565b5b602082029050602081019050919050565b600067ffffffffffffffff8211156122fb576122fa6125f3565b5b602082029050602081019050919050565b600067ffffffffffffffff821115612327576123266125f3565b5b6123308261265d565b9050602081019050919050565b6000819050602082019050919050565b600081519050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600082825260208201905092915050565b60006123b98261246e565b91506123c48361246e565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff038211156123f9576123f8612566565b5b828201905092915050565b600061240f8261244e565b9050919050565b60008115159050919050565b60007fffffffff0000000000000000000000000000000000000000000000000000000082169050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b82818337600083830152505050565b60005b838110156124a557808201518184015260208101905061248a565b838111156124b4576000848401525b50505050565b600060028204905060018216806124d257607f821691505b602082108114156124e6576124e5612595565b5b50919050565b6124f58261265d565b810181811067ffffffffffffffff82111715612514576125136125f3565b5b80604052505050565b60006125288261246e565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82141561255b5761255a612566565b5b600182019050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b600060033d11156126415760046000803e61263e60005161266e565b90505b90565b600080fd5b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b60008160e01c9050919050565b7f455243313135353a207472616e7366657220746f206e6f6e204552433131353560008201527f526563656976657220696d706c656d656e746572000000000000000000000000602082015250565b7f455243313135353a204552433131353552656365697665722072656a6563746560008201527f6420746f6b656e73000000000000000000000000000000000000000000000000602082015250565b7f455243313135353a2062616c616e636520717565727920666f7220746865207a60008201527f65726f2061646472657373000000000000000000000000000000000000000000602082015250565b7f455243313135353a2063616c6c6572206973206e6f74206f776e6572206e6f7260008201527f20617070726f7665640000000000000000000000000000000000000000000000602082015250565b7f455243313135353a207472616e7366657220746f20746865207a65726f20616460008201527f6472657373000000000000000000000000000000000000000000000000000000602082015250565b7f455243313135353a207472616e736665722063616c6c6572206973206e6f742060008201527f6f776e6572206e6f7220617070726f7665640000000000000000000000000000602082015250565b7f455243313135353a20696e73756666696369656e742062616c616e636520666f60008201527f72207472616e7366657200000000000000000000000000000000000000000000602082015250565b7f6f6e6c7920617574686f72697a6564206f776e65722063616e206d696e74204e60008201527f46542e0000000000000000000000000000000000000000000000000000000000602082015250565b7f455243313135353a2073657474696e6720617070726f76616c2073746174757360008201527f20666f722073656c660000000000000000000000000000000000000000000000602082015250565b7f455243313135353a206163636f756e747320616e6420696473206c656e67746860008201527f206d69736d617463680000000000000000000000000000000000000000000000602082015250565b7f455243313135353a2069647320616e6420616d6f756e7473206c656e6774682060008201527f6d69736d61746368000000000000000000000000000000000000000000000000602082015250565b7f455243313135353a206d696e7420746f20746865207a65726f2061646472657360008201527f7300000000000000000000000000000000000000000000000000000000000000602082015250565b600060443d1015612a3f57612ac2565b612a476122aa565b60043d036004823e80513d602482011167ffffffffffffffff82111715612a6f575050612ac2565b808201805167ffffffffffffffff811115612a8d5750505050612ac2565b80602083010160043d038501811115612aaa575050505050612ac2565b612ab9826020018501866124ec565b82955050505050505b90565b612ace81612404565b8114612ad957600080fd5b50565b612ae581612416565b8114612af057600080fd5b50565b612afc81612422565b8114612b0757600080fd5b50565b612b138161246e565b8114612b1e57600080fd5b5056fea2646970667358221220af513c6ee19613db37bd87551ede35e71ef788ba8201a59fbf9a840c0d72571b64736f6c63430008070033";
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
        
        // 调用合约的手续费，可以固定设置一个较大的值，保证交易能成功，此处设置0.01个BTY
        long fee = 1000000;
        
        int lenght = 2;
        int[] ids = new int[lenght];
        int[] amounts = new int[lenght];
        for (int i = 0; i < lenght; i++) {
        	ids[i] = 10000 + i;
        	amounts[i] = 100;
        }
        // 构造合约调用, mint对应solidity合约里的方法名， useraAddress, ids, amounts这三项对应合约里的参数。  将NFT发行在用户A地址下
        byte[] initNFT = EvmUtil.encodeParameter(abi, "mint", useraAddress, ids, amounts);

        hash = callContract(initNFT, contractAddress, managerAddress, managerPrivateKey, paraName, fee);
        
        // =======>  查询用户A地址下的余额
        byte[] packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress, ids[0]);
        queryContract(packAbiGet, contractAddress, "转账前用户A,NFTID=" + ids[0] + "余额");
        
        packAbiGet = EvmUtil.encodeParameter(abi, "balanceOf", useraAddress, ids[1]);
        queryContract(packAbiGet, contractAddress, "转账前用户A,NFTID=" + ids[1] + "余额");
        
        // =======>  从A地址向B地址转账,使用代扣交易
        // 代扣交易需要对平行链合约地址做一个处理
        String execer = paraName + "evm";
        // 平行链合约地址计算(平行链title前缀+合约名称)
        String paracontractAddress = client.convertExectoAddr(execer);
        // 用户A将第1个NFT中的50个转给用户B
    	byte[] transfer = EvmUtil.encodeParameter(abi, "transferArtNFT", userbAddress, ids[0], 50);
    	// 构造转账交易体，先用用户A对此笔交易签名，
    	String txEncode = EvmUtil.callEvmContractWithhold(transfer,"", 0, execer, useraPrivateKey, contractAddress);
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
